package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.OrderResult;

public class ServerResponse {
    private int code;
    private String message;
    private OrderResult result;

    public ServerResponse() {
    }

    public ServerResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + (result != null ? result.toString() : "null") +
                '}';
    }
    public OrderResult getResult() { return result; }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
