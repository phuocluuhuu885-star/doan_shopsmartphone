package com.example.doan_shopsmartphone.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String status;
    private boolean discounted;
    private String image;
    private double minPrice;
    private double averageRate;
    private int review;
    private int soldQuantity;
    private int reviewCount;
    @SerializedName("condition_percent")
    private String conditionPercent;
    @SerializedName("battery_health")
    private String batteryHealth;
    @SerializedName("is_original")
    private String isOriginal;
    @SerializedName("warranty_time")
    private String warrantyTime;

    private String screen;
    private String camera;
    private String chipset;
    private String cpu;
    private String gpu;
    private String operatingSystem;
    private String battery;
    private String connection;
    public Product(String id, String name, String status, boolean discounted, String image, double minPrice, double averageRate, int review, int soldQuantity, int reviewcount) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.discounted = discounted;
        this.image = image;
        this.minPrice = minPrice;
        this.averageRate = averageRate;
        this.review = review;
        this.soldQuantity = soldQuantity;
        this.reviewCount = reviewcount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Product() {
    }

    public Product(String id, String name,String status, boolean discounted, String image, int minPrice, double averageRate, int review, int soldQuantity) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.discounted = discounted;
        this.image = image;
        this.minPrice = minPrice;
        this.averageRate = averageRate;
        this.review = review;
        this.soldQuantity = soldQuantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", discounted=" + discounted +
                ", image='" + image + '\'' +
                ", minPrice=" + minPrice +
                ", averageRate=" + averageRate +
                ", reviewcount="+ reviewCount +
                ", review=" + review +
                ", soldQuantity=" + soldQuantity +
                '}';
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(double averageRate) {
        this.averageRate = averageRate;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
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

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
}
