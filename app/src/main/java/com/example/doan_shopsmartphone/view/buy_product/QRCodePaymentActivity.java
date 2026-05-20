package com.example.doan_shopsmartphone.view.buy_product;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.view.success_screen.OrderSuccessActivity;

import java.io.IOException;
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
    private TextView tvCountdown, tvOrderId, tvTotalPrice;
    private AppCompatButton btnCancel, btnContinue;

    private CountDownTimer countDownTimer;
    private String orderId;
    private int totalPrice;
    private boolean isCancelled = false; // tránh gọi cancel 2 lần

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
        btnCancel = findViewById(R.id.btn_cancel);
        btnContinue = findViewById(R.id.btn_continue);

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

        // Load QR Code
        String qrUrl = "https://api.vietqr.io/image/971025-0911193469-lUyQ2FF.jpg"
                + "?accountName=NGUYEN%20QUANG%20THANG"
                + "&amount=" + totalPrice
                + "&addInfo=THANH%20TOAN%20DON%20HANG";
        Glide.with(this).load(qrUrl).into(imgQrCode);

        // Bắt đầu đếm ngược
        startCountdown();

        // Nút Hủy
        btnCancel.setOnClickListener(v -> cancelOrder());

        // Nút Tiếp tục (xác nhận đã thanh toán)
        btnContinue.setOnClickListener(v -> confirmOrder());

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
                    // Server sẽ tự động hủy sau 15 phút (scheduleQROrderCleanup)
                    isCancelled = true;
                    finish();
                }
            }
        }.start();
    }

    private void cancelOrder() {
        if (isCancelled) return;
        isCancelled = true;

        if (countDownTimer != null) countDownTimer.cancel();

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

    private void confirmOrder() {
        if (orderId == null) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (countDownTimer != null) countDownTimer.cancel();
        isCancelled = true; // prevent timeout handler from running

        btnContinue.setEnabled(false);
        btnCancel.setEnabled(false);

        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.confirmOrderQR(token, orderId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && (response.body().getCode() == 200 || response.body().getCode() == 201)) {
                    Toast.makeText(QRCodePaymentActivity.this,
                            "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa giỏ hàng
                    removeCartItems();
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                        JSONObject json = new JSONObject(err);
                        Toast.makeText(QRCodePaymentActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(QRCodePaymentActivity.this, "Xác nhận thất bại", Toast.LENGTH_SHORT).show();
                    }
                    btnContinue.setEnabled(true);
                    btnCancel.setEnabled(true);
                    isCancelled = false;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi xác nhận QR: " + t.getMessage());
                Toast.makeText(QRCodePaymentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                btnContinue.setEnabled(true);
                btnCancel.setEnabled(true);
                isCancelled = false;
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
        // Nếu activity bị destroy mà chưa cancel (người dùng tắt app hoàn toàn),
        // server tự cancel sau 15 phút. Nếu muốn cancel ngay khi swipe-off:
        // isFinishing() = true chỉ khi finish() được gọi chủ động.
        if (!isFinishing() && !isCancelled && orderId != null) {
            // App bị tắt hoàn toàn (process kill) - server sẽ tự cleanup sau 15 phút
            Log.d(TAG, "Activity bị destroy, server sẽ tự hủy đơn hàng sau 15 phút.");
        }
    }
}
