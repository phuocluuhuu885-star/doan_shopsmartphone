package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Withdrawal;
import java.util.List;

public class WithdrawalListResponse {
    private int code;
    private String message;
    private List<Withdrawal> data;

    public WithdrawalListResponse() {}

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

    public List<Withdrawal> getData() {
        return data;
    }

    public void setData(List<Withdrawal> data) {
        this.data = data;
    }
}
