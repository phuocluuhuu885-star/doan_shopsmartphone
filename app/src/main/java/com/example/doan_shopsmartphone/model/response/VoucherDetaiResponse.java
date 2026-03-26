package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.VoucherDetail;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherDetaiResponse {
    String message;
    int code;
    @SerializedName("data")
    List<VoucherDetail> result;

    public VoucherDetaiResponse(String message, int code, List<VoucherDetail> result) {
        this.message = message;
        this.code = code;
        this.result = result;
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

    public List<VoucherDetail> getResult() {
        return result;
    }

    public void setResult(List<VoucherDetail> result) {
        this.result = result;
    }
}
