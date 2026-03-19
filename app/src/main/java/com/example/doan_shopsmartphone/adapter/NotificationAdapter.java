package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ItemNotificationBinding;
import com.example.doan_shopsmartphone.model.Notifi;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.response.UpdateStatusResponse;
import com.example.doan_shopsmartphone.model.response.store.DetailBills;
import com.example.doan_shopsmartphone.ultil.AccountUltil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        //Xử lí background cho thông báo chưa đọc/đã đọc
        if ("unread".equals(notification.getStatus())) {
            // NẾU CHƯA ĐỌC:
            // Đổi màu nền thành xanh nhạt
            holder.binding.bgItem.setBackgroundColor(ContextCompat.getColor(context, R.color.color_noti));
            // Có thể cho chữ tiêu đề in đậm để nổi bật hơn
            holder.binding.tvTitle.setTypeface(null, Typeface.BOLD);
        } else {
            // NẾU ĐÃ ĐỌC:
            // Đổi màu nền về trắng
            holder.binding.bgItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            // Chữ tiêu đề để bình thường
            holder.binding.tvTitle.setTypeface(null, Typeface.NORMAL);
        }

        switch (notification.getType()){
            case "msg":
                holder.binding.tvDate.setText("Bạn có tin nhắn mới từ " + notification.getSender().getUsername());
                break;
            case "delivere":
                holder.binding.tvDate.setText("Đang giao hàng");
                break;
            case "wfc":
                holder.binding.tvDate.setText("đang chờ xác nhận");
                break;
            case "wfd":
                holder.binding.tvDate.setText("Đang chờ giao hàng");
                break;
            case "delivered":
                holder.binding.tvDate.setText("Giao hàng thành công");
                break;
            case "canceled":
                holder.binding.tvDate.setText("Đã hủy");
                holder.binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.red));
                break;
            default:holder.binding.tvDate.setText("Bạn có thông báo mới");
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
        holder.binding.tvTitle.setText("Đơn hàng: #" + notification.getOrder_id().substring(notification.getOrder_id().length() - 6)+" đã đặt thành công");
        holder.binding.tvContent.setText("Thời gian đặt đơn:"+outputFormat.format(dateOrder));

        if(position == notifiList.size() -1) {
            holder.binding.line.setVisibility(View.GONE);
        } else {
            holder.binding.line.setVisibility(View.VISIBLE);
        }
        String token = AccountUltil.BEARER + AccountUltil.getToken(context);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApi.API.getDetailBill(token,notification.getOrder_id()).enqueue(new Callback<DetailBills>() {
                    @Override
                    public void onResponse(Call<DetailBills> call, Response<DetailBills> response) {
                        if(response.isSuccessful()){
                            DetailBills detailBills = response.body();
                            Order resultBuil = detailBills.getResult();
                            showCustomDialog(context,resultBuil);

                        }
                    }

                    @Override
                    public void onFailure(Call<DetailBills> call, Throwable t) {

                    }
                });

                BaseApi.API.updateStatusNotifi(notification.getId()).enqueue(new Callback<UpdateStatusResponse>() {
                    @Override
                    public void onResponse(Call<UpdateStatusResponse> call, Response<UpdateStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getCode() == 200) {
                                // 2. Cập nhật giao diện local: Đổi trạng thái biến trong list thành "read"
                                notification.setStatus("read");
                                notifyItemChanged(holder.getAdapterPosition());

                                // 3. Cập nhật lại số Badge ở Bottom Navigation (MainActivity)
                                if (context instanceof MainActivity) {
                                    ((MainActivity) context).fetchUnreadCount();
                                }

                                Log.d("API_SUCCESS", "Đã cập nhật trạng thái thông báo thành công");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateStatusResponse> call, Throwable t) {
                        Log.e("API_ERROR", "Lỗi kết nối: " + t.getMessage());
                    }
                });
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

    public static void showCustomDialog(Context context, Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_notifi_dialog, null);
        builder.setView(dialogView);
        RecyclerView rcvOrderDetail = dialogView.findViewById(R.id.rcvOrderDetail);
        TextView tvOrderId = dialogView.findViewById(R.id.tvOrderId);
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
        TextView tvTotalPrice = dialogView.findViewById(R.id.tvTotalPrice);
        TextView btnItem = dialogView.findViewById(R.id.btnItem);

        DecimalFormat df = new DecimalFormat("###,###,###");
        tvTotalPrice.setText(df.format(order.getTotalPrice()));
        tvOrderId.setText("Đơn hàng: " + order.getId());
        tvStatus.setText(order.getStatus());
        OrderProductAdapter   orderProductAdapter = new OrderProductAdapter(context, order.getProductsOrder());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rcvOrderDetail.setLayoutManager(layoutManager);
        rcvOrderDetail.setAdapter(orderProductAdapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}


