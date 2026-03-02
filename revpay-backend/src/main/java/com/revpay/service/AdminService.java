package com.revpay.service;

import com.revpay.dto.response.LoanApplicationResponse;
import com.revpay.dto.response.UserResponse;
import com.revpay.entity.LoanApplication;
import com.revpay.entity.User;
import com.revpay.enums.LoanStatus;
import com.revpay.enums.NotificationType;
import com.revpay.enums.Role;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.LoanApplicationRepository;
import com.revpay.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Logger logger = LogManager.getLogger(AdminService.class);

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoanService loanService;

    // verify caller is ADMIN
    private void requireAdmin() {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedAccessException("Admin access required");
        }
    }

    // get all loan applications (all users)
    public List<LoanApplicationResponse> getAllLoanApplications() {
        requireAdmin();
        logger.info("Admin fetching all loan applications");
        return loanApplicationRepository.findAll()
                .stream()
                .map(loanService::toLoanResponse)
                .collect(Collectors.toList());
    }

    // get loan applications by status
    public List<LoanApplicationResponse> getLoanApplicationsByStatus(String status) {
        requireAdmin();
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        logger.info("Admin fetching loans by status: {}", status);
        return loanApplicationRepository.findByStatus(loanStatus)
                .stream()
                .map(loanService::toLoanResponse)
                .collect(Collectors.toList());
    }

    // approve a loan application
    @Transactional
    public LoanApplicationResponse approveLoan(Long loanId) {
        requireAdmin();
        logger.info("Admin approving loan: {}", loanId);

        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        if (!loan.getStatus().equals(LoanStatus.PENDING)) {
            throw new UnauthorizedAccessException("Only PENDING loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loanApplicationRepository.save(loan);

        // notify the business user
        notificationService.sendNotification(
                loan.getUser(),
                "Your loan application of ₹" + loan.getLoanAmount() + " has been APPROVED! EMI: ₹" + loan.getEmiAmount() + "/month",
                NotificationType.LOAN
        );

        logger.info("Loan {} approved by admin", loanId);
        return loanService.toLoanResponse(loan);
    }

    // reject a loan application
    @Transactional
    public LoanApplicationResponse rejectLoan(Long loanId, String reason) {
        requireAdmin();
        logger.info("Admin rejecting loan: {}", loanId);

        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        if (!loan.getStatus().equals(LoanStatus.PENDING)) {
            throw new UnauthorizedAccessException("Only PENDING loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loanApplicationRepository.save(loan);

        // notify the business user
        String message = "Your loan application of ₹" + loan.getLoanAmount() + " has been REJECTED.";
        if (reason != null && !reason.isBlank()) {
            message += " Reason: " + reason;
        }

        notificationService.sendNotification(loan.getUser(), message, NotificationType.LOAN);

        logger.info("Loan {} rejected by admin", loanId);
        return loanService.toLoanResponse(loan);
    }

    // get all users
    public List<UserResponse> getAllUsers() {
        requireAdmin();
        logger.info("Admin fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // get users by role
    public List<UserResponse> getUsersByRole(String role) {
        requireAdmin();
        Role userRole = Role.valueOf(role.toUpperCase());
        logger.info("Admin fetching users by role: {}", role);
        return userRepository.findByRole(userRole)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // get dashboard stats
    public AdminDashboardStats getDashboardStats() {
        requireAdmin();
        long totalUsers = userRepository.count();
        long businessUsers = userRepository.findByRole(Role.BUSINESS).size();
        long personalUsers = userRepository.findByRole(Role.PERSONAL).size();
        long pendingLoans = loanApplicationRepository.findByStatus(LoanStatus.PENDING).size();
        long approvedLoans = loanApplicationRepository.findByStatus(LoanStatus.APPROVED).size();
        long rejectedLoans = loanApplicationRepository.findByStatus(LoanStatus.REJECTED).size();
        long totalLoans = loanApplicationRepository.count();

        return new AdminDashboardStats(
                totalUsers, businessUsers, personalUsers,
                totalLoans, pendingLoans, approvedLoans, rejectedLoans
        );
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setActive(user.isActive());
        response.setHasTransactionPin(user.getTransactionPin() != null);
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    // inner DTO class for dashboard stats
    public static class AdminDashboardStats {
        public long totalUsers;
        public long businessUsers;
        public long personalUsers;
        public long totalLoans;
        public long pendingLoans;
        public long approvedLoans;
        public long rejectedLoans;

        public AdminDashboardStats(long totalUsers, long businessUsers, long personalUsers,
                                   long totalLoans, long pendingLoans, long approvedLoans, long rejectedLoans) {
            this.totalUsers = totalUsers;
            this.businessUsers = businessUsers;
            this.personalUsers = personalUsers;
            this.totalLoans = totalLoans;
            this.pendingLoans = pendingLoans;
            this.approvedLoans = approvedLoans;
            this.rejectedLoans = rejectedLoans;
        }
    }
}