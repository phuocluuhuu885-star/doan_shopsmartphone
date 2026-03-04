package com.example.doan_shopsmartphone.screens.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.databinding.ActivityResetPasswordBinding;
import com.example.doan_shopsmartphone.databinding.ActivitySplashBinding;

public class ResetPassword extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}