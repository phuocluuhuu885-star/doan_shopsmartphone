package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Notifi;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    private int code;
    private String message;
    private Notifi result;

    // Getter và Setter
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Notifi getResult() { return result; }
    public void setResult(Notifi result) { this.result = result; }
}
