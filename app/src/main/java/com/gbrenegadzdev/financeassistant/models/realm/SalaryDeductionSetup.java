package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalaryDeductionSetup extends RealmObject {
    public static final String SALARY_DEDUCTION_ID = "salaryDeductionId";
    public static final String DEDUCTION_NAME = "deductionName";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String MODIFIED_DATETIME = "modifiedDatetime";
    public static final String IS_SELECTED = "isSelected";

    @PrimaryKey
    private String salaryDeductionId;
    private String deductionName;
    private Date createdDatetime;
    private Date modifiedDatetime;
    private boolean isSelected;

    public String getSalaryDeductionId() {
        return salaryDeductionId;
    }

    public void setSalaryDeductionId(String salaryDeductionId) {
        this.salaryDeductionId = salaryDeductionId;
    }

    public String getDeductionName() {
        return deductionName;
    }

    public void setDeductionName(String deductionName) {
        this.deductionName = deductionName;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "SalaryDeductionSetup{" +
                "salaryDeductionId='" + salaryDeductionId + '\'' +
                ", deductionName='" + deductionName + '\'' +
                ", createdDatetime=" + createdDatetime +
                ", modifiedDatetime=" + modifiedDatetime +
                ", isSelected=" + isSelected +
                '}';
    }
}
