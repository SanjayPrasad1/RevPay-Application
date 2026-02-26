package com.revpay.service;

import com.revpay.entity.LoanApplication;
import com.revpay.entity.User;
import com.revpay.enums.Role;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.LoanApplicationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LoanDocumentService {

    private static final Logger logger = LogManager.getLogger(LoanDocumentService.class);

    // saves inside the project folder — works on both Windows and Linux
    private static final String UPLOAD_DIR = "loan-documents";

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserService userService;

    public String uploadDocument(Long loanId, MultipartFile file) {

        // validate user
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.BUSINESS)) {
            throw new UnauthorizedAccessException("Only business accounts can upload documents");
        }

        // validate loan ownership
        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This loan does not belong to you");
        }

        // validate file not empty
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        // validate file name
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // validate extension
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!extension.equals("pdf") && !extension.equals("jpg") &&
                !extension.equals("jpeg") && !extension.equals("png")) {
            throw new IllegalArgumentException("Only PDF, JPG, and PNG files are allowed");
        }

        // validate size — 5MB max
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }

        try {
            // create directory using File — works on Windows too
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Could not create upload directory: " + uploadDir.getAbsolutePath());
                }
            }

            // unique filename to avoid conflicts
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename
                    .replaceAll("[^a-zA-Z0-9._-]", "_"); // sanitize filename

            Path targetPath = Paths.get(UPLOAD_DIR, uniqueFilename);

            // copy file to disk
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // save filename to DB
            loan.setDocumentPath(uniqueFilename);
            loanApplicationRepository.save(loan);

            logger.info("Document uploaded for loan {} by user {}: {}", loanId, user.getEmail(), uniqueFilename);
            return "Document uploaded successfully";

        } catch (IOException e) {
            logger.error("File upload failed for loan {}: {}", loanId, e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public String getDocumentPath(Long loanId) {
        User user = userService.getLoggedInUser();

        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        boolean isAdmin = user.getRole().equals(Role.ADMIN);
        boolean isOwner = loan.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("You cannot access this document");
        }

        if (loan.getDocumentPath() == null || loan.getDocumentPath().isBlank()) {
            throw new ResourceNotFoundException("No document uploaded for this loan");
        }

        return loan.getDocumentPath();
    }
}