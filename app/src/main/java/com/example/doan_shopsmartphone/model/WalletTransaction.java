package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class WalletTransaction implements Serializable {
    @SerializedName("_id")
    private String id;
    private String user_id;
    private String type;
    private int amount;
    private String description;
    private String order_id;
    private String trans_id;
    private String createdAt;
    private String updatedAt;

    public WalletTransaction() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public String getTrans_id() { return trans_id; }
    public void setTrans_id(String trans_id) { this.trans_id = trans_id; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
