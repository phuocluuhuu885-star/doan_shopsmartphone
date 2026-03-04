package com.example.doan_shopsmartphone.buy_product;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.InfoAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityAddressBinding;
import com.example.doan_shopsmartphone.model.Info;
import com.example.doan_shopsmartphone.ultil.InfoInterface;


public class AddressActivity extends AppCompatActivity implements InfoInterface {

    private ActivityAddressBinding binding;
    private List<Info> infoList;
    private InfoAdapter infoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();

    }

    private void initView() {
        infoList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rcvInfo.setLayoutManager(layoutManager);
        infoAdapter = new InfoAdapter(this, infoList, this);
        binding.rcvInfo.setAdapter(infoAdapter);
    }

    private void initController() {
        binding.addAddress.setOnClickListener(view -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            mActivityResultLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        });

        binding.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onclickObject(Object object) {
        Info info = (Info) object;
        Intent intent = new Intent(AddressActivity.this, PayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_info", info);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void updateObject(Object object) {
        Info info = (Info) object;
        Intent intent = new Intent(this, UpdateAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", info);
        intent.putExtras(bundle);
        mActivityResultLauncher.launch(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                }
            });
}
