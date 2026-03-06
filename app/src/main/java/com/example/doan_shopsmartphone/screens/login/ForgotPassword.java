package com.example.doan_shopsmartphone.screens.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.databinding.ActivityForgotPassBinding;

public class ForgotPassword extends AppCompatActivity {
    private ActivityForgotPassBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
