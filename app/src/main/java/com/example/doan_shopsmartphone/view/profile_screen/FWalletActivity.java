package com.example.doan_shopsmartphone.view.profile_screen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.WalletTransaction;
import com.example.doan_shopsmartphone.model.response.WalletResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FWalletActivity extends AppCompatActivity {

    private TextView tvWalletBalance, tvEmptyTransactions;
    private RecyclerView rvTransactions;
    private Button btnTopUp;
    private ImageView imgBack;

    private List<WalletTransaction> transactionList = new ArrayList<>();
    private TransactionAdapter adapter;
    private ProgressLoadingDialog loadingDialog;

    private int currentBalance = 0;
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;
    private Dialog qrDialog;
    private boolean isPolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwallet);

        tvWalletBalance = findViewById(R.id.tv_wallet_balance);
        tvEmptyTransactions = findViewById(R.id.tv_empty_transactions);
        rvTransactions = findViewById(R.id.rv_transactions);
        btnTopUp = findViewById(R.id.btn_top_up);
        imgBack = findViewById(R.id.imgBack);

        loadingDialog = new ProgressLoadingDialog(this);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(adapter);

        imgBack.setOnClickListener(v -> finish());

        btnTopUp.setOnClickListener(v -> showTopUpDialog());

        fetchWalletInfo(true);
    }

    private void fetchWalletInfo(boolean showProgress) {
        if (showProgress) {
            loadingDialog.show();
        }
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.getWalletInfo(token).enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                if (showProgress) {
                    loadingDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    WalletResponse.WalletData data = response.body().getData();
                    currentBalance = data.getBalance();
                    
                    DecimalFormat formatter = new DecimalFormat("###,###,###");
                    tvWalletBalance.setText(formatter.format(currentBalance) + "đ");

                    transactionList.clear();
                    if (data.getTransactions() != null && !data.getTransactions().isEmpty()) {
                        transactionList.addAll(data.getTransactions());
                        tvEmptyTransactions.setVisibility(View.GONE);
                        rvTransactions.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyTransactions.setVisibility(View.VISIBLE);
                        rvTransactions.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FWalletActivity.this, "Không thể lấy thông tin ví", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                if (showProgress) {
                    loadingDialog.dismiss();
                }
                Toast.makeText(FWalletActivity.this, "Lỗi kết nối ví: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTopUpDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_top_up);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextInputEditText edtAmount = dialog.findViewById(R.id.edt_amount);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(FWalletActivity.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            if (amount < 10000) {
                Toast.makeText(FWalletActivity.this, "Số tiền nạp tối thiểu là 10.000đ", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            showWarningAndOpenQR(amount);
        });

        dialog.show();
    }

    private void showWarningAndOpenQR(int amount) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo nạp tiền")
                .setMessage("Tạm thời chưa có chức năng rút tiền, bạn có xác nhận muốn nạp tiền vào ví ko?")
                .setPositiveButton("Đồng ý", (dialog, which) -> openQRDialog(amount))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void openQRDialog(int amount) {
        qrDialog = new Dialog(this);
        qrDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        qrDialog.setContentView(R.layout.dialog_sepay_qr);
        
        Window window = qrDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView imgQrCode = qrDialog.findViewById(R.id.img_qr_code);
        TextView tvAmount = qrDialog.findViewById(R.id.tv_amount);
        TextView tvDes = qrDialog.findViewById(R.id.tv_des);
        Button btnClose = qrDialog.findViewById(R.id.btn_close);

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvAmount.setText("Số tiền: " + formatter.format(amount) + "đ");

        String userId = AccountUltil.USER.getId();
        String memo = "NAPW" + userId;
        tvDes.setText("Nội dung: " + memo);

        // Pre-filled Sepay QR URL format
        String qrUrl = "https://qr.sepay.vn/img?acc=0911193469&bank=MBBank&amount=" + amount + "&des=" + memo;
        Glide.with(this).load(qrUrl).into(imgQrCode);

        btnClose.setOnClickListener(v -> {
            stopPolling();
            qrDialog.dismiss();
        });

        qrDialog.setOnDismissListener(dialog -> stopPolling());

        qrDialog.show();
        startPolling(amount);
    }

    private void startPolling(int amount) {
        if (isPolling) return;
        isPolling = true;
        
        final int targetBalance = currentBalance + amount;

        pollRunnable = new Runnable() {
            @Override
            public void run() {
                String token = AccountUltil.BEARER + AccountUltil.getToken(FWalletActivity.this);
                BaseApi.API.getWalletInfo(token).enqueue(new Callback<WalletResponse>() {
                    @Override
                    public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                            int newBalance = response.body().getData().getBalance();
                            if (newBalance >= targetBalance || newBalance > currentBalance) {
                                // Balance updated!
                                stopPolling();
                                if (qrDialog != null && qrDialog.isShowing()) {
                                    qrDialog.dismiss();
                                }
                                Toast.makeText(FWalletActivity.this, "Nạp tiền vào ví F thành công!", Toast.LENGTH_LONG).show();
                                fetchWalletInfo(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletResponse> call, Throwable t) {
                        // Suppress failures during polling to keep user experience smooth
                    }
                });

                if (isPolling) {
                    pollHandler.postDelayed(this, 5000); // poll every 5s
                }
            }
        };

        pollHandler.postDelayed(pollRunnable, 5000);
    }

    private void stopPolling() {
        isPolling = false;
        if (pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        stopPolling();
        super.onDestroy();
    }

    // --- Transactions Adapter ---
    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        private final List<WalletTransaction> list;
        private final DecimalFormat formatter = new DecimalFormat("###,###,###");

        public TransactionAdapter(List<WalletTransaction> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WalletTransaction tx = list.get(position);
            
            // Format time
            String rawTime = tx.getCreatedAt();
            if (rawTime != null && rawTime.contains("T")) {
                // simple format from ISO
                String date = rawTime.split("T")[0];
                String time = rawTime.split("T")[1].substring(0, 5);
                holder.tvTime.setText(date + " " + time);
            } else {
                holder.tvTime.setText(rawTime);
            }

            // Set content based on type
            String type = tx.getType();
            if ("deposit".equalsIgnoreCase(type)) {
                holder.ivIcon.setImageResource(R.drawable.ic_check_circle_primary); // standard check icon
                holder.ivIcon.setBackgroundResource(R.drawable.custom_border_item_list);
                holder.ivIcon.setBackgroundColor(Color.parseColor("#E8F5E9")); // light green
                holder.ivIcon.setColorFilter(Color.parseColor("#4CAF50")); // green
                
                holder.tvTitle.setText(tx.getDescription() != null ? tx.getDescription() : "Nạp tiền vào ví F");
                holder.tvAmount.setText("+" + formatter.format(tx.getAmount()) + "đ");
                holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
            } else if ("refund".equalsIgnoreCase(type)) {
                holder.ivIcon.setImageResource(R.drawable.ic_check_circle_primary);
                holder.ivIcon.setBackgroundResource(R.drawable.custom_border_item_list);
                holder.ivIcon.setBackgroundColor(Color.parseColor("#E3F2FD")); // light blue
                holder.ivIcon.setColorFilter(Color.parseColor("#2196F3")); // blue
                
                holder.tvTitle.setText(tx.getDescription() != null ? tx.getDescription() : "Hoàn tiền hủy đơn");
                holder.tvAmount.setText("+" + formatter.format(tx.getAmount()) + "đ");
                holder.tvAmount.setTextColor(Color.parseColor("#2196F3"));
            } else { // payment
                holder.ivIcon.setImageResource(R.drawable.ic_circle_outline);
                holder.ivIcon.setBackgroundResource(R.drawable.custom_border_item_list);
                holder.ivIcon.setBackgroundColor(Color.parseColor("#FFEBEE")); // light red
                holder.ivIcon.setColorFilter(Color.parseColor("#E53935")); // red
                
                holder.tvTitle.setText(tx.getDescription() != null ? tx.getDescription() : "Thanh toán đơn hàng");
                holder.tvAmount.setText("-" + formatter.format(tx.getAmount()) + "đ");
                holder.tvAmount.setTextColor(Color.parseColor("#E53935"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle, tvTime, tvAmount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_tx_icon);
                tvTitle = itemView.findViewById(R.id.tv_tx_title);
                tvTime = itemView.findViewById(R.id.tv_tx_time);
                tvAmount = itemView.findViewById(R.id.tv_tx_amount);
            }
        }
    }
}
