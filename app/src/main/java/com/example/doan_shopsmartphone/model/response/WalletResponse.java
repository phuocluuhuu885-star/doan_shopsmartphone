package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.WalletTransaction;
import java.util.List;

public class WalletResponse {
    private int code;
    private String message;
    private WalletData data;

    public WalletResponse() {}

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public WalletData getData() { return data; }
    public void setData(WalletData data) { this.data = data; }

    public static class WalletData {
        private int balance;
        private List<WalletTransaction> transactions;

        public WalletData() {}

        public int getBalance() { return balance; }
        public void setBalance(int balance) { this.balance = balance; }

        public List<WalletTransaction> getTransactions() { return transactions; }
        public void setTransactions(List<WalletTransaction> transactions) { this.transactions = transactions; }
    }
}
