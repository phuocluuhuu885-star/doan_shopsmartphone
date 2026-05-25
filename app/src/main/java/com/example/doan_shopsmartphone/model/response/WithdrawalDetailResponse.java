package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Withdrawal;

public class WithdrawalDetailResponse {
    private int code;
    private String message;
    private Withdrawal data;

    public WithdrawalDetailResponse() {}

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

    public Withdrawal getData() {
        return data;
    }

    public void setData(Withdrawal data) {
        this.data = data;
    }
}
