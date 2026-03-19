package com.example.doan_shopsmartphone.model.response.store;

import com.example.doan_shopsmartphone.model.Order;

import java.io.Serializable;


public class DetailBills implements Serializable {
    private  int code;
    Order result;
    private String message;

    public DetailBills(int code, Order result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Order getResult() {
        return result;
    }

    public void setResult(Order result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
