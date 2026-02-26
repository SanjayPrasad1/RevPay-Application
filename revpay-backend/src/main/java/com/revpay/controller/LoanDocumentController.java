package com.revpay.controller;

import com.revpay.service.LoanDocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/loans")
public class LoanDocumentController {

    private static final Logger logger = LogManager.getLogger(LoanDocumentController.class);

    @Autowired
    private LoanDocumentService loanDocumentService;

    @PostMapping("/{id}/upload-document")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        logger.info("Document upload request for loan: {}", id);
        String message = loanDocumentService.uploadDocument(id, file);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<String> getDocumentPath(@PathVariable Long id) {
        logger.info("Get document path for loan: {}", id);
        String path = loanDocumentService.getDocumentPath(id);
        return ResponseEntity.ok(path);
    }
    @GetMapping("/{id}/download-document")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id) throws IOException {

        String filename = loanDocumentService.getDocumentPath(id);
        Path filePath = Paths.get("loan-documents").resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}