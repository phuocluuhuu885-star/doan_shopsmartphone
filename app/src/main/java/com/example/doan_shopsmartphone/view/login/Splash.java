package com.example.doan_shopsmartphone.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.view.product_screen.DetailProduct;
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
                // Kiểm tra xem có intent từ notification không
                Intent intent;
                Bundle extras = getIntent().getExtras();
                if (extras != null && "NEW_PRODUCT".equals(extras.getString("type")) && extras.containsKey("product_id")) {
                    String productId = extras.getString("product_id");
                    intent = new Intent(Splash.this, DetailProduct.class);
                    intent.putExtra("id_product", productId);
                } else {
                    intent = new Intent(Splash.this, LoginApp.class);
                }
                
                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}