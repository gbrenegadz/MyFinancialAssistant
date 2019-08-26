package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;

public class Budget extends RealmObject {
    public static final String BUDGET_ID = "budgetId";
    public static final String BUDGET_NAME = "budgetName";
    public static final String AMOUNT = "amount";
    public static final String CATEGORY = "category";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String MODIFIED_DATETIME = "modifiedDatetime";

    private String budgetId;
    private String budgetName;
    private double amount;
    private String category;
    private String month;
    private int year;
    private Date createdDatetime;
    private Date modifiedDatetime;

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
        return "Budget{" +
                "budgetId='" + budgetId + '\'' +
                ", budgetName='" + budgetName + '\'' +
                ", amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", month='" + month + '\'' +
                ", year=" + year +
                ", createdDatetime=" + createdDatetime +
                ", modifiedDatetime=" + modifiedDatetime +
                '}';
    }
}
