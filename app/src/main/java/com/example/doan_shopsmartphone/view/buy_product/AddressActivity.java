package com.example.doan_shopsmartphone.view.buy_product;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.databinding.ActivityAddressBinding;


public class AddressActivity extends AppCompatActivity {

    private ActivityAddressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();

    }

    private void initView() {

    }

    private void initController() {
        binding.addAddress.setOnClickListener(view -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            mActivityResultLauncher.launch(intent);
            overridePendingTransition(0,0);
        });

        binding.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,0);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }


    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                }
            });
}
