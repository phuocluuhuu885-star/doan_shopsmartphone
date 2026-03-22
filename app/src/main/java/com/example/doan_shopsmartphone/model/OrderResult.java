package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderResult implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("app_trans_id")
    private String appTransId;

    // Getter cho app_trans_id
    public String getAppTransId() {
        return appTransId;
    }

    public String getId() {
        return id;
    }
}