package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ItemNotificationBinding;
import com.example.doan_shopsmartphone.model.Notifi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;

    private List<Notifi> notifiList;

    public NotificationAdapter(Context context, List<Notifi> notifiList) {
        this.context = context;
        this.notifiList = notifiList;
    }

    public void setNotifiList(List<Notifi> notifiList) {
        this.notifiList = notifiList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifi notification = notifiList.get(position);

        switch (notification.getType()){
            case "msg":
                holder.binding.tvTitle.setText("Bạn có tin nhắn mới từ " + notification.getSender().getUsername());
                break;
            case "delivere":
                holder.binding.tvTitle.setText("Đơn hàng đang giao");
                break;
            case "wfc":
                holder.binding.tvTitle.setText("Đơn hàng đang chờ xác nhận");
                break;
            case "wfd":
                holder.binding.tvTitle.setText("Đơn hàng đang chờ giao");
                break;
            case "delivered":
                holder.binding.tvTitle.setText("Giao hàng thành công");
                break;
            case "canceled":
                holder.binding.tvTitle.setText("Đã hủy đơn hàng");
                break;
            default:holder.binding.tvTitle.setText("Bạn có thông báo mới");
                break;
        }
        // image
        Glide.with(context)
                .load(R.drawable.avatar1)
                .placeholder(R.drawable.loading)
                .into(holder.binding.imgAvartar);

        // date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss  dd-MM-yyyy");
        Date date;
        Date dateOrder;
        try {
            date = inputFormat.parse(notification.getCreatedAt());
            dateOrder = inputFormat.parse((notification.getContent()));
        } catch (Exception e) {
            date = new Date();
            dateOrder = new Date();
        }
        holder.binding.tvDate.setText(outputFormat.format(date));
        holder.binding.tvContent.setText("thời gian đặt đơn:"+outputFormat.format(dateOrder));

        if(position == notifiList.size() -1) {
            holder.binding.line.setVisibility(View.GONE);
        } else {
            holder.binding.line.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(notifiList != null) {
            return notifiList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemNotificationBinding binding;
        public ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}


