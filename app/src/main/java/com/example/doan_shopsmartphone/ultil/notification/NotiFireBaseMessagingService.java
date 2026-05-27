package com.example.doan_shopsmartphone.ultil.notification;

import android.util.Log;

import com.example.doan_shopsmartphone.ultil.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotiFireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "NotiFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Nhận được thông báo FCM mới!");
        String orderId = "";
        String type = "";
        String productId = "";
        String voucherId = "";
        if (remoteMessage.getData().size() > 0) {
            orderId = remoteMessage.getData().get("order_id");
            type = remoteMessage.getData().get("type");
            productId = remoteMessage.getData().get("product_id");
            voucherId = remoteMessage.getData().get("voucher_id");
        }

        // Lấy tiêu đề và nội dung
        String title = "";
        String messageBody = "";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            messageBody = remoteMessage.getNotification().getBody();
        } else if (remoteMessage.getData().size() > 0) {
            // Trường hợp Backend chỉ gửi data mà không gửi notification object
            title = remoteMessage.getData().get("title");
            messageBody = remoteMessage.getData().get("body");
        }

        if (title == null || title.trim().isEmpty()) {
            title = "Thông báo mới";
        }

        // Hiển thị thông báo qua NotificationUtil
        NotificationUtil.showNotification(this, title, messageBody, orderId, type, productId, voucherId);
    }
}
