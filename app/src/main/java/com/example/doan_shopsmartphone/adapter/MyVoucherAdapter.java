package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.view.voucher.VoucherDetailActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyVoucherAdapter extends RecyclerView.Adapter<MyVoucherAdapter.ViewHolder> {

    private final Context context;
    private final List<Voucher> voucherList;

    public MyVoucherAdapter(Context context, List<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_my_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        holder.tvTitle.setText(voucher.getTitle());
        holder.tvCode.setText(voucher.getCode());

        // Discount value display
        if (voucher.getDiscountType() == 1) {
            holder.tvDiscountValue.setText("GIẢM " + voucher.getDiscountValue() + "%");
        } else {
            holder.tvDiscountValue.setText("GIẢM " + voucher.getDiscountValue() + "k");
        }

        // Min order value
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (voucher.getMinOrderValue() > 0) {
            holder.tvMinOrder.setText("Đơn tối thiểu: " + formatter.format(voucher.getMinOrderValue()) + "đ");
        } else {
            holder.tvMinOrder.setText("Đơn tối thiểu: 0đ");
        }

        // Expiry Date
        if (voucher.getExpiryDate() != null) {
            holder.tvExpiryDate.setText("HSD: " + formatDate(voucher.getExpiryDate()));
        } else {
            holder.tvExpiryDate.setText("HSD: N/A");
        }

        // Click to view details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VoucherDetailActivity.class);
            intent.putExtra("VOUCHER_OBJECT", voucher);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return voucherList != null ? voucherList.size() : 0;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCode, tvMinOrder, tvExpiryDate, tvDiscountValue;
        ImageView ivArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvMinOrder = itemView.findViewById(R.id.tvMinOrder);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvDiscountValue = itemView.findViewById(R.id.tvDiscountValue);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}
