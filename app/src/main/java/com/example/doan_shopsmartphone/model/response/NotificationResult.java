package com.example.doan_shopsmartphone.model.response;

import com.google.gson.annotations.SerializedName;

public class NotificationResult {
    @SerializedName("_id")
    private String id;
    private String sender_id;
    private String receiver_id;
    private String order_id;
    private String content;
    private String status;
    private String type;

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getOrderId() { return order_id; }
}