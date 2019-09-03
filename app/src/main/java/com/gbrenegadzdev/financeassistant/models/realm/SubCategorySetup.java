package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;

public class SubCategorySetup extends RealmObject {
    public static final String SUB_CATEGORY_ID = "subCategoryId";
    public static final String SUB_CATEGORY_NAME = "subCategoryName";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String IS_EDITABLE = "isEditable";
    public static final String IS_DELETABLE = "isDeletable";
    public static final String IS_SHOWN = "isShown";

    private String subCategoryId;
    private String subCategoryName;
    private Date createdDatetime;
    private boolean isEditable;
    private boolean isDeletable;
    private boolean isShown;

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    @Override
    public String toString() {
        return "SubCategorySetup{" +
                "subCategoryId='" + subCategoryId + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", createdDatetime=" + createdDatetime +
                ", isEditable=" + isEditable +
                ", isDeletable=" + isDeletable +
                ", isShown=" + isShown +
                '}';
    }
}
