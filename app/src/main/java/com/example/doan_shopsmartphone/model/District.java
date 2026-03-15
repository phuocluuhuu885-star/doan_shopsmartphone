package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class District {

    @SerializedName("district_id")
    private int districtId;

    @SerializedName("district_name")
    private String districtName;

    public District() {
    }

    public District(int districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
    }

    public District(String districtName) {
        this.districtName = districtName;
    }

    // Spinner sẽ hiển thị tên quận/huyện
    @Override
    public String toString() {
        return districtName;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}
