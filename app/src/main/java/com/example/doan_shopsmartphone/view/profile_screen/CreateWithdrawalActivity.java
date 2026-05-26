package com.example.doan_shopsmartphone.view.profile_screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityCreateWithdrawalBinding;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.model.response.WalletResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.ArrayAdapter;

public class CreateWithdrawalActivity extends AppCompatActivity {

    public static class BankItem {
        public String name;
        public String code;

        public BankItem(String name, String code) {
            this.name = name;
            this.code = code;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    private static final BankItem[] POPULAR_BANKS = new BankItem[]{
            new BankItem("Vietcombank", "vietcombank"),
            new BankItem("Techcombank", "techcombank"),
            new BankItem("MBBank", "mbbank"),
            new BankItem("VietinBank", "vietinbank"),
            new BankItem("BIDV", "bidv"),
            new BankItem("Agribank", "agribank"),
            new BankItem("ACB", "acb"),
            new BankItem("VPBank", "vpbank"),
            new BankItem("TPBank", "tpbank"),
            new BankItem("Sacombank", "sacombank"),
            new BankItem("VIB", "vib")
    };

    private ActivityCreateWithdrawalBinding binding;
    private ProgressLoadingDialog loadingDialog;
    private double currentBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new ProgressLoadingDialog(this);

        initController();
        setupBankSpinner();
        fetchWalletBalance();
        checkIntentExtras();
    }

    private void initController() {
        binding.imgBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(com.example.doan_shopsmartphone.R.anim.slide_in_right, com.example.doan_shopsmartphone.R.anim.slide_out_right);
        });

        binding.btnWithdrawalSubmit.setOnClickListener(v -> submitWithdrawalRequest());
    }

    private void setupBankSpinner() {
        ArrayAdapter<BankItem> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, POPULAR_BANKS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnWithdrawalBank.setAdapter(adapter);
    }

    private void fetchWalletBalance() {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        BaseApi.API.getWalletInfo(token).enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletResponse> call, @NonNull Response<WalletResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WalletResponse walletRes = response.body();
                    if (walletRes.getCode() == 200 && walletRes.getData() != null) {
                        currentBalance = walletRes.getData().getBalance();
                        DecimalFormat df = new DecimalFormat("###,###,###");
                        binding.tvWalletBalanceHint.setText(String.format("Số dư ví khả dụng: %sđ", df.format(currentBalance)));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                Log.e(TAG.toString, "onFailure-getWalletInfo: " + t.getMessage());
            }
        });
    }

    private void checkIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String bank = intent.getStringExtra("bank");
            String bankCode = intent.getStringExtra("bank_code");
            String accountNumber = intent.getStringExtra("account_number");
            double amount = intent.getDoubleExtra("amount", 0);

            if (!TextUtils.isEmpty(name)) {
                binding.edtWithdrawalName.setText(name);
            }
            if (!TextUtils.isEmpty(bankCode) || !TextUtils.isEmpty(bank)) {
                String target = !TextUtils.isEmpty(bankCode) ? bankCode : bank;
                for (int i = 0; i < POPULAR_BANKS.length; i++) {
                    if (POPULAR_BANKS[i].code.equalsIgnoreCase(target) || POPULAR_BANKS[i].name.equalsIgnoreCase(target)) {
                        binding.spnWithdrawalBank.setSelection(i);
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(accountNumber)) {
                binding.edtWithdrawalAccountNumber.setText(accountNumber);
            }
            if (amount > 0) {
                // Display amount as integer
                DecimalFormat df = new DecimalFormat("#");
                binding.edtWithdrawalAmount.setText(df.format(amount));
            }
        }
    }

    private void submitWithdrawalRequest() {
        String name = binding.edtWithdrawalName.getText().toString().trim();
        BankItem selectedBank = (BankItem) binding.spnWithdrawalBank.getSelectedItem();
        if (selectedBank == null) {
            Toast.makeText(this, "Vui lòng chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        String bankName = selectedBank.name;
        String bankCode = selectedBank.code;
        String accountNumber = binding.edtWithdrawalAccountNumber.getText().toString().trim();
        String amountStr = binding.edtWithdrawalAmount.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(accountNumber) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 1000) {
            Toast.makeText(this, "Số tiền rút tối thiểu là 1,000đ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > currentBalance) {
            Toast.makeText(this, "Số dư ví F-Wallet không đủ để rút số tiền này", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        loadingDialog.show();

        BaseApi.API.createWithdrawal(token, name, bankName, bankCode, accountNumber, amount).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ServerResponse serverResponse = response.body();
                    if (serverResponse.getCode() == 201 || serverResponse.getCode() == 200) {
                        Toast.makeText(CreateWithdrawalActivity.this, "Gửi đơn rút tiền thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CreateWithdrawalActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Toast.makeText(CreateWithdrawalActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(CreateWithdrawalActivity.this, "Gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(CreateWithdrawalActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG.toString, "onFailure-createWithdrawal: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(com.example.doan_shopsmartphone.R.anim.slide_in_right, com.example.doan_shopsmartphone.R.anim.slide_out_right);
    }
}
