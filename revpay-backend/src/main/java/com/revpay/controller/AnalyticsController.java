package com.revpay.controller;

import com.revpay.dto.response.AnalyticsDashboardResponse;
import com.revpay.dto.response.InvoiceSummaryResponse;
import com.revpay.dto.response.RevenueReportResponse;
import com.revpay.dto.response.TopCustomerResponse;
import com.revpay.service.AnalyticsService;
import com.revpay.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger logger = LogManager.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    // main dashboard - all metrics in one call
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard() {
        logger.info("Analytics dashboard request received");
        return ResponseEntity.ok(analyticsService.getDashboard());
    }

    // revenue report only
    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResponse> getRevenueReport() {
        logger.info("Revenue report request received");
        return ResponseEntity.ok(
                analyticsService.getRevenueReport(userService.getLoggedInUser()));
    }

    // invoice summary only
    @GetMapping("/invoices/summary")
    public ResponseEntity<InvoiceSummaryResponse> getInvoiceSummary() {
        logger.info("Invoice summary request received");
        return ResponseEntity.ok(
                analyticsService.getInvoiceSummary(userService.getLoggedInUser()));
    }

    // top customers only
    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers() {
        logger.info("Top customers request received");
        return ResponseEntity.ok(
                analyticsService.getTopCustomers(userService.getLoggedInUser()));
    }
}