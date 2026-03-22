package com.example.doan_shopsmartphone.ultil.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.view.oder.DetailOderActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotiFireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String orderId = "";
        if (remoteMessage.getData().size() > 0) {
            orderId = remoteMessage.getData().get("order_id");
        }

        // 2. Lấy tiêu đề và nội dung
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

        // 3. Hiển thị thông báo
        sendNotification(title, messageBody, orderId);
    }

    private void sendNotification(String title, String messageBody, String orderId) {
        String channelId = "shop_notification_channel";

        // 1. Tạo Intent thông minh: Nhấn vào sẽ mở màn hình chi tiết (nếu có ID)
        Intent intent;
        if (orderId != null && !orderId.isEmpty()) {
            // Thay 'OrderDetailActivity' bằng tên Activity chi tiết đơn hàng của bạn
            intent = new Intent(this, DetailOderActivity.class);
            intent.putExtra("ORDER_ID_KEY", orderId); // Truyền ID sang để Activity xử lý
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // FLAG_IMMUTABLE là bắt buộc từ Android 12+, dùng thêm FLAG_UPDATE_CURRENT để cập nhật dữ liệu mới
        int flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, flags);

        // 2. Build nội dung thông báo chuẩn
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logofm)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)) // Hiển thị nội dung dài
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 3. Xử lý Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Shop Smartphone Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 4. Notify với ID là thời gian để không bị đè các thông báo cũ
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }
}
