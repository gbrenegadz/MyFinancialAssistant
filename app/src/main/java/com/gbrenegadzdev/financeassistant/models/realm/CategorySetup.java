package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CategorySetup extends RealmObject {
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CREATED_DATETIME = "createdDatetime";
    public static final String IS_EDITABLE = "isEditable";
    public static final String IS_DELETABLE = "isDeletable";
    public static final String SUB_CATEGORY_LIST = "subCategoryList";

    @PrimaryKey
    private String categoryId;
    private String categoryName;
    private Date createdDatetime;
    private boolean isEditable;
    private boolean isDeletable;
    private RealmList<SubCategorySetup> subCategoryList;

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

    public RealmList<SubCategorySetup> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(RealmList<SubCategorySetup> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    @Override
    public String toString() {
        return "CategorySetup{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", createdDatetime=" + createdDatetime +
                ", isEditable=" + isEditable +
                ", isDeletable=" + isDeletable +
                ", \n\tsubCategoryList=" + subCategoryList.toString() +
                '}';
    }
}
