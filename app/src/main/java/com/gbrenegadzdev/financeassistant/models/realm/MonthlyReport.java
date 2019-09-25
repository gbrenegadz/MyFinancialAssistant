package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;

public class MonthlyReport extends RealmObject {
    public static final String MONTHLY_REPORT_ID = "monthlyReportId";
    public static final String REPORT_TYPE = "reportType";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String AMOUNT = "amount"; // Update the value whenever income or expense is done
    public static final String CREATED_DATETIME = "createdDatetime";

    private String monthlyReportId;
    private int reportType;
    private int year;
    private String month;
    private double amount;
    private Date createdDatetime;

    public String getMonthlyReportId() {
        return monthlyReportId;
    }

    public void setMonthlyReportId(String monthlyReportId) {
        this.monthlyReportId = monthlyReportId;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
        return "MonthlyReport{" +
                "monthlyReportId='" + monthlyReportId + '\'' +
                ", reportType=" + reportType +
                ", year=" + year +
                ", month='" + month + '\'' +
                ", amount=" + amount +
                ", createdDatetime=" + createdDatetime +
                '}';
    }
}
