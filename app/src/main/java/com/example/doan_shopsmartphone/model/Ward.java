package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class Ward {
    @SerializedName("ward_id")
    private int wardId;
    @SerializedName("ward_name")
    private String wardName;

    public Ward() {
    }

    public Ward(String wardName) {
        this.wardName = wardName;
    }

    public Ward(int wardId, String wardName) {
        this.wardId = wardId;
        this.wardName = wardName;
    }
    public int getWardId() {
        return wardId;
    }

    public void setWardId(int wardId) {
        this.wardId = wardId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }
}
