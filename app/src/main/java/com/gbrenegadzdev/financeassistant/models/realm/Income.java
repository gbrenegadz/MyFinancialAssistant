package com.gbrenegadzdev.financeassistant.models.realm;

import java.lang.ref.SoftReference;
import java.util.Date;

import io.realm.RealmObject;

public class Income extends RealmObject {
    public static final String INCOME_ID = "incomeId";
    public static final String INCOME_NAME = "incomeName";
    public static final String AMOUNT = "amount";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String MODIFIED_DATETIME = "modifiedDatetime";

    private String incomeId;
    private String incomeName;
    private double amount;
    private String month;
    private int year;
    private Date createdDatetime;
    private Date modifiedDatetime;

    public String getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(String incomeId) {
        this.incomeId = incomeId;
    }

    public String getIncomeName() {
        return incomeName;
    }

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Date getModifiedDatetime() {
        return modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    @Override
    public String toString() {
        return "Income{" +
                "incomeId='" + incomeId + '\'' +
                ", incomeName='" + incomeName + '\'' +
                ", amount=" + amount +
                ", month='" + month + '\'' +
                ", year=" + year +
                ", createdDatetime=" + createdDatetime +
                ", modifiedDatetime=" + modifiedDatetime +
                '}';
    }
}
