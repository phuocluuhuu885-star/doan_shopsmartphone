package com.example.doan_shopsmartphone.model.body;

public class NotificationBody {
    private String sender_id;
    private String receiver_id;
    private String order_id;
    private String content;
    private String type;

    public NotificationBody(String sender_id, String receiver_id, String order_id, String content, String type) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.order_id = order_id;
        this.content = content;
        this.type = type;
    }
}