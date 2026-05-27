package com.example.doan_shopsmartphone.ultil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.view.oder.DetailOderActivity;
import com.example.doan_shopsmartphone.view.product_screen.DetailProduct;

public class NotificationUtil {
    private static final String TAG = "NotificationUtil";

    public static void sendNotification(Context context, String content) {
        showNotification(context, "Thông báo mới", content, null, null, null, null);
    }

    public static void showNotification(Context context, String title, String messageBody, String orderId, String type, String productId, String voucherId) {
        String channelId = MyApplication.CHANNEL_ID;

        // 1. Tạo Intent thông minh: Ưu tiên theo Loại và ID thực tế
        Intent intent;
        
        // Kiểm tra tính hợp lệ của ID và Loại
        boolean isWithdraw = "withdraw".equalsIgnoreCase(type);
        boolean isWallet = "wallet".equalsIgnoreCase(type);
        boolean isPromotion = "promotion".equalsIgnoreCase(type) || "voucher".equalsIgnoreCase(type) || "system".equalsIgnoreCase(type);
        boolean hasOrderId = orderId != null && !orderId.trim().isEmpty() && !"null".equalsIgnoreCase(orderId) && !"undefined".equalsIgnoreCase(orderId);
        boolean hasProductId = productId != null && !productId.trim().isEmpty() && !"null".equalsIgnoreCase(productId) && !"undefined".equalsIgnoreCase(productId);

        if (isWithdraw) {
            intent = new Intent(context, com.example.doan_shopsmartphone.view.profile_screen.WithdrawalActivity.class);
            intent.putExtra("WITHDRAWAL_ID_KEY", orderId);
        } else if (isWallet) {
            intent = new Intent(context, com.example.doan_shopsmartphone.view.profile_screen.FWalletActivity.class);
        } else if (isPromotion && !hasProductId) {
            // Đây chắc chắn là Voucher hoặc thông báo hệ thống -> Về trang chủ
            intent = new Intent(context, MainActivity.class);
            intent.putExtra("action", "open_voucher");
            intent.putExtra("VOUCHER_ID_KEY", voucherId);
        } else if (hasOrderId) {
            // Chỉ mở đơn hàng nếu thực sự có ID đơn hàng hợp lệ
            intent = new Intent(context, DetailOderActivity.class);
            intent.putExtra("ORDER_ID_KEY", orderId);
        } else if (hasProductId) {
            // Mở chi tiết sản phẩm
            intent = new Intent(context, DetailProduct.class);
            intent.putExtra("id_product", productId);
        } else {
            // Mặc định về trang chủ cho mọi trường hợp khác
            intent = new Intent(context, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // FLAG_IMMUTABLE là bắt buộc từ Android 12+, dùng thêm FLAG_UPDATE_CURRENT để cập nhật dữ liệu mới
        int flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, flags);

        // 2. Build nội dung thông báo chuẩn
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logofm)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)) // Hiển thị nội dung dài
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
