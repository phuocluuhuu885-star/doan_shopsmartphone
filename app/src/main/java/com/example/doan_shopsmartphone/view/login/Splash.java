package com.example.doan_shopsmartphone.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.view.product_screen.DetailProduct;
import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.databinding.ActivitySplashBinding;

public class Splash extends AppCompatActivity {
    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                Bundle extras = getIntent().getExtras();
                
                // Kiểm tra trạng thái đăng nhập (session tĩnh)
                boolean isLogged = com.example.doan_shopsmartphone.ultil.AccountUltil.TOKEN != null && 
                                   !com.example.doan_shopsmartphone.ultil.AccountUltil.TOKEN.trim().isEmpty();
                
                if (!isLogged) {
                    // Nếu chưa đăng nhập hoặc app bị kill làm rỗng session -> Chuyển sang LoginApp và truyền extras đi cùng
                    intent = new Intent(Splash.this, LoginApp.class);
                    if (extras != null) {
                        intent.putExtras(extras);
                    }
                } else {
                    // Nếu đã đăng nhập (session còn nguyên trong bộ nhớ)
                    if (extras != null && "NEW_PRODUCT".equals(extras.getString("type")) && extras.containsKey("product_id")) {
                        String productId = extras.getString("product_id");
                        intent = new Intent(Splash.this, DetailProduct.class);
                        intent.putExtra("id_product", productId);
                    } else if (extras != null && ("promotion".equals(extras.getString("type")) || "open_voucher".equals(extras.getString("action")))) {
                        intent = new Intent(Splash.this, MainActivity.class);
                        intent.putExtra("action", "open_voucher");
                    } else {
                        intent = new Intent(Splash.this, MainActivity.class);
                    }
                }
                
                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}