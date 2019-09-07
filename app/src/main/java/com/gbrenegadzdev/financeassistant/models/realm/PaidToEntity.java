package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.RealmObject;

public class PaidToEntity extends RealmObject {
    public static final String PAID_TO_ENTITY_ID = "paidToId";
    public static final String PAID_TO_ENTITY_NAME = "paidToEntityName";
    public static final String CREATED_DATETIME = "createdDatetime";

    private String paidToId;
    private String paidToEntityName;
    private Date createdDatetime;

    public String getPaidToId() {
        return paidToId;
    }

    public void setPaidToId(String paidToId) {
        this.paidToId = paidToId;
    }

    public String getPaidToEntityName() {
        return paidToEntityName;
    }

    public void setPaidToEntityName(String paidToEntityName) {
        this.paidToEntityName = paidToEntityName;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
        return "PaidToEntity{" +
                "paidToId='" + paidToId + '\'' +
                ", paidToEntityName='" + paidToEntityName + '\'' +
                ", createdDatetime=" + createdDatetime +
                '}';
    }
}
