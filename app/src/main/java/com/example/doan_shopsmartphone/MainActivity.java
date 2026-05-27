package com.example.doan_shopsmartphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private int userRetryCount = 0;

    // Launcher xin quyền POST_NOTIFICATIONS (Android 13+)
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("MainActivity", "Quyền POST_NOTIFICATIONS đã được cấp.");
                } else {
                    Log.w("MainActivity", "Quyền POST_NOTIFICATIONS bị từ chối. Thông báo sẽ không hiển thị.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermission();
        onClickBottomNav();
        handleNotificationIntent(getIntent());

        // Khởi động lấy thông báo ngầm định kỳ và đẩy lên thanh trạng thái hệ thống
        com.example.doan_shopsmartphone.ultil.notification.NotificationPollingManager.getInstance().startPolling(this);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userRetryCount = 0;
        fetchUnreadCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng lấy thông báo định kỳ khi thoát màn hình chính
        com.example.doan_shopsmartphone.ultil.notification.NotificationPollingManager.getInstance().stopPolling();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && "open_voucher".equals(intent.getStringExtra("action"))) {
            String voucherId = intent.getStringExtra("VOUCHER_ID_KEY");
            intent.removeExtra("action");
            intent.removeExtra("VOUCHER_ID_KEY");

            // Open MyVoucherActivity first to build the back stack
            Intent voucherIntent = new Intent(MainActivity.this, com.example.doan_shopsmartphone.view.voucher.MyVoucherActivity.class);
            startActivity(voucherIntent);

            // Open VoucherDetailActivity immediately on top
            if (voucherId != null && !voucherId.trim().isEmpty() && !"null".equalsIgnoreCase(voucherId)) {
                Intent detailIntent = new Intent(MainActivity.this, com.example.doan_shopsmartphone.view.voucher.VoucherDetailActivity.class);
                detailIntent.putExtra("VOUCHER_ID_KEY", voucherId);
                startActivity(detailIntent);
            }

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
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
            binding.bottomNavigation.clearCount(3);
        }
    }

    public void fetchUnreadCount() {
        try {
            if (AccountUltil.USER == null) {
                if (userRetryCount < 5) {
                    userRetryCount++;
                    Log.e("MainActivity", "USER đang NULL, sẽ thử lại sau 1 giây... (Lần " + userRetryCount + ")");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchUnreadCount();
                        }
                    }, 1000);
                } else {
                    Log.e("MainActivity", "Không thể lấy thông tin USER sau nhiều lần thử.");
                }
                return;
            }

            userRetryCount = 0; // Reset count
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