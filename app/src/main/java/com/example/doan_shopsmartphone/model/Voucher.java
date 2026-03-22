package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Voucher implements Serializable, Cloneable {
    @SerializedName("_id")
    private String _id;
    private String code;
    private String title;
    private int quantity;
    private int discountType; // 1: %, 2: Tiền mặt
    private int discountValue;

    // Đổi từ List<Object> thành List<ProductObj> để Mapping đúng JSON
    private List<ProductObj> applicableProducts;

    private int minOrderValue;
    private boolean selected;
    private int maxDiscountValue;
    private String expiryDate;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    // 1. Constructor sao chép (Copy Constructor) - Cực kỳ quan trọng để clone dữ liệu
    public Voucher(Voucher other) {
        this._id = other._id;
        this.code = other.code;
        this.title = other.title;
        this.quantity = other.quantity;
        this.discountType = other.discountType;
        this.discountValue = other.discountValue;
        this.minOrderValue = other.minOrderValue;
        this.maxDiscountValue = other.maxDiscountValue;
        this.selected = other.selected;
        // Sao chép danh sách sản phẩm
        if (other.applicableProducts != null) {
            this.applicableProducts = new ArrayList<>(other.applicableProducts);
        }
        this.expiryDate = other.expiryDate;
        this.status = other.status;
    }

    public Voucher() {
    }

    // 2. Lớp nội bộ đại diện cho đối tượng Sản phẩm trong mảng JSON
    public static class ProductObj implements Serializable {
        @SerializedName("_id")
        private String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }

    // Getter và Setter


    public int getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDiscountType() { return discountType; }
    public void setDiscountType(int discountType) { this.discountType = discountType; }

    public int getDiscountValue() { return discountValue; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }

    public List<ProductObj> getApplicableProducts() {
        return applicableProducts == null ? new ArrayList<>() : applicableProducts;
    }
    public void setApplicableProducts(List<ProductObj> applicableProducts) { this.applicableProducts = applicableProducts; }

    public int getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(int minOrderValue) { this.minOrderValue = minOrderValue; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public int getMaxDiscountValue() { return maxDiscountValue; }
    public void setMaxDiscountValue(int maxDiscountValue) { this.maxDiscountValue = maxDiscountValue; }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}