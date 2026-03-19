package com.example.doan_shopsmartphone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.example.doan_shopsmartphone.databinding.LayoutItemAddressBinding;
import com.example.doan_shopsmartphone.model.Info;
import com.example.doan_shopsmartphone.ultil.InfoInterface;
public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder>{

    private final Context context;
    private List<Info> infoList;
    private final InfoInterface infoInterface;

    public InfoAdapter(Context context, List<Info> infoList, InfoInterface infoInterface) {
        this.context = context;
        this.infoList = infoList;
        this.infoInterface = infoInterface;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setInfoList(List<Info> infoList) {
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemAddressBinding binding = LayoutItemAddressBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new InfoViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        Info info = infoList.get(position);
        if(info == null) {
            return;
        }
        holder.binding.tvName.setText(info.getName()  + " | ");
        holder.binding.tvAddress.setText(info.getAddress());
        holder.binding.tvPhoneNumber.setText(info.getPhoneNumber());
        // 1. Xóa listener cũ trước khi set trạng thái để tránh việc gọi vòng lặp vô tận
        holder.binding.chkChooseInfo.setOnCheckedChangeListener(null);

        // 2. Thiết lập trạng thái hiển thị
        if(info.getChecked()) {
            holder.binding.tvDefault.setVisibility(View.VISIBLE);
            holder.binding.chkChooseInfo.setChecked(true);
        } else {
            holder.binding.tvDefault.setVisibility(View.GONE);
            holder.binding.chkChooseInfo.setChecked(false);
        }

        // 3. Xử lý sự kiện click vào Checkbox (hoặc cả Item)
        holder.binding.chkChooseInfo.setOnClickListener(v -> {
            // Chỉ xử lý nếu item này chưa được chọn
            if (!info.getChecked()) {
                updateSingleSelection(position);
                infoInterface.onclickObject(info);
            }
        });
        holder.binding.chkChooseInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    if (!info.getChecked()) {
                        // 1. Cập nhật giao diện cục bộ (Local Update)
                        for (int i = 0; i < infoList.size(); i++) {
                            infoList.get(i).setChecked(i == position);
                        }
                        notifyDataSetChanged();

                        // 2. Gửi lệnh lên Server (Sync Update)
                        // Trong Activity/Fragment, bạn phải viết API để update trạng thái này vào DB
                        infoInterface.onclickObject(info);
                    }
                }
            }
        });

        holder.binding.tvUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoInterface.updateObject(info);
            }
        });
    }
    private void updateSingleSelection(int selectedPosition) {
        for (int i = 0; i < infoList.size(); i++) {
            infoList.get(i).setChecked(i == selectedPosition);
        }
        notifyDataSetChanged(); // Làm mới toàn bộ danh sách để cập nhật giao diện
    }
    @Override
    public int getItemCount() {
        if(infoList != null) {
            return infoList.size();
        }
        return 0;
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemAddressBinding binding;
        public InfoViewHolder(LayoutItemAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
