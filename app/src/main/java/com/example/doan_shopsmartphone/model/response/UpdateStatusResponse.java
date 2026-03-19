package com.example.doan_shopsmartphone.model.response;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    // "result" từ server (updateOne) thường chứa thông tin như nModified, acknowledged
    @SerializedName("result")
    private Object result; 

    // Getter và Setter
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
}