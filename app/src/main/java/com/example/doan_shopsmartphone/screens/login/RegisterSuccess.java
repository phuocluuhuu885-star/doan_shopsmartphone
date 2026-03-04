package com.example.doan_shopsmartphone.screens.login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityLoginBinding;
import com.example.doan_shopsmartphone.databinding.ActivityRegisterSuccessBinding;

public class RegisterSuccess extends AppCompatActivity {
    private ActivityRegisterSuccessBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}