package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Voucher;

import java.util.List;

public class VoucherResponse {
        private int code;
        private String message;
        private List<Voucher> data; // Biến này phải trùng tên với "data" trong JSON server trả về

        // Getter và Setter
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public List<Voucher> getData() { return data; }
}
