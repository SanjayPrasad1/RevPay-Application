package com.revpay.dto.response;


import java.math.BigDecimal;


public class RevenueReportResponse {

    private BigDecimal daily;
    private BigDecimal weekly;
    private BigDecimal monthly;

    public RevenueReportResponse(BigDecimal daily, BigDecimal monthly, BigDecimal weekly) {
        this.daily = daily;
        this.monthly = monthly;
        this.weekly = weekly;
    }

    public RevenueReportResponse() {
    }

    public BigDecimal getDaily() {
        return daily;
    }

    public void setDaily(BigDecimal daily) {
        this.daily = daily;
    }

    public BigDecimal getMonthly() {
        return monthly;
    }

    public void setMonthly(BigDecimal monthly) {
        this.monthly = monthly;
    }

    public BigDecimal getWeekly() {
        return weekly;
    }

    public void setWeekly(BigDecimal weekly) {
        this.weekly = weekly;
    }

    @Override
    public String toString() {
        return "RevenueReportResponse{" +
                "daily=" + daily +
                ", weekly=" + weekly +
                ", monthly=" + monthly +
                '}';
    }
}