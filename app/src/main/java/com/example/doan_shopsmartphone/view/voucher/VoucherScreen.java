package com.example.doan_shopsmartphone.view.voucher;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.doan_shopsmartphone.adapter.VoucherSCAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityVoucherScreenBinding;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.response.VoucherResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VoucherScreen extends AppCompatActivity {
    ActivityVoucherScreenBinding binding;
    List<Voucher> list;
    VoucherSCAdapter adapter;
    private String productId;
    private int productPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadVoucherList();
    }

    private void init() {
        // Nhận dữ liệu từ Adapter Pay truyền sang
        productId = getIntent().getStringExtra("productId");
        productPrice = getIntent().getIntExtra("price", 0);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();
        adapter = new VoucherSCAdapter(this, list, productId, productPrice);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvVoucherStore.setLayoutManager(layoutManager);
        binding.rcvVoucherStore.setAdapter(adapter);
    }

    private void loadVoucherList() {
        String productId = getIntent().getStringExtra("productId");
        BaseApi.API.getVoucherByProduct(productId)
                .enqueue(new Callback<VoucherResponse>() {
                    @Override
                    public void onResponse(Call<VoucherResponse> call, Response<VoucherResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            VoucherResponse res = response.body();

                            if (res.getCode() == 200 || res.getCode() == 201) {

                                // lấy list voucher theo product
                                list = res.getData();

                                // set dữ liệu cho adapter
                                adapter.setListOrder(list);

                                adapter.notifyDataSetChanged();

                            } else {
                                Log.e("API_ERROR", "Code API: " + res.getCode());
                            }

                        } else {
                            Log.e("API_ERROR", "Không lấy được danh sách Voucher");
                        }
                    }

                    @Override
                    public void onFailure(Call<VoucherResponse> call, Throwable t) {
                        Log.e("API_FAILURE", t.getMessage());
                    }
                });
    }
}