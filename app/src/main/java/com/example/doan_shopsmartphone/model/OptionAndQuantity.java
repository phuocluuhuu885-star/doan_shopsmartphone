package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class OptionAndQuantity {
    @SerializedName("_id")
    private String id;
    @SerializedName("option_id")
    private OptionOfListCart optionProduct;
    private int quantity;
    private double discount_value;

    public OptionAndQuantity(String id, OptionOfListCart optionProduct, int quantity, double discount_value) {
        this.id = id;
        this.optionProduct = optionProduct;
        this.quantity = quantity;
        this.discount_value = discount_value;
    }

    public double getDiscount_value() {
        return discount_value;
    }

    public void setDiscount_value(double discount_value) {
        this.discount_value = discount_value;
    }

    public OptionAndQuantity() {
    }

    public OptionAndQuantity(String id, OptionOfListCart optionProduct, int quantity) {
        this.id = id;
        this.optionProduct = optionProduct;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartOfList{" +
                "id='" + id + '\'' +
                ", optionProduct=" + optionProduct +
                ", quantity=" + quantity +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OptionOfListCart getOptionProduct() {
        return optionProduct;
    }

    public void setOptionProduct(OptionOfListCart optionProduct) {
        this.optionProduct = optionProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
