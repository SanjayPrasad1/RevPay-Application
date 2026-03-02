package com.revpay.dto.response;


import java.math.BigDecimal;


public class TopCustomerResponse {

    private String customerName;
    private String customerEmail;
    private BigDecimal totalTransactionVolume;
    private Long transactionCount;

    public TopCustomerResponse(String customerEmail, String customerName, Long transactionCount, BigDecimal totalTransactionVolume) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.transactionCount = transactionCount;
        this.totalTransactionVolume = totalTransactionVolume;
    }

    public TopCustomerResponse() {
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalTransactionVolume() {
        return totalTransactionVolume;
    }

    public void setTotalTransactionVolume(BigDecimal totalTransactionVolume) {
        this.totalTransactionVolume = totalTransactionVolume;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    @Override
    public String toString() {
        return "TopCustomerResponse{" +
                "customerEmail='" + customerEmail + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalTransactionVolume=" + totalTransactionVolume +
                ", transactionCount=" + transactionCount +
                '}';
    }
}