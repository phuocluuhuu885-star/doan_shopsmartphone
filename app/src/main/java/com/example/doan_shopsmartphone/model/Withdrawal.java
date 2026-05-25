package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Withdrawal implements Serializable {
    @SerializedName("_id")
    private String id;
    private String user_id;
    private String name;
    private String bank;
    private String account_number;
    private double amount;
    private String status; // "pending", "approved", "rejected"
    private String bill_image;
    private String rejection_reason;
    private String createdAt;
    private String updatedAt;

    public Withdrawal() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBill_image() {
        return bill_image;
    }

    public void setBill_image(String bill_image) {
        this.bill_image = bill_image;
    }

    public String getRejection_reason() {
        return rejection_reason;
    }

    public void setRejection_reason(String rejection_reason) {
        this.rejection_reason = rejection_reason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
