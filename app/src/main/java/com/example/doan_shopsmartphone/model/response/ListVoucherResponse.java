package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Voucher;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListVoucherResponse {
    private int code;
    private String message;
    private List<Voucher> data;

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

    public List<Voucher> getData() {
        return data;
    }

    public void setData(List<Voucher> data) {
        this.data = data;
    }
}
