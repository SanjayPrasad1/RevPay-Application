package com.revpay.dto.response;


import java.math.BigDecimal;


public class InvoiceSummaryResponse {

    private Long totalInvoices;
    private Long paidInvoices;
    private Long unpaidInvoices;
    private Long overdueInvoices;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalOutstandingAmount;

    public InvoiceSummaryResponse(Long totalInvoices, Long paidInvoices, Long unpaidInvoices, Long overdueInvoices, BigDecimal totalPaidAmount, BigDecimal totalOutstandingAmount) {
        this.overdueInvoices = overdueInvoices;
        this.paidInvoices = paidInvoices;
        this.totalInvoices = totalInvoices;
        this.totalOutstandingAmount = totalOutstandingAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.unpaidInvoices = unpaidInvoices;
    }

    public InvoiceSummaryResponse() {
    }

    public Long getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(Long overdueInvoices) {
        this.overdueInvoices = overdueInvoices;
    }

    public Long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(Long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public Long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public BigDecimal getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

    public void setTotalOutstandingAmount(BigDecimal totalOutstandingAmount) {
        this.totalOutstandingAmount = totalOutstandingAmount;
    }

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public Long getUnpaidInvoices() {
        return unpaidInvoices;
    }

    public void setUnpaidInvoices(Long unpaidInvoices) {
        this.unpaidInvoices = unpaidInvoices;
    }

    @Override
    public String toString() {
        return "InvoiceSummaryResponse{" +
                "overdueInvoices=" + overdueInvoices +
                ", totalInvoices=" + totalInvoices +
                ", paidInvoices=" + paidInvoices +
                ", unpaidInvoices=" + unpaidInvoices +
                ", totalPaidAmount=" + totalPaidAmount +
                ", totalOutstandingAmount=" + totalOutstandingAmount +
                '}';
    }
}