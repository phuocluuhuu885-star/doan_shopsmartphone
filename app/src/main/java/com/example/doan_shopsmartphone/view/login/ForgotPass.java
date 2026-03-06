package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityForgotPassBinding;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.Validator;


public class ForgotPass extends AppCompatActivity {
    private ActivityForgotPassBinding binding;
    private ProgressLoadingDialog loadingDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initController();
        initView();
    }

    private void initController() {
        binding.btnVerifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email  =binding.edtEmail.getText().toString().trim();
                registerAccount(email);
            }
        });
        binding.backfogot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
    }
    private void registerAccount(String email) {
        if(checkEmail(email)) {
            loadingDialog.show();

        }
    }
    private boolean checkEmail(String email) {
        setTextUI();
        if(!Validator.isValidEmail(email)) {
            binding.tvErrorEmail.setText("Không đúng định dạng email!");
            binding.lineEmail.setVisibility(View.GONE);
            binding.tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
    private void setTextUI() {
        binding.tvErrorEmail.setText("");
        binding.lineEmail.setVisibility(View.VISIBLE);
        binding.tvErrorEmail.setVisibility(View.GONE);
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
//    }
}