package com.example.doan_shopsmartphone.ultil.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.Notifi;
import com.example.doan_shopsmartphone.model.response.ListNotifiReponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.NotificationUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationPollingManager {
    private static final String TAG = "NotiPollingManager";
    private static final String PREF_NAME = "NOTI_PREFS";
    private static final String KEY_LAST_NOTI_ID = "LAST_NOTI_ID_";
    private static final long POLL_INTERVAL = 8000; // 8 giây định kỳ

    private static NotificationPollingManager instance;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPolling = false;
    private Context appContext;

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPolling) return;
            pollNotifications();
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };

    private NotificationPollingManager() {}

    public static synchronized NotificationPollingManager getInstance() {
        if (instance == null) {
            instance = new NotificationPollingManager();
        }
        return instance;
    }

    public void startPolling(Context context) {
        if (isPolling) return;
        this.appContext = context.getApplicationContext();
        isPolling = true;
        Log.d(TAG, "Bắt đầu lập lịch lấy thông báo định kỳ...");
        handler.post(pollRunnable);
    }

    public void stopPolling() {
        if (!isPolling) return;
        isPolling = false;
        handler.removeCallbacks(pollRunnable);
        Log.d(TAG, "Đã dừng lấy thông báo định kỳ.");
    }

    private void pollNotifications() {
        if (AccountUltil.USER == null) {
            Log.w(TAG, "USER chưa đăng nhập hoặc đang null, bỏ qua polling chu kỳ này.");
            return;
        }

        String userId = AccountUltil.USER.getId();
        String rawToken = AccountUltil.getToken(appContext);
        if (rawToken == null || rawToken.trim().isEmpty()) {
            Log.w(TAG, "Token trống, bỏ qua polling chu kỳ này.");
            return;
        }

        String token = AccountUltil.BEARER + rawToken;

        BaseApi.API.getNotifiList(token, userId).enqueue(new Callback<ListNotifiReponse>() {
            @Override
            public void onResponse(Call<ListNotifiReponse> call, Response<ListNotifiReponse> response) {
                if (!isPolling) return;

                if (response.isSuccessful() && response.body() != null) {
                    ListNotifiReponse res = response.body();
                    if ((res.getCode() == 200 || res.getCode() == 201) && res.getResult() != null) {
                        processNotifications(res.getResult(), userId);
                    }
                } else {
                    Log.e(TAG, "Lỗi lấy danh sách thông báo: Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ListNotifiReponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối mạng khi polling thông báo: " + t.getMessage());
            }
        });
    }

    private void processNotifications(List<Notifi> list, String userId) {
        if (list == null || list.isEmpty()) return;

        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String prefKey = KEY_LAST_NOTI_ID + userId;
        String lastNotiId = prefs.getString(prefKey, null);

        // Trường hợp chạy lần đầu hoặc chưa lưu ID
        if (lastNotiId == null) {
            // Lưu ID của thông báo mới nhất hiện tại làm mốc
            String newestId = list.get(0).getId();
            prefs.edit().putString(prefKey, newestId).apply();
            Log.d(TAG, "Lần chạy đầu tiên, lưu thông báo mới nhất làm mốc: " + newestId);
            return;
        }

        // Tìm index của lastNotiId trong danh sách (danh sách sắp xếp giảm dần theo thời gian)
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(lastNotiId)) {
                index = i;
                break;
            }
        }

        // Nếu không tìm thấy lastNotiId (có thể bị xóa hoặc quá cũ), set lại mốc là thông báo đầu tiên
        if (index == -1) {
            String newestId = list.get(0).getId();
            prefs.edit().putString(prefKey, newestId).apply();
            Log.d(TAG, "Không tìm thấy thông báo mốc cũ trong danh sách. Cập nhật mốc mới: " + newestId);
            return;
        }

        // Các thông báo mới nằm từ vị trí index - 1 về 0 (những thông báo mới hơn mốc cũ)
        if (index > 0) {
            Log.d(TAG, "Phát hiện có " + index + " thông báo mới!");
            // Đẩy thông báo từ cũ đến mới (từ index - 1 giảm dần về 0) để đúng trình tự thời gian
            for (int i = index - 1; i >= 0; i--) {
                Notifi noti = list.get(i);
                
                // Trích xuất thông tin
                String title = "Thông báo mới";
                String type = noti.getType();
                if ("withdraw".equalsIgnoreCase(type)) {
                    title = "Đơn rút tiền";
                } else if ("wallet".equalsIgnoreCase(type)) {
                    title = "Ví F-Wallet";
                } else if ("promotion".equalsIgnoreCase(type) || "voucher".equalsIgnoreCase(type)) {
                    title = "Khuyến mãi & Voucher";
                } else if (noti.getOrder_id() != null && !noti.getOrder_id().trim().isEmpty() && !"null".equalsIgnoreCase(noti.getOrder_id())) {
                    title = "Trạng thái đơn hàng";
                } else if (noti.getProduct_id() != null && !noti.getProduct_id().trim().isEmpty() && !"null".equalsIgnoreCase(noti.getProduct_id())) {
                    title = "Sản phẩm mới";
                }

                String content = noti.getContent();
                String orderId = noti.getOrder_id();
                String productId = noti.getProduct_id();

                Log.d(TAG, "Hiển thị thông báo lên Status Bar: " + title + " - " + content);
                
                // Gửi lên thanh thông báo hệ thống thông qua NotificationUtil
                NotificationUtil.showNotification(appContext, title, content, orderId, type, productId);
            }

            // Cập nhật lại mốc thông báo mới nhất
            String newestId = list.get(0).getId();
            prefs.edit().putString(prefKey, newestId).apply();
        }
    }
}
