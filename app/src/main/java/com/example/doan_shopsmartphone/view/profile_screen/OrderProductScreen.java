package com.example.doan_shopsmartphone.view.profile_screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ViewPageOrderAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityOrderProductScreenBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class OrderProductScreen extends AppCompatActivity {
    private ViewPageOrderAdapter viewPageHistoryAdapter;
    private ActivityOrderProductScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderProductScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();
        setTab();
    }

    private void setTab() {
        viewPageHistoryAdapter = new ViewPageOrderAdapter(this);
        binding.viewPagerHistory.setAdapter(viewPageHistoryAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabHistory, binding.viewPagerHistory, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Chờ xử lý");
                        break;
                    case 1:
                        tab.setText("Chờ giao hàng");
                        break;
                    case 2:
                        tab.setText("Đang giao hàng");
                        break;
                    case 3:
                        tab.setText("Đã giao hàng");
                        break;
                    case 4:
                        tab.setText("Đã hủy");
                        break;
                }
            }
        });

        mediator.attach();
    }
    private void initController() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }

    @SuppressLint("GestureBackNavigation")
    private void initView() {
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}