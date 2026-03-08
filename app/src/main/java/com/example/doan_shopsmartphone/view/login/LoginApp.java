package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.databinding.ActivityLoginBinding;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.Validator;

public class LoginApp extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ProgressLoadingDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initController();
        initView();
    }

    private void initController() {
        binding.txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginApp.this, Register.class);
                startActivity(intent);
            }
        });

        binding.txtfogotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginApp.this, ForgotPass.class);
                startActivity(intent1);

            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String pass = binding.edtPass.getText().toString().trim();
                loginAccount(email,pass);
            }
        });

        binding.btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void screenSwitch(Context context, Class<?> cls) {
        Intent intent = new Intent(context,cls);
        startActivity(intent);
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);
    }

    private void loginAccount(String email,String pass) {
        if(validateLogin(email,pass)) {
            screenSwitch(LoginApp.this, MainActivity.class);
            finish();
        }

    }

    private boolean validateLogin(String email,String pass) {
        if(areEditTextsEmpty(binding.edtEmail,binding.edtPass)) {
            Toast.makeText(LoginApp.this,"Vui lòng nhập đủ thông tin !",Toast.LENGTH_SHORT).show();
            return false;
        } else if(!Validator.isValidEmail(email)) {
            Toast.makeText(LoginApp.this,"Nhập đúng định dạng email",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean areEditTextsEmpty(EditText editText1, EditText editText2) {
        String text1  = editText1.getText().toString().trim();
        String text2 = editText2.getText().toString().trim();
        return TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2);
    }
}