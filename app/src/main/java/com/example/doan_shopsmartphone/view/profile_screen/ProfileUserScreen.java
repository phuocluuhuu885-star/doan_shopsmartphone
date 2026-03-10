package com.example.doan_shopsmartphone.view.profile_screen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityProfileUserScreenBinding;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ApiUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;
import com.github.dhaval2404.imagepicker.ImagePicker;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserScreen extends AppCompatActivity {
    private ActivityProfileUserScreenBinding binding;
    private ProgressLoadingDialog loadingDialog;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUserScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initController();
    }

    private void initController() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK); // Chỉ cần thông báo thành công
                finish(); // Đóng màn hình hiện tại
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        binding.imgAvartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileUserScreen.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        binding.updatePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtUserName.setEnabled(true);
                binding.edtBirthday.setEnabled(true);
                binding.linearCalander.setClickable(true);
                binding.btnSave.setEnabled(true);
            }
        });

        binding.linearCalander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalander();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.edtUserName.getText().toString().trim();
                String birthday = binding.edtBirthday.getText().toString().trim();
                if (validateValue(username, birthday)) {
                    binding.edtUserName.setEnabled(false);
                    binding.edtBirthday.setEnabled(false);
                    binding.linearCalander.setClickable(false);
                    apiEditProfile(username, birthday);
                }
            }
        });
    }

    private void openCalander() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                binding.edtBirthday.setText(formatter.format(calendar.getTime()));
            }
        }, year, month, day);

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = getPath(uri);
            if (path != null) {
                File file = new File(path);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                binding.imgAvartar.setImageURI(uri);
                MultipartBody.Part fileImgAvatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
                apiUploadAvatar(fileImgAvatar);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    private String getPath(Uri uri) {
        String result = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index != -1) result = cursor.getString(index);
            }
            cursor.close();
        }
        return result != null ? result : uri.getPath();
    }

    private void apiUploadAvatar(MultipartBody.Part fileImgAvatar) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        String idUser = AccountUltil.USER.getId();
        loadingDialog.show();
        BaseApi.API.uploadAvatar(token, idUser, fileImgAvatar).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiUtil.getDetailUser(ProfileUserScreen.this, loadingDialog);
                    Toast.makeText(ProfileUserScreen.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    private void apiEditProfile(String username, String birthday) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        String idUser = AccountUltil.USER.getId();
        loadingDialog.show();

        BaseApi.API.editProfile(token, idUser, username, birthday).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiUtil.getDetailUser(ProfileUserScreen.this, loadingDialog);

                    Toast.makeText(ProfileUserScreen.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ProfileUserScreen.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(ProfileUserScreen.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateValue(String username, String birthday) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(birthday)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            formatter.setLenient(false);
            formatter.parse(birthday);
        } catch (Exception e) {
            Toast.makeText(this, "Nhập đúng định dạng ngày tháng dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressLint({"SimpleDateFormat", "ResourceAsColor"})
    private void initData() {
        loadingDialog = new ProgressLoadingDialog(this);
        if (AccountUltil.USER != null) {
            binding.edtUserName.setText(AccountUltil.USER.getUsername());
            binding.edtBirthday.setText(AccountUltil.USER.getBirthday());
            binding.email.setText(AccountUltil.USER.getEmail());
            Glide.with(this)
                    .load(AccountUltil.USER.getAvatar())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.avatar1)
                    .into(binding.imgAvartar);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileUserScreen.this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}