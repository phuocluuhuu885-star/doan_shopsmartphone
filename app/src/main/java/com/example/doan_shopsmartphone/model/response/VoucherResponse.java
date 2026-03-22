package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.VoucherGroup;

import java.util.List;

public class VoucherResponse {
        private boolean success;
        private String message;
        private List<VoucherGroup> data;

        public VoucherResponse(boolean success, String message, List<VoucherGroup> data) {
                this.success = success;
                this.message = message;
                this.data = data;
        }

        public boolean isSuccess() {
                return success;
        }

        public void setSuccess(boolean success) {
                this.success = success;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public List<VoucherGroup> getData() {
                return data;
        }

        public void setData(List<VoucherGroup> data) {
                this.data = data;
        }
}
