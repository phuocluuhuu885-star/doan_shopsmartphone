package com.example.doan_shopsmartphone.view.voucher;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.MyVoucherAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.response.ListVoucherResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVoucherActivity extends AppCompatActivity {

    private RecyclerView rcvVoucher;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private MyVoucherAdapter adapter;
    private final List<Voucher> validVoucherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voucher);

        initViews();
        loadVouchers();
    }

    private void initViews() {
        rcvVoucher = findViewById(R.id.rcvVoucher);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        rcvVoucher.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyVoucherAdapter(this, validVoucherList);
        rcvVoucher.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadVouchers() {
        if (AccountUltil.TOKEN == null) {
            progressBar.setVisibility(View.GONE);
            tvEmptyState.setText("Vui lòng đăng nhập để xem voucher");
            tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        String authHeader = AccountUltil.BEARER + AccountUltil.TOKEN;

//        BaseApi.API.getListVoucher(authHeader).enqueue(new Callback<ListVoucherResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<ListVoucherResponse> call, @NonNull Response<ListVoucherResponse> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body() != null) {
//                    ListVoucherResponse listVoucherResponse = response.body();
//                    if (listVoucherResponse.getCode() == 200 && listVoucherResponse.getData() != null) {
//                        filterValidVouchers(listVoucherResponse.getData());
//                    } else {
//                        Toast.makeText(MyVoucherActivity.this, "Không thể tải dữ liệu voucher", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.e("MyVoucherActivity", "Response code: " + response.code());
//                    Toast.makeText(MyVoucherActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
//                }
//
//                updateUIState();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ListVoucherResponse> call, @NonNull Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Log.e("MyVoucherActivity", "Failure: " + t.getMessage(), t);
//                Toast.makeText(MyVoucherActivity.this, "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
//                updateUIState();
//            }
//        });
    }

    private void filterValidVouchers(List<Voucher> allVouchers) {
        validVoucherList.clear();
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        for (Voucher voucher : allVouchers) {
            // Lọc:
            // 1. status == true (hoạt động)
            // 2. quantity > 0 (còn lượt sử dụng)
            // 3. expiryDate chưa quá hạn
            if (voucher.getStatus() == 1 && voucher.getQuantity() > 0) {
                try {
                    if (voucher.getExpiryDate() != null) {
                        Date expiryDate = sdf.parse(voucher.getExpiryDate());
                        if (expiryDate != null && expiryDate.after(currentDate)) {
                            validVoucherList.add(voucher);
                        }
                    }
                } catch (Exception e) {
                    Log.e("MyVoucherActivity", "Lỗi parse ngày voucher: " + voucher.getExpiryDate(), e);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateUIState() {
        if (validVoucherList.isEmpty()) {
            rcvVoucher.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rcvVoucher.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}
