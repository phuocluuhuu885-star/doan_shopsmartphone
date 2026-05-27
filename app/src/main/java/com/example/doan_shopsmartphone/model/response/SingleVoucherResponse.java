package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Voucher;

public class SingleVoucherResponse {
    private String message;
    private int code;
    private Voucher data;

    public SingleVoucherResponse(String message, int code, Voucher data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Voucher getData() {
        return data;
    }

    public void setData(Voucher data) {
        this.data = data;
    }
}
