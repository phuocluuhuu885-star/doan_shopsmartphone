package com.example.doan_shopsmartphone.ultil;

public class TAG {
    public static String toString = "zzzzzzzzzzzz";
    public static String WAIT_CONFIRM = "Chờ xác nhận";
    public static String WAIT_DELIVERY = "Chờ giao hàng";
    public static String DELIVERING = "Đang giao hàng";
    public static String WAIT_DELIVERING_COMBINED = "Chờ/Đang giao hàng";

    public static String DELIVERED = "Đã giao hàng";
    public static String CANCELLED = "Đã hủy";
    public static String PAID = "Đã thanh toán";

    public static String formatOrderStatus(String status) {
        if ("Chờ giao hàng".equals(status)) {
            return "Chờ lấy hàng";
        }
        return status;
    }
}
