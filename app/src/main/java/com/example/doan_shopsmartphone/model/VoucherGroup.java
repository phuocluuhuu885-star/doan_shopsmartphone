package com.example.doan_shopsmartphone.model;

import java.util.List;

public class VoucherGroup {
    private String productName;
    private List<Voucher> vouchers;

    public VoucherGroup( String productName, List<Voucher> vouchers) {
        this.productName = productName;
        this.vouchers = vouchers;
    }
    // Getter & Setter...
    public List<Voucher> getVouchers() { return vouchers; }
    public String getProductName() { return productName; }
}