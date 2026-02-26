package com.revpay.controller;

import com.revpay.dto.response.LoanApplicationResponse;
import com.revpay.dto.response.UserResponse;
import com.revpay.service.AdminService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminService.AdminDashboardStats> getDashboardStats() {
        logger.info("Admin dashboard stats request");
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoans() {
        logger.info("Admin get all loans request");
        return ResponseEntity.ok(adminService.getAllLoanApplications());
    }

    @GetMapping("/loans/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoansByStatus(@PathVariable String status) {
        logger.info("Admin get loans by status: {}", status);
        return ResponseEntity.ok(adminService.getLoanApplicationsByStatus(status));
    }

    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<LoanApplicationResponse> approveLoan(@PathVariable Long id) {
        logger.info("Admin approve loan: {}", id);
        return ResponseEntity.ok(adminService.approveLoan(id));
    }

    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<LoanApplicationResponse> rejectLoan(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        logger.info("Admin reject loan: {}", id);
        return ResponseEntity.ok(adminService.rejectLoan(id, reason));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Admin get all users request");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        logger.info("Admin get users by role: {}", role);
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }


}