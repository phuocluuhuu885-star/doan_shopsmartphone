package com.example.doan_shopsmartphone.ultil;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.OptionOfListCart;
import com.example.doan_shopsmartphone.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;

public class ProductDetailDialogHelper {

    public static void showProductDetailDialog(Context context, OptionOfListCart option) {
        if (option == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_option_summary, null);
        dialog.setContentView(dialogView);

        ImageView imgOption = dialogView.findViewById(R.id.imgDialogOption);
        TextView tvPrice = dialogView.findViewById(R.id.tvDialogPrice);
        TextView tvStock = dialogView.findViewById(R.id.tvDialogStock);
        TextView tvSummaryTitle = dialogView.findViewById(R.id.tvSummaryTitle);
        TextView tvSummaryRam = dialogView.findViewById(R.id.tvSummaryRam);
        TextView tvSummaryIntegrity = dialogView.findViewById(R.id.tvSummaryIntegrity);
        TextView tvSummaryStorage = dialogView.findViewById(R.id.tvSummaryStorage);
        TextView tvSummaryCondition = dialogView.findViewById(R.id.tvSummaryCondition);
        TextView tvSummaryBattery = dialogView.findViewById(R.id.tvSummaryBattery);
        TextView btnMoreSpecs = dialogView.findViewById(R.id.btnMoreSpecs);
        TableLayout tableGeneralSpecs = dialogView.findViewById(R.id.tableGeneralSpecs);

        TextView tvSpecScreen = dialogView.findViewById(R.id.tvDialogSpecScreen);
        TextView tvSpecCamera = dialogView.findViewById(R.id.tvDialogSpecCamera);
        TextView tvSpecChipset = dialogView.findViewById(R.id.tvDialogSpecChipset);
        TextView tvSpecOS = dialogView.findViewById(R.id.tvDialogSpecOS);
        TextView tvSpecBattery = dialogView.findViewById(R.id.tvDialogSpecBattery);
        TextView tvSpecConnection = dialogView.findViewById(R.id.tvDialogSpecConnection);

        View layoutQuantity = dialogView.findViewById(R.id.layoutQuantity);
        Button btnAddToCart = dialogView.findViewById(R.id.btnAddToCart);

        // Hide quantity selector since it's an order item
        if (layoutQuantity != null) {
            layoutQuantity.setVisibility(View.GONE);
        }

        // Change button to "ĐÓNG"
        if (btnAddToCart != null) {
            btnAddToCart.setText("ĐÓNG");
            btnAddToCart.setOnClickListener(v -> dialog.dismiss());
        }

        // Load price
        DecimalFormat df = new DecimalFormat("###,###,###");
        double discount = (double) (100 - option.getDiscountValue()) / 100;
        int finalPrice = (int) (option.getPrice() * discount);
        tvPrice.setText(df.format(finalPrice) + " đ");

        // Hide stock info or show "Đã mua"
        if (tvStock != null) {
            tvStock.setText("Sản phẩm trong đơn hàng");
        }

        // Load image
        Glide.with(context)
                .load(option.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imgOption);

        // Load general specs from OptionOfListCart
        tvSummaryTitle.setText("Chi tiết sản phẩm (" + (option.getNameColor() != null ? option.getNameColor() : "--") + "):");
        tvSummaryRam.setText("RAM: " + (option.getRam() != null ? option.getRam() : "--"));
        tvSummaryStorage.setText("Bộ nhớ: " + (option.getStorageCapacity() != null ? option.getStorageCapacity() : "--"));
        tvSummaryIntegrity.setText("Tình trạng máy: " + (option.getIsOriginal() != null ? option.getIsOriginal() : "--"));
        tvSummaryCondition.setText("Ngoại hình: " + (option.getConditionPercent() != null ? option.getConditionPercent() + "%" : "--"));
        tvSummaryBattery.setText("Pin: " + (option.getBatteryHealth() != null ? option.getBatteryHealth() : "--"));

        // Load product specs from Product model
        Product product = option.getProduct();
        if (product != null) {
            // Screen logic
            String optionScreen = option.getScreen();
            if (!TextUtils.isEmpty(optionScreen)) {
                if (optionScreen.toLowerCase().contains("thay màn")) {
                    tvSpecScreen.setText(optionScreen);
                } else {
                    tvSpecScreen.setText(optionScreen + " (đã thay màn)");
                }
            } else {
                tvSpecScreen.setText(product.getScreen() != null ? product.getScreen() : "N/A");
            }

            tvSpecCamera.setText(product.getCamera() != null ? product.getCamera() : "N/A");
            tvSpecChipset.setText(product.getChipset() != null ? product.getChipset() : "N/A");
            tvSpecOS.setText(product.getOperatingSystem() != null ? product.getOperatingSystem() : "N/A");
            tvSpecBattery.setText(product.getBattery() != null ? product.getBattery() : "N/A");
            tvSpecConnection.setText(product.getConnection() != null ? product.getConnection() : "N/A");
        } else {
            tvSpecScreen.setText("N/A");
            tvSpecCamera.setText("N/A");
            tvSpecChipset.setText("N/A");
            tvSpecOS.setText("N/A");
            tvSpecBattery.setText("N/A");
            tvSpecConnection.setText("N/A");
        }

        // More specs toggle
        if (btnMoreSpecs != null && tableGeneralSpecs != null) {
            btnMoreSpecs.setOnClickListener(v -> {
                if (tableGeneralSpecs.getVisibility() == View.GONE) {
                    tableGeneralSpecs.setVisibility(View.VISIBLE);
                    btnMoreSpecs.setText("Thu gọn thông số ▴");
                } else {
                    tableGeneralSpecs.setVisibility(View.GONE);
                    btnMoreSpecs.setText("Xem thêm thông số ▾");
                }
            });
        }

        dialog.show();
    }
}
