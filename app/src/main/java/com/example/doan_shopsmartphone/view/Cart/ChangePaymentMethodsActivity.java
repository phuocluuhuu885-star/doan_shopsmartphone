package com.example.doan_shopsmartphone.view.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_shopsmartphone.R;

public class ChangePaymentMethodsActivity extends AppCompatActivity {

    private LinearLayout layoutCod, layoutZalopay;
    private ImageView ivCheckCod, ivCheckZaloPay, imgBack;
    private AppCompatButton btnConfirm;
    private int paymentMethods = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_payment_methods);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutCod = findViewById(R.id.layout_cod);
        layoutZalopay = findViewById(R.id.layout_zalopay);
        ivCheckCod = findViewById(R.id.iv_check_cod);
        ivCheckZaloPay = findViewById(R.id.iv_check_zalopay);
        btnConfirm = findViewById(R.id.btn_confirm);
        imgBack = findViewById(R.id.imgBack);

        // Lấy giá trị paymentMethods từ Intent
        paymentMethods = getIntent().getIntExtra("paymentMethods", 0);
        updateSelectionUI();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethods = 1;
                updateSelectionUI();
            }
        });

        layoutZalopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethods = 2;
                updateSelectionUI();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("paymentMethods", paymentMethods);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updateSelectionUI() {
        if (paymentMethods == 1) {
            ivCheckCod.setImageResource(R.drawable.ic_check_circle_primary);
            ivCheckZaloPay.setImageResource(R.drawable.ic_circle_outline);
        } else if (paymentMethods == 2) {
            ivCheckCod.setImageResource(R.drawable.ic_circle_outline);
            ivCheckZaloPay.setImageResource(R.drawable.ic_check_circle_primary);
        } else {
            ivCheckCod.setImageResource(R.drawable.ic_circle_outline);
            ivCheckZaloPay.setImageResource(R.drawable.ic_circle_outline);
        }
    }
}