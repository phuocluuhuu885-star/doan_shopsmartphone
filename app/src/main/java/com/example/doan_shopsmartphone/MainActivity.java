package com.example.doan_shopsmartphone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityMainBinding;
import com.example.doan_shopsmartphone.fragment.FragmentHome;
import com.example.doan_shopsmartphone.fragment.FragmentNotification;
import com.example.doan_shopsmartphone.fragment.FragmentNotifycation;
import com.example.doan_shopsmartphone.fragment.FragmentProduct;
import com.example.doan_shopsmartphone.fragment.FragmentProfile;
import com.example.doan_shopsmartphone.model.response.CountResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickBottomNav();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchUnreadCount();
            }
        }, 3000);
    }

    private void onClickBottomNav() {
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.homepage));
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.product_24));
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.thongbao));

        binding.bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.profile));
        binding.bottomNavigation.show(1, true);
        loadFragment(FragmentHome.newInstance());

        binding.bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        loadFragment(FragmentHome.newInstance());
                        break;
                    case 2:
                        loadFragment(FragmentProduct.newInstance());
                        break;
                    case 3:
                        loadFragment(FragmentNotification.newInstance());
                        break;
                    case 4:
                        loadFragment(FragmentProfile.newInstance());
                        break;
                }
                return null;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.commit();
    }

    public void updateBadge(int count) {
        if (count > 0) {
            String displayCount = (count > 99) ? "99+" : String.valueOf(count);
            binding.bottomNavigation.setCount(3, displayCount);
        } else {
            binding.bottomNavigation.setCount(3, "empty");
        }
    }

    public void fetchUnreadCount() {
        // Lấy ID người dùng hiện tại

        try {
            if (AccountUltil.USER == null) {
                Log.e("MainActivity", "USER đang NULL, không thể gọi API!");
                return;
            }

            String userId = AccountUltil.USER.getId();
            Log.d("MainActivity", "Đang gọi API cho UserId: " + userId);

            BaseApi.API.getCountUnread(userId).enqueue(new Callback<CountResponse>() {
                @Override
                public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                    Log.d("MainActivity", "Đã có phản hồi từ Server, Code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        int unreadCount = response.body().getCount();
                        Log.d("MainActivity", "Số lượng nhận được: " + unreadCount);
                        updateBadge(unreadCount);
                    }
                }

                @Override
                public void onFailure(Call<CountResponse> call, Throwable t) {
                    Log.e("MainActivity", "Lỗi mạng hoặc API: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", "Lỗi crash ngầm: " + e.getMessage());
        }
    }
}