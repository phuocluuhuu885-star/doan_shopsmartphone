package com.example.doan_shopsmartphone.screens.login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityForgotPasswordBinding;
import com.example.doan_shopsmartphone.databinding.ActivityLoginBinding;

public class ForgotPassword extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
