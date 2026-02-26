package com.revpay.service;

import com.revpay.dto.response.*;
import com.revpay.entity.User;
import com.revpay.entity.Wallet;
import com.revpay.enums.InvoiceStatus;
import com.revpay.enums.RequestStatus;
import com.revpay.enums.Role;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private static final Logger logger = LogManager.getLogger(AnalyticsService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MoneyRequestRepository moneyRequestRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    // main dashboard - returns everything in one call
    public AnalyticsDashboardResponse getDashboard() {
        User user = getBusinessUser();
        logger.info("Fetching analytics dashboard for: {}", user.getEmail());

        AnalyticsDashboardResponse dashboard = new AnalyticsDashboardResponse();

        // wallet summary
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        dashboard.setCurrentBalance(wallet.getBalance());
        dashboard.setTotalReceived(transactionRepository.getTotalReceived(user));
        dashboard.setTotalSent(transactionRepository.getTotalSent(user));

        // pending requests summary
        dashboard.setPendingRequestsCount(
                moneyRequestRepository.countByRecipientAndStatus(user, RequestStatus.PENDING));
        dashboard.setPendingRequestsAmount(
                moneyRequestRepository.getTotalPendingAmount(user));

        // revenue report
        dashboard.setRevenueReport(getRevenueReport(user));

        // invoice summary
        dashboard.setInvoiceSummary(getInvoiceSummary(user));

        // top customers
        dashboard.setTopCustomers(getTopCustomers(user));

        logger.info("Analytics dashboard fetched successfully for: {}", user.getEmail());
        return dashboard;
    }

    // revenue breakdown - daily, weekly, monthly
    public RevenueReportResponse getRevenueReport(User user) {
        LocalDateTime now = LocalDateTime.now();

        BigDecimal daily = transactionRepository.getTotalReceivedAfter(
                user, now.minusDays(1));

        BigDecimal weekly = transactionRepository.getTotalReceivedAfter(
                user, now.minusDays(7));

        BigDecimal monthly = transactionRepository.getTotalReceivedAfter(
                user, now.minusDays(30));

        return new RevenueReportResponse(daily, weekly, monthly);
    }

    // invoice summary breakdown
    public InvoiceSummaryResponse getInvoiceSummary(User user) {
        long total = invoiceRepository.findByBusinessUserOrderByCreatedAtDesc(user).size();
        long paid = invoiceRepository.countByBusinessUserAndStatus(user, InvoiceStatus.PAID);
        long overdue = invoiceRepository.countByBusinessUserAndStatus(user, InvoiceStatus.OVERDUE);

        // unpaid = everything that is not paid or cancelled
        long unpaid = invoiceRepository.countByBusinessUserAndStatus(user, InvoiceStatus.SENT)
                + overdue
                + invoiceRepository.countByBusinessUserAndStatus(user, InvoiceStatus.DRAFT);

        BigDecimal totalPaid = invoiceRepository.getTotalPaidAmount(user);
        BigDecimal totalOutstanding = invoiceRepository.getTotalOutstandingAmount(user);

        return new InvoiceSummaryResponse(total, paid, unpaid, overdue, totalPaid, totalOutstanding);
    }

    // top customers by transaction volume
    public List<TopCustomerResponse> getTopCustomers(User user) {
        List<Object[]> results = transactionRepository.findTopCustomers(user);
        List<TopCustomerResponse> topCustomers = new ArrayList<>();

        for (Object[] row : results) {
            TopCustomerResponse customer = new TopCustomerResponse();
            customer.setCustomerName((String) row[0]);
            customer.setCustomerEmail((String) row[1]);
            customer.setTotalTransactionVolume((BigDecimal) row[2]);
            customer.setTransactionCount((Long) row[3]);
            topCustomers.add(customer);
        }

        return topCustomers;
    }

    // ---- helper methods ----

    private User getBusinessUser() {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.BUSINESS)) {
            throw new UnauthorizedAccessException("Only business accounts can access this feature");
        }
        return user;
    }
}