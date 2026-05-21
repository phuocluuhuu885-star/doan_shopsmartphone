package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderResult implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("app_trans_id")
    private String appTransId;

    @SerializedName("savedOrder")
    private OrderResult savedOrder;

    // Getter cho app_trans_id
    public String getAppTransId() {
        if (appTransId != null) {
            return appTransId;
        }
        if (savedOrder != null) {
            return savedOrder.getAppTransId();
        }
        return null;
    }

    public String getId() {
        if (id != null) {
            return id;
        }
        if (savedOrder != null) {
            return savedOrder.getId();
        }
        return null;
    }
}