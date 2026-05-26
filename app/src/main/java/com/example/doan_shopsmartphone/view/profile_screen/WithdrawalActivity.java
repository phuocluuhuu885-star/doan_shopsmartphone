package com.example.doan_shopsmartphone.view.profile_screen;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ViewPageWithdrawalAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityWithdrawalBinding;
import com.example.doan_shopsmartphone.databinding.DialogWithdrawalDetailBinding;
import com.example.doan_shopsmartphone.model.Withdrawal;
import com.example.doan_shopsmartphone.model.response.WalletResponse;
import com.example.doan_shopsmartphone.model.response.WithdrawalDetailResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawalActivity extends AppCompatActivity {

    private ActivityWithdrawalBinding binding;
    private ViewPageWithdrawalAdapter pageAdapter;
    private ProgressLoadingDialog loadingDialog;

    private final ActivityResultLauncher<Intent> createWithdrawalLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    fetchWalletBalance();
                    reloadFragmentsData();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new ProgressLoadingDialog(this);

        initView();
        initController();
        fetchWalletBalance();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void initView() {
        pageAdapter = new ViewPageWithdrawalAdapter(this);
        binding.viewPagerWithdrawal.setAdapter(pageAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(
                binding.tabWithdrawal,
                binding.viewPagerWithdrawal,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Chờ xử lý");
                    } else {
                        tab.setText("Đã xử lý");
                    }
                }
        );
        mediator.attach();
    }

    private void initController() {
        binding.imgBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });

        binding.fabCreateRequest.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateWithdrawalActivity.class);
            createWithdrawalLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        });
    }

    private void fetchWalletBalance() {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.getWalletInfo(token).enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletResponse> call, @NonNull Response<WalletResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WalletResponse walletRes = response.body();
                    if (walletRes.getCode() == 200 && walletRes.getData() != null) {
                        DecimalFormat df = new DecimalFormat("###,###,###");
                        binding.tvWalletBalance.setText(String.format("%sđ", df.format(walletRes.getData().getBalance())));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                Log.e(TAG.toString, "onFailure-getWalletInfo: " + t.getMessage());
            }
        });
    }

    private void reloadFragmentsData() {
        // Find active fragments and reload their data
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof FragmentWithdrawalList) {
                ((FragmentWithdrawalList) fragment).loadData();
            }
        }
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("WITHDRAWAL_ID_KEY")) {
            String withdrawalId = intent.getStringExtra("WITHDRAWAL_ID_KEY");
            if (!TextUtils.isEmpty(withdrawalId) && !"null".equalsIgnoreCase(withdrawalId)) {
                fetchWithdrawalDetailAndShow(withdrawalId);
            }
        }
    }

    private void fetchWithdrawalDetailAndShow(String id) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        loadingDialog.show();

        BaseApi.API.getWithdrawalDetail(token, id).enqueue(new Callback<WithdrawalDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalDetailResponse> call, @NonNull Response<WithdrawalDetailResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    WithdrawalDetailResponse detailRes = response.body();
                    if (detailRes.getCode() == 200 && detailRes.getData() != null) {
                        showWithdrawalDetailDialog(detailRes.getData());
                    } else {
                        Toast.makeText(WithdrawalActivity.this, detailRes.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Toast.makeText(WithdrawalActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(WithdrawalActivity.this, "Không thể tải chi tiết đơn rút", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WithdrawalDetailResponse> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(WithdrawalActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG.toString, "onFailure-getWithdrawalDetail: " + t.getMessage());
            }
        });
    }

    public void showWithdrawalDetailDialog(Withdrawal withdrawal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogWithdrawalDetailBinding dialogBinding = DialogWithdrawalDetailBinding.inflate(
                LayoutInflater.from(this)
        );
        builder.setView(dialogBinding.getRoot());

        dialogBinding.tvDialogName.setText(withdrawal.getName());
        dialogBinding.tvDialogBank.setText(withdrawal.getBank());
        dialogBinding.tvDialogAccountNumber.setText(withdrawal.getAccount_number());

        DecimalFormat df = new DecimalFormat("###,###,###");
        dialogBinding.tvDialogAmount.setText(String.format("%sđ", df.format(withdrawal.getAmount())));

        // Format Date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.US);
        String formattedDate = withdrawal.getCreatedAt();
        try {
            Date date = inputFormat.parse(withdrawal.getCreatedAt());
            if (date != null) {
                formattedDate = outputFormat.format(date);
            }
        } catch (Exception e) {
            // ignore
        }
        dialogBinding.tvDialogTime.setText(formattedDate);

        // Status styling
        String status = withdrawal.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            dialogBinding.tvDialogStatus.setText("Chờ duyệt");
            dialogBinding.tvDialogStatus.setTextColor(Color.parseColor("#F57C00"));
            dialogBinding.tvDialogStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            dialogBinding.layoutDialogBill.setVisibility(View.GONE);
            dialogBinding.layoutDialogRejection.setVisibility(View.GONE);
            dialogBinding.btnRecreateRequest.setVisibility(View.GONE);
        } else if ("approved".equalsIgnoreCase(status)) {
            dialogBinding.tvDialogStatus.setText("Thành công");
            dialogBinding.tvDialogStatus.setTextColor(Color.parseColor("#388E3C"));
            dialogBinding.tvDialogStatus.setBackgroundResource(R.drawable.bg_badge_approved);
            dialogBinding.layoutDialogRejection.setVisibility(View.GONE);
            dialogBinding.btnRecreateRequest.setVisibility(View.GONE);

            // Show and load bill image
            if (!TextUtils.isEmpty(withdrawal.getBill_image())) {
                dialogBinding.layoutDialogBill.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(withdrawal.getBill_image())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(dialogBinding.ivDialogBillImage);
            } else {
                dialogBinding.layoutDialogBill.setVisibility(View.GONE);
            }
        } else {
            dialogBinding.tvDialogStatus.setText("Bị từ chối");
            dialogBinding.tvDialogStatus.setTextColor(Color.parseColor("#D32F2F"));
            dialogBinding.tvDialogStatus.setBackgroundResource(R.drawable.bg_badge_rejected);
            dialogBinding.layoutDialogBill.setVisibility(View.GONE);

            // Show rejection reason
            dialogBinding.layoutDialogRejection.setVisibility(View.VISIBLE);
            dialogBinding.tvDialogRejectionReason.setText(
                    TextUtils.isEmpty(withdrawal.getRejection_reason())
                            ? "Không có lý do cụ thể"
                            : withdrawal.getRejection_reason()
            );

            // Show recreate button
            dialogBinding.btnRecreateRequest.setVisibility(View.VISIBLE);
        }

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        dialogBinding.btnDialogClose.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnRecreateRequest.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(WithdrawalActivity.this, CreateWithdrawalActivity.class);
            intent.putExtra("name", withdrawal.getName());
            intent.putExtra("bank", withdrawal.getBank());
            intent.putExtra("bank_code", withdrawal.getBank_code());
            intent.putExtra("account_number", withdrawal.getAccount_number());
            intent.putExtra("amount", withdrawal.getAmount());
            createWithdrawalLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
