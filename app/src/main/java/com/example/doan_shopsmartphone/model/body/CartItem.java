package com.example.doan_shopsmartphone.model.body;

import com.example.doan_shopsmartphone.model.Voucher;

import java.util.List;

public class CartItem {
    private String productName;
    private List<Voucher> listVoucher;

    public CartItem(String productName, List<Voucher> listVoucher) {
        this.productName = productName;
        this.listVoucher = listVoucher;
    }
    public String getProductName() { return productName; }
    public List<Voucher> getListVoucher() { return listVoucher; }
}