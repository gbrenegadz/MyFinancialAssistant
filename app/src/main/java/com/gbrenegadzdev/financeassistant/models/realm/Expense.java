package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Expense extends RealmObject {
    public static final String EXPENSE_ID = "expenseId";
    public static final String EXPENSE_NAME = "expenseName";
    public static final String AMOUNT = "amount";
    public static final String PAID_TO = "paidTo";
    public static final String CATEGORY = "category";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String MODIFIED_DATETIME = "modifiedDatetime";

    @PrimaryKey
    private String expenseId;
    private String expenseName;
    private double amount;
    private String paidTo;
    private String category;
    private String month;
    private int year;
    private Date createdDatetime;
    private Date modifiedDatetime;

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(String paidTo) {
        this.paidTo = paidTo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return "Expense{" +
                "expenseId='" + expenseId + '\'' +
                ", expenseName='" + expenseName + '\'' +
                ", amount=" + amount +
                ", paidTo='" + paidTo + '\'' +
                ", category='" + category + '\'' +
                ", month='" + month + '\'' +
                ", year=" + year +
                ", createdDatetime=" + createdDatetime +
                ", modifiedDatetime=" + modifiedDatetime +
                '}';
    }
}
