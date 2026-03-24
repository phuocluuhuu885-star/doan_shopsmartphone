package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("_id")
    private String id;
    @SerializedName("user_id")
    private User user;
    @SerializedName("info_id")
    private Info info;
    private List<OptionAndQuantity> productsOrder;
    @SerializedName("total_price")
    private int totalPrice;
    private String status;
    @SerializedName("payment_status")
    private boolean payment_status;
    private String createdAt;
    private String updatedAt;
    private String reason; // Thêm trường reason


    public Order(String id, User user, Info info, List<OptionAndQuantity> productsOrder, int totalPrice, String status, boolean payment_status) {
        this.id = id;
        this.user = user;
        this.info = info;
        this.productsOrder = productsOrder;
        this.totalPrice = totalPrice;
        this.status = status;
        this.payment_status = payment_status;
    }

    public Order() {
    }




    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", info=" + info +
                ", productsOrder=" + productsOrder +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", payment_status=" + payment_status +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<OptionAndQuantity> getProductsOrder() {
        return productsOrder;
    }

    public void setProductsOrder(List<OptionAndQuantity> productsOrder) {
        this.productsOrder = productsOrder;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public boolean isPayment_status() {
        return payment_status;
    }

    public void setPayment_status(boolean payment_status) {
        this.payment_status = payment_status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}