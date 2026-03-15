package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("province_id")
    private int provinceId;

    @SerializedName("province_name")
    private String provinceName;

    @SerializedName("province_type")
    private String provinceType;

    public City() {
    }

    public City(String provinceName) {
        this.provinceName = provinceName;
    }

    public City(int provinceId, String provinceName, String provinceType) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.provinceType = provinceType;
    }

    // ⭐ FIX LỖI Ở ĐÂY
    public City(int code, String name) {
        this.provinceId = code;
        this.provinceName = name;
    }

    // ⭐ spinner sẽ hiển thị tên tỉnh
    @Override
    public String toString() {
        return provinceName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
