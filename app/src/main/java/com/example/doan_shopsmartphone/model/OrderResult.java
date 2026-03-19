package com.example.doan_shopsmartphone.model;

public class OrderResult {
    private String _id;
    private String app_trans_id; // Phải trùng tên với key trong JSON của Server

    // Getter cho app_trans_id
    public String getAppTransId() {
        return app_trans_id;
    }

    public String getId() {
        return _id;
    }
}