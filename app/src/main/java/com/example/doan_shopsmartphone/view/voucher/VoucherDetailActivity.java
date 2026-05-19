package com.example.doan_shopsmartphone.view.voucher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Voucher;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoucherDetailActivity extends AppCompatActivity {

    private ImageView btnBack, btnCopy;
    private TextView tvDetailDiscountDisplay, tvDetailTitle, tvDetailCode;
    private TextView tvDetailMinOrder, tvDetailMaxDiscount, tvDetailQuantity, tvDetailExpiry, tvDetailProducts;
    private LinearLayout layoutMaxDiscount;
    private Button btnApply;
    private Voucher voucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voucher_detail);

        voucher = (Voucher) getIntent().getSerializableExtra("VOUCHER_OBJECT");
        if (voucher == null) {
            Toast.makeText(this, "Không tìm thấy thông tin voucher", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayVoucherDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCopy = findViewById(R.id.btnCopy);
        tvDetailDiscountDisplay = findViewById(R.id.tvDetailDiscountDisplay);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCode = findViewById(R.id.tvDetailCode);
        tvDetailMinOrder = findViewById(R.id.tvDetailMinOrder);
        tvDetailMaxDiscount = findViewById(R.id.tvDetailMaxDiscount);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailExpiry = findViewById(R.id.tvDetailExpiry);
        tvDetailProducts = findViewById(R.id.tvDetailProducts);
        layoutMaxDiscount = findViewById(R.id.layoutMaxDiscount);
        btnApply = findViewById(R.id.btnApply);

        btnBack.setOnClickListener(v -> finish());

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Voucher Code", voucher.getCode());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Đã sao chép mã giảm giá: " + voucher.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btnApply.setOnClickListener(v -> {
            Toast.makeText(this, "Đã áp dụng mã giảm giá!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, com.example.doan_shopsmartphone.MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayVoucherDetails() {
        tvDetailTitle.setText(voucher.getTitle());
        tvDetailCode.setText(voucher.getCode());

        // Discount display
        if (voucher.getDiscountType() == 1) {
            tvDetailDiscountDisplay.setText("GIẢM " + voucher.getDiscountValue() + "%");
        } else {
            tvDetailDiscountDisplay.setText("GIẢM " + voucher.getDiscountValue() + "k");
        }

        // Min order
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (voucher.getMinOrderValue() > 0) {
            tvDetailMinOrder.setText(formatter.format(voucher.getMinOrderValue()) + "đ");
        } else {
            tvDetailMinOrder.setText("0đ");
        }

        // Max discount
        if (voucher.getMaxDiscountValue() > 0) {
            tvDetailMaxDiscount.setText(formatter.format(voucher.getMaxDiscountValue()) + "đ");
            layoutMaxDiscount.setVisibility(View.VISIBLE);
        } else {
            if (voucher.getDiscountType() == 1) {
                tvDetailMaxDiscount.setText("Không giới hạn");
                layoutMaxDiscount.setVisibility(View.VISIBLE);
            } else {
                layoutMaxDiscount.setVisibility(View.GONE);
            }
        }

        // Quantity
        tvDetailQuantity.setText(String.valueOf(voucher.getQuantity()));

        // Expiry Date
        if (voucher.getExpiryDate() != null) {
            tvDetailExpiry.setText(formatDate(voucher.getExpiryDate()));
        } else {
            tvDetailExpiry.setText("N/A");
        }

        // Applicable products list
        if (voucher.getApplicableProducts() == null || voucher.getApplicableProducts().isEmpty()) {
            tvDetailProducts.setText("Áp dụng cho toàn bộ cửa hàng");
            tvDetailProducts.setTextColor(getResources().getColor(R.color.color_chudao));
        } else {
            StringBuilder sb = new StringBuilder();
            for (Voucher.ProductObj p : voucher.getApplicableProducts()) {
                if (p.getName() != null && !p.getName().isEmpty()) {
                    sb.append("• ").append(p.getName()).append("\n");
                } else {
                    sb.append("• Sản phẩm (ID: ").append(p.get_id()).append(")\n");
                }
            }
            tvDetailProducts.setText(sb.toString().trim());
            tvDetailProducts.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "N/A";
        }
    }
}
