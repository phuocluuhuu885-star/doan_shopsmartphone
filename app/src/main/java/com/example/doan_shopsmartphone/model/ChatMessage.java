package com.example.doan_shopsmartphone.model;

import java.io.Serializable;

public class ChatMessage {
    private String senderId;
    private String content;
    private long timestamp;

    public ChatMessage() {} // Bắt buộc

    public ChatMessage(String senderId, String content, long timestamp) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }
    // Getter/Setter...


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}