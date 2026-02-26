package com.revpay.dto.response;


import java.math.BigDecimal;
import java.util.List;


public class AnalyticsDashboardResponse {

    // wallet summary
    private BigDecimal totalReceived;
    private BigDecimal totalSent;
    private BigDecimal currentBalance;

    // pending money requests
    private Long pendingRequestsCount;
    private BigDecimal pendingRequestsAmount;

    // revenue report
    private RevenueReportResponse revenueReport;

    // invoice summary
    private InvoiceSummaryResponse invoiceSummary;

    // top customers
    private List<TopCustomerResponse> topCustomers;

    public AnalyticsDashboardResponse(BigDecimal currentBalance, InvoiceSummaryResponse invoiceSummary, BigDecimal pendingRequestsAmount, Long pendingRequestsCount, RevenueReportResponse revenueReport, List<TopCustomerResponse> topCustomers, BigDecimal totalReceived, BigDecimal totalSent) {
        this.currentBalance = currentBalance;
        this.invoiceSummary = invoiceSummary;
        this.pendingRequestsAmount = pendingRequestsAmount;
        this.pendingRequestsCount = pendingRequestsCount;
        this.revenueReport = revenueReport;
        this.topCustomers = topCustomers;
        this.totalReceived = totalReceived;
        this.totalSent = totalSent;
    }

    public AnalyticsDashboardResponse() {
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public InvoiceSummaryResponse getInvoiceSummary() {
        return invoiceSummary;
    }

    public void setInvoiceSummary(InvoiceSummaryResponse invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }

    public BigDecimal getPendingRequestsAmount() {
        return pendingRequestsAmount;
    }

    public void setPendingRequestsAmount(BigDecimal pendingRequestsAmount) {
        this.pendingRequestsAmount = pendingRequestsAmount;
    }

    public Long getPendingRequestsCount() {
        return pendingRequestsCount;
    }

    public void setPendingRequestsCount(Long pendingRequestsCount) {
        this.pendingRequestsCount = pendingRequestsCount;
    }

    public List<TopCustomerResponse> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<TopCustomerResponse> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public RevenueReportResponse getRevenueReport() {
        return revenueReport;
    }

    public void setRevenueReport(RevenueReportResponse revenueReport) {
        this.revenueReport = revenueReport;
    }

    public BigDecimal getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(BigDecimal totalReceived) {
        this.totalReceived = totalReceived;
    }

    public BigDecimal getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(BigDecimal totalSent) {
        this.totalSent = totalSent;
    }

    @Override
    public String toString() {
        return "AnalyticsDashboardResponse{" +
                "currentBalance=" + currentBalance +
                ", totalReceived=" + totalReceived +
                ", totalSent=" + totalSent +
                ", pendingRequestsCount=" + pendingRequestsCount +
                ", pendingRequestsAmount=" + pendingRequestsAmount +
                ", revenueReport=" + revenueReport +
                ", invoiceSummary=" + invoiceSummary +
                ", topCustomers=" + topCustomers +
                '}';
    }
}