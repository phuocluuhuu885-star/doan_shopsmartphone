package com.example.doan_shopsmartphone.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityVerifyBinding;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Verify extends AppCompatActivity {
    private ActivityVerifyBinding binding;
    private ProgressLoadingDialog loadingDialog;
    private String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initController();
    }

    private void initController(){
        binding.backVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
        binding.btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCode = binding.pinview.getText().toString();
                sendCode(strCode);
            }
        });
        binding.tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
    }

    private void sendCode(String strCode) {
        loadingDialog.show();
        //handle gửi mã cho đổi mật khẩu
    }

    private void resendCode() {
        loadingDialog.show();
        //handle gửi lại mã cho đổi mật khẩu
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);
        Intent intent = getIntent();
        strEmail = intent.getStringExtra("email");
    }

}