package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Voucher implements Serializable {
    @SerializedName("_id")
    private String _id;
    private String code;
    private String title;
    private int discountType; // 1: %, 2: Tiền mặt
    private int discountValue;
    private List<String> applicableProducts; // Danh sách ID sản phẩm được áp dụng
    private int minOrderValue;
    private int maxDiscountValue;

    public Voucher(String _id, String code, String title, int discountType, int discountValue, List<String> applicableProducts, int minOrderValue, int maxDiscountValue) {
        this._id = _id;
        this.code = code;
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.applicableProducts = applicableProducts;
        this.minOrderValue = minOrderValue;
        this.maxDiscountValue = maxDiscountValue;
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

    public List<String> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<String> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public int getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(int minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public int getMaxDiscountValue() {
        return maxDiscountValue;
    }

    public void setMaxDiscountValue(int maxDiscountValue) {
        this.maxDiscountValue = maxDiscountValue;
    }
}
