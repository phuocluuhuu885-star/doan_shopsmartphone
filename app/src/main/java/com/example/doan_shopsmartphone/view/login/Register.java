package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityRegisterBinding;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.Validator;


public class Register extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private ProgressLoadingDialog loadingDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initController();
        initView();
    }

    private void initController() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String pass = binding.edtPass.getText().toString().trim();
                String repass = binding.edtRepass.getText().toString().trim();
                registerAccount(email,pass,repass);
            }
        });
        binding.btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
        binding.tvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
    }

    private void registerAccount(String email,String pass,String repass) {
        if(checkRegister(email,pass,repass)) {
            loadingDialog.show();

        }
    }
    private boolean checkRegister(String email,String pass,String repass) {
        setTextUI();
        if(!Validator.isValidEmail(email)) {
            binding.tvErrorEmail.setText("Không đúng định dạng email!");
            binding.lineEmail.setVisibility(View.GONE);
            binding.tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        } else if(!pass.equals(repass)) {
            binding.tvErrorPass.setText("Pass và repass không khớp nhau!");
            binding.linePass.setVisibility(View.GONE);
            binding.tvErrorPass.setVisibility(View.VISIBLE);
            return false;
        } else if(pass.length() < 6) {
            binding.tvErrorPass.setText("Vui lòng đặt mật khẩu từ 6 kí tự!");
            binding.linePass.setVisibility(View.GONE);
            binding.tvErrorPass.setVisibility(View.VISIBLE);
            return false;
        }
        return true;

    }
    private void setTextUI() {
        binding.tvErrorEmail.setText("");
        binding.tvErrorPass.setText("");
        binding.lineEmail.setVisibility(View.VISIBLE);
        binding.tvErrorEmail.setVisibility(View.GONE);
        binding.linePass.setVisibility(View.VISIBLE);
        binding.tvErrorPass.setVisibility(View.GONE);
    }
    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);
    }


}