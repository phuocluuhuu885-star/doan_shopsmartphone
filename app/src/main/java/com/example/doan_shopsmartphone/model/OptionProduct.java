package com.example.doan_shopsmartphone.model;

import com.google.gson.annotations.SerializedName;

public class OptionProduct {
    @SerializedName("_id")
    private String id;
    @SerializedName("product_id")
    private String productId;
    @SerializedName("name_color")
    private String nameColor;
    @SerializedName("color_code")
    private String colorCode;
    private String image;
    private int price;
    @SerializedName("discount_value")
    private int discountValue;
    private int quantity;
    private int soldQuantity;
    private boolean hot_option;
    @SerializedName("storage_capacity")
    private String storageCapacity;
    @SerializedName("condition_percent")
    private String conditionPercent;
    @SerializedName("battery_health")
    private String batteryHealth;
    @SerializedName("is_original")
    private String isOriginal;
    @SerializedName("warranty_time")
    private String warrantyTime;

    private String ram;
    private String screen;


    public String getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(String storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public String getConditionPercent() {
        return conditionPercent;
    }

    public void setConditionPercent(String conditionPercent) {
        this.conditionPercent = conditionPercent;
    }

    public String getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(String batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public String getIsOriginal() {
        return isOriginal;
    }

    public void setIsOriginal(String isOriginal) {
        this.isOriginal = isOriginal;
    }

    public String getWarrantyTime() {
        return warrantyTime;
    }

    public void setWarrantyTime(String warrantyTime) {
        this.warrantyTime = warrantyTime;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public OptionProduct() {
    }

    public OptionProduct(String id, String productId, String nameColor, String colorCode, String image, int price, int discountValue, int quantity, int soldQuantity, boolean hot_option, String storageCapacity, String conditionPercent, String batteryHealth, String isOriginal, String warrantyTime, String ram, String screen) {
        this.id = id;
        this.productId = productId;
        this.nameColor = nameColor;
        this.colorCode = colorCode;
        this.image = image;
        this.price = price;
        this.discountValue = discountValue;
        this.quantity = quantity;
        this.soldQuantity = soldQuantity;
        this.hot_option = hot_option;
        this.storageCapacity = storageCapacity;
        this.conditionPercent = conditionPercent;
        this.batteryHealth = batteryHealth;
        this.isOriginal = isOriginal;
        this.warrantyTime = warrantyTime;
        this.ram = ram;
        this.screen = screen;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", nameColor='" + nameColor + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", discount_value=" + discountValue +
                ", quantity=" + quantity +
                ", soldQuantity=" + soldQuantity +
                ", hot_option=" + hot_option +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discount_value) {
        this.discountValue = discount_value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public boolean isHot_option() {
        return hot_option;
    }

    public void setHot_option(boolean hot_option) {
        this.hot_option = hot_option;
    }


}
