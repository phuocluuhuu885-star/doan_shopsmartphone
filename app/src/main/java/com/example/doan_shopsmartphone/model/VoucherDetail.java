package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class VoucherDetail {
    @SerializedName("_id")
    private String _id;
    private String code;
    private String title;
    private int quantity;
    private int discountType; // 1: %, 2: Tiền mặt
    private int discountValue;

    public VoucherDetail(String _id, String code, String title, int quantity, int discountType, int discountValue) {
        this._id = _id;
        this.code = code;
        this.title = title;
        this.quantity = quantity;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }
}
