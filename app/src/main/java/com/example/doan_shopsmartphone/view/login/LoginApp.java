package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;

import com.example.doan_shopsmartphone.databinding.ActivityLoginBinding;
import com.example.doan_shopsmartphone.model.response.LoginResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ApiUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.Validator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginApp extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ProgressLoadingDialog loadingDialog;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;
    final int RC_SIGN_IN = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initLoginGoogle();
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
                signIn();
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
            BaseApi.API.login(email,pass).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        assert loginResponse != null;
                        if (loginResponse.getCode() == 200) {
                            String token = loginResponse.getToken();

                            SharedPreferences prefs =
                                    getSharedPreferences("ACCOUNT", MODE_PRIVATE);
                            prefs.edit().putString("TOKEN", token).apply();
                            AccountUltil.saveToken(LoginApp.this, token);

                            // set static
                            AccountUltil.TOKEN = token;

                            Log.d("JWT", "Saved token = " + token);

                            ApiUtil.getDetailUser(LoginApp.this, loadingDialog);
                            ApiUtil.getAllCart(LoginApp.this, null);

                            screenSwitch(LoginApp.this, MainActivity.class);
                            finish();
                        }

                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                        }catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginApp.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
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

    private void initLoginGoogle() {
        sharedPreferences = getSharedPreferences("USER_FILE",MODE_PRIVATE);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);

            // lấy token người dùng
            String idToken = account.getIdToken();
            String email=  account.getEmail();

            // kiểm tra token xem có null ko

            if(idToken!= null) {
                // Điều hướng qua màn Main và chuyển token qua
                BaseApi.API.loginGoogle(idToken).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful()) {
                            LoginResponse loginResponse =response.body();
                            if (loginResponse.getCode() == 200) {
                                String token = loginResponse.getToken();

                                AccountUltil.saveToken(LoginApp.this, token);

                                AccountUltil.TOKEN = token;

                                Log.d("JWT", "Saved GG token = " + token);

                                ApiUtil.getDetailUser(LoginApp.this, loadingDialog);
                                ApiUtil.getAllCart(LoginApp.this, null);

                                screenSwitch(LoginApp.this, MainActivity.class);
                                finish();
                            }

                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                JSONObject erJson = new JSONObject(errorBody);
                                String errorMessage = erJson.getString("message");
                                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                throw  new RuntimeException(e);
                            }
                        }
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                    }
                });
            } else  {
                // Xử lí token null
                Log.e("Token","Token is null");
            }
        } catch (ApiException e) {
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ApiException", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập bằng google thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}