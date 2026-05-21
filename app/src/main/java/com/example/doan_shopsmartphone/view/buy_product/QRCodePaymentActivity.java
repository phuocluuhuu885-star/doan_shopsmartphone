package com.example.doan_shopsmartphone.view.buy_product;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.model.response.store.DetailBills;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.view.success_screen.OrderSuccessActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRCodePaymentActivity extends AppCompatActivity {

    private static final long COUNTDOWN_MILLIS = 15 * 60 * 1000L; // 15 phút
    private static final String TAG = "QRPayment";

    private ImageView imgBack, imgQrCode;
    private TextView tvCountdown, tvOrderId, tvTotalPrice, tvOrderStatus;
    private AppCompatButton btnCancel, btnContinue, btnSaveQr;
    private LinearLayout layoutProducts;

    private CountDownTimer countDownTimer;
    private String orderId;
    private int totalPrice;
    private boolean isCancelled = false; // tránh gọi cancel 2 lần

    private Handler pollHandler = new Handler();
    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            checkOrderStatus();
            pollHandler.postDelayed(this, 5000); // 5 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment);

        // Bind views
        imgBack = findViewById(R.id.imgBack);
        imgQrCode = findViewById(R.id.img_qr_code);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        btnCancel = findViewById(R.id.btn_cancel);
        btnContinue = findViewById(R.id.btn_continue);
        btnSaveQr = findViewById(R.id.btn_save_qr);
        layoutProducts = findViewById(R.id.layout_products);

        // Nút Tiếp tục bị mờ và disabled mặc định
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        // Lấy dữ liệu từ Intent
        orderId = getIntent().getStringExtra("orderId");
        totalPrice = getIntent().getIntExtra("totalPrice", 0);

        // Hiển thị thông tin
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvTotalPrice.setText(formatter.format(totalPrice) + " đ");
        if (orderId != null && orderId.length() > 8) {
            tvOrderId.setText("..." + orderId.substring(orderId.length() - 8));
        } else {
            tvOrderId.setText(orderId != null ? orderId : "---");
        }

        // Hiển thị danh sách sản phẩm
        if (CartUtil.listCartCheck != null && !CartUtil.listCartCheck.isEmpty()) {
            for (OptionAndQuantity item : CartUtil.listCartCheck) {
                if (item.getOptionProduct() != null && item.getOptionProduct().getProduct() != null) {
                    LinearLayout itemLayout = new LinearLayout(this);
                    itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 4, 0, 4);
                    itemLayout.setLayoutParams(layoutParams);

                    TextView tvName = new TextView(this);
                    LinearLayout.LayoutParams lpName = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    tvName.setLayoutParams(lpName);
                    String name = item.getOptionProduct().getProduct().getName();
                    if (item.getOptionProduct().getNameColor() != null && !item.getOptionProduct().getNameColor().isEmpty()) {
                        name += " (" + item.getOptionProduct().getNameColor() + ")";
                    }
                    tvName.setText(name);
                    tvName.setTextColor(Color.parseColor("#212121"));
                    tvName.setTextSize(13);

                    TextView tvQtyPrice = new TextView(this);
                    tvQtyPrice.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    double price = item.getOptionProduct().getPrice();
                    double discount = item.getOptionProduct().getDiscountValue();
                    double finalPrice = price * (1 - discount / 100);
                    tvQtyPrice.setText(item.getQuantity() + " x " + formatter.format(finalPrice) + " đ");
                    tvQtyPrice.setTextColor(Color.parseColor("#757575"));
                    tvQtyPrice.setTextSize(13);

                    itemLayout.addView(tvName);
                    itemLayout.addView(tvQtyPrice);
                    layoutProducts.addView(itemLayout);
                }
            }
        }

        // Load QR Code
        String qrUrl = "https://api.vietqr.io/image/971025-0911193469-lUyQ2FF.jpg"
                + "?accountName=NGUYEN%20QUANG%20THANG"
                + "&amount=" + totalPrice
                + "&addInfo=THANH%20TOAN%20DON%20HANG";
        Glide.with(this).load(qrUrl).into(imgQrCode);

        // Bắt đầu đếm ngược
        startCountdown();

        // Bắt đầu polling kiểm tra trạng thái đơn hàng mỗi 5s
        pollHandler.post(pollRunnable);

        // Nút Hủy
        btnCancel.setOnClickListener(v -> cancelOrder());

        // Nút Tiếp tục
        btnContinue.setOnClickListener(v -> removeCartItems());

        // Nút Lưu QR
        btnSaveQr.setOnClickListener(v -> saveQRCode());

        // Back button
        imgBack.setOnClickListener(v -> cancelOrder());
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("00:00");
                if (!isCancelled) {
                    Toast.makeText(QRCodePaymentActivity.this,
                            "Hết thời gian thanh toán, đơn hàng đã bị hủy.", Toast.LENGTH_LONG).show();
                    // Server sẽ tự động hủy sau 15 phút
                    isCancelled = true;
                    finish();
                }
            }
        }.start();
    }

    private void checkOrderStatus() {
        if (orderId == null || isCancelled) return;

        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.getDetailBill(token, orderId).enqueue(new Callback<DetailBills>() {
            @Override
            public void onResponse(Call<DetailBills> call, Response<DetailBills> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body().getResult();
                    if (order != null) {
                        String status = order.getStatus();
                        boolean isPaid = order.isPayment_status() || "Đã thanh toán".equals(status);

                        if (isPaid) {
                            // Dừng đếm ngược và dừng polling
                            pollHandler.removeCallbacks(pollRunnable);
                            if (countDownTimer != null) countDownTimer.cancel();

                            // Cập nhật trạng thái sang Đã thanh toán màu xanh lá
                            tvOrderStatus.setText("Đã thanh toán");
                            tvOrderStatus.setTextColor(Color.parseColor("#4CAF50"));

                            // Bật sáng nút Tiếp tục
                            btnContinue.setEnabled(true);
                            btnContinue.setAlpha(1.0f);

                            // Vô hiệu hóa nút Hủy
                            btnCancel.setEnabled(false);
                            btnCancel.setAlpha(0.5f);

                            Toast.makeText(QRCodePaymentActivity.this, "Đã nhận thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            tvOrderStatus.setText("Chờ thanh toán");
                            tvOrderStatus.setTextColor(Color.parseColor("#FF9800"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DetailBills> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi kiểm tra trạng thái: " + t.getMessage());
            }
        });
    }

    private void saveQRCode() {
        Drawable drawable = imgQrCode.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Payment_" + (orderId != null ? orderId : System.currentTimeMillis()) + ".jpg");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES);
                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
                }

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            getContentResolver().update(uri, values, null, null);
                        }
                        Toast.makeText(this, "Đã lưu mã QR vào thư viện ảnh", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi khi lưu ảnh QR: " + e.getMessage());
                        Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Hình ảnh QR trống", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Chưa tải xong mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelOrder() {
        if (isCancelled) return;
        isCancelled = true;

        if (countDownTimer != null) countDownTimer.cancel();
        pollHandler.removeCallbacks(pollRunnable);

        if (orderId == null) {
            finish();
            return;
        }

        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.cancelOrderQR(token, orderId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Hủy đơn hàng QR thành công: " + response.body().getMessage());
                    Toast.makeText(QRCodePaymentActivity.this, "Đơn hàng đã được hủy thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Lỗi khi hủy đơn hàng QR: " + response.code());
                }
                finish();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi hủy đơn hàng QR: " + t.getMessage());
                finish();
            }
        });
    }

    private void removeCartItems() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final int[] deletedCount = {0};
        final int total = CartUtil.listCartCheck.size();

        if (total == 0) {
            goToSuccess();
            return;
        }

        for (int i = 0; i < total; i++) {
            int position = i;
            executorService.execute(() -> {
                String token = AccountUltil.BEARER + AccountUltil.getToken(QRCodePaymentActivity.this);
                String cartId = CartUtil.listCartCheck.get(position).getId();
                BaseApi.API.deleteCartItem(token, cartId).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        synchronized (deletedCount) {
                            deletedCount[0]++;
                            if (deletedCount[0] >= total) {
                                runOnUiThread(() -> goToSuccess());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        synchronized (deletedCount) {
                            deletedCount[0]++;
                            if (deletedCount[0] >= total) {
                                runOnUiThread(() -> goToSuccess());
                            }
                        }
                    }
                });
            });
        }
    }

    private void goToSuccess() {
        CartUtil.listCart.removeAll(CartUtil.listCartCheck);
        CartUtil.listCartCheck.clear();
        Intent intent = new Intent(QRCodePaymentActivity.this, OrderSuccessActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pollHandler.removeCallbacks(pollRunnable);
        if (countDownTimer != null) countDownTimer.cancel();

        if (!isCancelled && orderId != null) {
            isCancelled = true;
            // Gửi request hủy đồng bộ trên thread phụ
            new Thread(() -> {
                try {
                    String token = AccountUltil.BEARER + AccountUltil.getToken(QRCodePaymentActivity.this);
                    BaseApi.API.cancelOrderQR(token, orderId).execute();
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi hủy đơn hàng QR trong onDestroy: " + e.getMessage());
                }
            }).start();
        }
    }
}
