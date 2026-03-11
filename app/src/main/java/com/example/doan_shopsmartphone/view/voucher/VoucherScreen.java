package com.example.doan_shopsmartphone.view.voucher;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.adapter.VoucherSCAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityVoucherScreenBinding;
import com.example.doan_shopsmartphone.model.Voucher;

import java.util.ArrayList;
import java.util.List;


public class VoucherScreen extends AppCompatActivity {
    ActivityVoucherScreenBinding binding;
    List<Voucher> list;
    VoucherSCAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();
        list.add(new Voucher("Miễn phí vận chuyển", "2tr" , "31/12/2024", "12%"));
        list.add(new Voucher("Giảm 5% với đơn hàng trên 200k", "2tr" , "31/12/2024", "5%"));
        list.add(new Voucher("Miễn phí vận chuyển", "2tr" , "31/12/2024", "12%"));
        list.add(new Voucher("Giảm 5% với đơn hàng trên 100k", "2tr" , "31/12/2024", "12%"));
        list.add(new Voucher("Giảm 5% với đơn hàng trên 100k", "2tr" , "31/12/2024", "12%"));
        list.add(new Voucher("Miễn phí vận chuyển", "2tr" , "31/12/2024", "12%"));
        list.add(new Voucher("Giảm 5% với đơn hàng trên 100k", "2tr" , "31/12/2024", "12%"));
        adapter = new VoucherSCAdapter(this, list);
        binding.rcvVoucherStore.setAdapter(adapter);
    }
}