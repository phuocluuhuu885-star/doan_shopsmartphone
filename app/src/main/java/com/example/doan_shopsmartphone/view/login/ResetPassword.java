package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityResetPasswordBinding;
import com.example.doan_shopsmartphone.databinding.ActivitySplashBinding;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.JWTUltil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.ultil.TAG;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private ProgressLoadingDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initController();
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
            if(validateRepass(strOldPassword,strNewPassword,strReNewPassword)) {
//            String token = AccountUltil.BEARER + AccountUltil.TOKEN;
                String token = AccountUltil.BEARER + AccountUltil.getToken(this);

                String idUser = JWTUltil.decoded(AccountUltil.getToken(this)).getUserId();
                loadingDialog.show();
                BaseApi.API.changePassword(token,idUser,strOldPassword,strNewPassword).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if(response.isSuccessful()){ // chỉ nhận đầu status 200
                            ServerResponse serverResponse = response.body();
                            Log.d(TAG.toString, "onResponse-repassAccount: " + serverResponse.toString());
                            if(serverResponse.getCode() == 200) {
                                Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else { // nhận các đầu status #200
                            try {
                                String errorBody = response.errorBody().string();
                                JSONObject errorJson = new JSONObject(errorBody);
                                String errorMessage = errorJson.getString("message");
                                Log.d(TAG.toString, "onResponse-register: " + errorMessage);
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }catch (IOException e){
                                e.printStackTrace();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(ResetPassword.this, t.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG.toString, "onFailure-ResetPassWord: " + t.toString());
                        loadingDialog.dismiss();
                    }
                });
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}