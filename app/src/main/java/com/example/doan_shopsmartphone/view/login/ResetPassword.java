package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.databinding.ActivityResetPasswordBinding;
import com.example.doan_shopsmartphone.databinding.ActivitySplashBinding;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import com.example.doan_shopsmartphone.R;

public class ResetPassword extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private ProgressLoadingDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initController() {
        binding.btnRepassSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strOldPassword = binding.edtOldPassword.getText().toString().trim();
                String strNewPassword = binding.edtNewPassword.getText().toString().trim();
                String strReNewPassword = binding.edtReNewPassword.getText().toString().trim();
                repassAccount(strOldPassword, strNewPassword, strReNewPassword);
            }
        });
        binding.backResetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
    }

    private void repassAccount(String strOldPassword,String strNewPassword,String strReNewPassword) {
        if(validateRepass(strOldPassword,strNewPassword,strReNewPassword)) {
            //handle logic reset password
        }
    }

    private boolean validateRepass(String strOldPassword,String strNewPassword,String strReNewPassword) {
        if (TextUtils.isEmpty(strOldPassword) || TextUtils.isEmpty(strNewPassword) || TextUtils.isEmpty(strReNewPassword)){
            Toast.makeText(ResetPassword.this,"Bạn đừng để trống chỗ nhập nhé!", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!strNewPassword.equals(strReNewPassword)) {
            Toast.makeText(ResetPassword.this,"NewPassword và ReNewPassword không khớp nhau!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);
    }
}