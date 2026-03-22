package com.example.doan_shopsmartphone.model.response;

import java.util.List;

public class VoucherRequest {
    private List<String> productIds;

    public VoucherRequest(List<String> productIds) {
        this.productIds = productIds;
    }
}
