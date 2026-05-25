package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.LayoutItemWithdrawalBinding;
import com.example.doan_shopsmartphone.model.Withdrawal;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.ViewHolder> {
    private final Context context;
    private List<Withdrawal> withdrawalList;
    private final OnWithdrawalClickListener listener;

    public interface OnWithdrawalClickListener {
        void onWithdrawalClick(Withdrawal withdrawal);
    }

    public WithdrawalAdapter(Context context, List<Withdrawal> withdrawalList, OnWithdrawalClickListener listener) {
        this.context = context;
        this.withdrawalList = withdrawalList;
        this.listener = listener;
    }

    public void setWithdrawalList(List<Withdrawal> withdrawalList) {
        this.withdrawalList = withdrawalList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemWithdrawalBinding binding = LayoutItemWithdrawalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Withdrawal withdrawal = withdrawalList.get(position);

        holder.binding.tvWithdrawalBankInfo.setText(String.format("%s - %s", withdrawal.getBank(), withdrawal.getAccount_number()));

        DecimalFormat df = new DecimalFormat("###,###,###");
        holder.binding.tvWithdrawalAmount.setText(String.format("-%sđ", df.format(withdrawal.getAmount())));

        // Format date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss  dd-MM-yyyy", Locale.US);
        String formattedDate = withdrawal.getCreatedAt();
        try {
            Date date = inputFormat.parse(withdrawal.getCreatedAt());
            if (date != null) {
                formattedDate = outputFormat.format(date);
            }
        } catch (Exception e) {
            // ignore, use original string
        }
        holder.binding.tvWithdrawalTime.setText(formattedDate);

        // Status text and color
        String status = withdrawal.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            holder.binding.tvWithdrawalStatus.setText("Chờ duyệt");
            holder.binding.tvWithdrawalStatus.setTextColor(Color.parseColor("#F57C00")); // Orange
        } else if ("approved".equalsIgnoreCase(status)) {
            holder.binding.tvWithdrawalStatus.setText("Thành công");
            holder.binding.tvWithdrawalStatus.setTextColor(Color.parseColor("#388E3C")); // Green
        } else {
            holder.binding.tvWithdrawalStatus.setText("Bị từ chối");
            holder.binding.tvWithdrawalStatus.setTextColor(Color.parseColor("#D32F2F")); // Red
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWithdrawalClick(withdrawal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return withdrawalList != null ? withdrawalList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LayoutItemWithdrawalBinding binding;

        public ViewHolder(LayoutItemWithdrawalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
