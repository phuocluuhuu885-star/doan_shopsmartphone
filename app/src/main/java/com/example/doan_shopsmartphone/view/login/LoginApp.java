package com.example.doan_shopsmartphone.view.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.messaging.FirebaseMessaging;

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
        initView();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "Token của máy này: " + token);
                    // Hãy gọi API của bạn để lưu Token này vào bảng User trên Server
                });
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initLoginGoogle();
        setContentView(binding.getRoot());
        initController();

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
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = "";
            if (task.isSuccessful()) {
                token = task.getResult();
            }

            // Gọi API login với 3 tham số
            BaseApi.API.login(email,pass,token).enqueue(new Callback<LoginResponse>() {
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
                .requestIdToken("511125845737-i0frkihqr9dos93vee83m1kog0n7u20c.apps.googleusercontent.com")
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
            String idToken = account.getIdToken();

            if (idToken != null) {
                // 1. Hiển thị loading trước khi gọi API
                if (loadingDialog != null) loadingDialog.show();

                BaseApi.API.loginGoogle(idToken).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        // 2. Tắt loading khi có kết quả trả về
                        if (loadingDialog != null) loadingDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.getCode() == 200) {
                                String token = loginResponse.getToken();

                                // Lưu token
                                AccountUltil.saveToken(LoginApp.this, token);
                                AccountUltil.TOKEN = token;

                                Log.d("JWT", "Login thành công, Token: " + token);

                                // Lấy dữ liệu cần thiết
                                ApiUtil.getDetailUser(LoginApp.this, null); // Truyền null nếu ko muốn hiện dialog lần nữa
                                ApiUtil.getAllCart(LoginApp.this, null);

                                screenSwitch(LoginApp.this, MainActivity.class);
                                finish();
                            } else {
                                Toast.makeText(LoginApp.this, "Lỗi: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Xử lý lỗi từ Server (400, 401, 500...)
                            parseErrorBody(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // 3. Phải tắt loading và báo lỗi nếu rớt mạng/server sập
                        if (loadingDialog != null) loadingDialog.dismiss();
                        Log.e("API_ERROR", t.getMessage());
                        Toast.makeText(LoginApp.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ApiException e) {
            Log.w("ApiException", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập Google thất bại (Code: " + e.getStatusCode() + ")", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm phụ để đọc lỗi từ server cho sạch code
    private void parseErrorBody(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody().string();
            JSONObject erJson = new JSONObject(errorBody);
            String errorMessage = erJson.optString("message", "Lỗi không xác định");
            Log.e("SERVER_ERROR", "Raw error body: " + errorBody);
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SERVER_ERROR", "Lỗi khi parse JSON: " + e.getMessage());
        }
    }
}