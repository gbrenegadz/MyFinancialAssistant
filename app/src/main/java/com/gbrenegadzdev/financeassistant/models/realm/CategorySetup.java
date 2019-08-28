package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;

public class CategorySetup extends RealmObject {
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String IS_EDITABLE = "isEditable";
    public static final String IS_DELETABLE = "isDeletable";

    private String categoryId;
    private String categoryName;
    private Date createdDatetime;
    private boolean isEditable;
    private boolean isDeletable;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    @Override
    public String toString() {
        return "CategorySetup{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", createdDatetime=" + createdDatetime +
                ", isEditable=" + isEditable +
                ", isDeletable=" + isDeletable +
                '}';
    }
}
