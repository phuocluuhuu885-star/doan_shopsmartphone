package com.example.doan_shopsmartphone.buy_product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.DecimalFormat;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityPayBinding;
import com.example.doan_shopsmartphone.success_screen.OderSuccessActivity;

public class PayActivity extends AppCompatActivity {

    private ActivityPayBinding binding;

    private int paymentMethods;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paymentMethods = getIntent().getIntExtra("paymentMethods", 0);
        if (paymentMethods == 1) {
            binding.txtPaymentMethods.setText("Thanh toán khi nhận hàng");
        } else if (paymentMethods == 2) {
            binding.txtPaymentMethods.setText("Thanh toán qua ví ZaloPay");
        }

        initView();
        initController();
    }

    private void initView() {
        totalPrice = getIntent().getIntExtra("totalPrice", 0);
        DecimalFormat df = new DecimalFormat("###,###,###");
        binding.tvTotalPrice.setText(df.format(totalPrice) + "đ");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rcvProduct.setLayoutManager(linearLayoutManager);
    }

    private void initController() {
        binding.layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, AddressActivity.class);
                mActivityResultLauncher.launch(intent);
                overridePendingTransition(0,0);
            }
        });

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentMethods == 0) {
                    Toast.makeText(PayActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(PayActivity.this, OderSuccessActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Bundle bundle = intent.getExtras();
                            if (bundle != null && bundle.containsKey("object_info")) {
                                Object objectInfo = bundle.get("object_info");
                            }
                        }
                    }
                }
            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
