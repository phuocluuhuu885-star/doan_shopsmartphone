package com.example.doan_shopsmartphone.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.LayoutItemOrderBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OrderProductAdapter orderProductAdapter;
    private ObjectUtil objectUtil;
    private int status = 0; // 1, 2, 3, 4 ứng với thứ tự hiển thị trên tab
    private int maxVisibleItems = 2; // Số lượng sản phẩm hiển thị ban đầu

    private boolean isExpanded = false;//Lưu trữ trạng thái hiện tại của danh sách

    public OrderAdapter(Context context, List<Order> orderList, ObjectUtil objectUtil, int status) {
        this.context = context;
        this.orderList = orderList;
        this.objectUtil = objectUtil;
        this.status = status;
    }

    public void setListOrder(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemOrderBinding binding = LayoutItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
            List<String> listName = new ArrayList<>();
            List<String> listidProduct = new ArrayList<>();
        for(int i=0;i<order.getProductsOrder().size();i++){
            listName.add(order.getProductsOrder().get(i).getOptionProduct().getProduct().getName());
            listidProduct.add(order.getProductsOrder().get(i).getOptionProduct().getProduct().getId());

        }
        holder.binding.tvOrderId.setText("Đơn hàng: " + order.getId());
        DecimalFormat df = new DecimalFormat("###,###,###");
        holder.binding.tvTotalPrice.setText(df.format(order.getTotalPrice()) + "");
        holder.binding.tvStatus.setText(order.getStatus());
        holder.binding.tvQuantityTypeProduct.setText(order.getProductsOrder().size() + " loại sản phẩm");


        if (status == 0) {
            holder.binding.btnItem.setText("Hủy hàng");
            int color = Color.parseColor("#FFCC00");
            holder.binding.tvStatus.setTextColor(color);
            holder.binding.btnreview.setVisibility(View.GONE);

        } else if (status == 1) {
            holder.binding.btnItem.setVisibility(View.GONE);
            holder.binding.btnreview.setVisibility(View.GONE);

        } else if (status == 2) {
            holder.binding.btnItem.setVisibility(View.GONE);
            holder.binding.btnreview.setVisibility(View.GONE);

        } else if (status == 3) {
            holder.binding.btnreview.setVisibility(View.VISIBLE);
            holder.binding.btnItem.setText("Mua lại");
        } else if (status == 4) {
            holder.binding.btnItem.setText("Mua lại");
            holder.binding.btnreview.setVisibility(View.GONE);

            holder.binding.tvStatus.setTextColor(Color.GRAY);
        }

        holder.binding.btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectUtil.onclickObject(order);
            }
        });
        holder.binding.btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo dialog
                Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                dialog.setContentView(R.layout.layout_modal_review);
                Spinner spinner = dialog.findViewById(R.id.spinnerName);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                ImageView btn_back = dialog.findViewById(R.id.btn_back_review);
                TextInputEditText commentEditText = dialog.findViewById(R.id.edt_commentReview);
                TextInputEditText commentName = dialog.findViewById(R.id.edt_commentName);
                Button postButton = dialog.findViewById(R.id.btnPost);

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        commentName.setText("");
                        commentEditText.setText("");
                        ratingBar.setRating(5);
                        String productid= listidProduct.get(position);
                        Log.d("test gọi dữ liệu", "prduct"+productid+"order"+order.getId()+"user"+AccountUltil.USER.getId());


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                // Hiển thị dialog
                dialog.show();
            }
        });

        if (order.getProductsOrder().size() > maxVisibleItems && !isExpanded) {
            // Hiển thị chỉ 2 mục đầu tiên
            setRcvProduct(holder.binding, order, maxVisibleItems);

            holder.binding.tvSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Đảo ngược trạng thái isExpanded
                    isExpanded = !isExpanded;
                    Log.d("Expanded status", "onClick Seemore: " + isExpanded);
                    // Hiển thị toàn bộ danh sách sản phẩm nếu isExpanded là true, ngược lại hiển thị chỉ 2 mục đầu tiên
                    if (!isExpanded) {
                        setRcvProduct(holder.binding, order, order.getProductsOrder().size());
                        holder.binding.tvSeeMore.setText("Thu gọn");


                    } else {
//                        setRcvProduct(holder.binding, order, maxVisibleItems);
//                        holder.binding.tvSeeMore.setText("Xem thêm");
                        setRcvProduct(holder.binding, order, maxVisibleItems);
                        holder.binding.tvSeeMore.setText("Xem thêm");

                    }
                    // Cập nhật giao diện
                    // notifyItemChanged(holder.getAdapterPosition());
                }
            });
        } else {
            //Ds sản phẩm <2 hiển thị toàn bộ sản phẩm và ẩn nút xem thêm
            holder.binding.tvSeeMore.setVisibility(View.GONE);
            setRcvProduct(holder.binding, order, order.getProductsOrder().size());
        }
    }
    private void setRcvProduct(LayoutItemOrderBinding binding, Order order, int maxVisibleItems) {
        List<OptionAndQuantity> productList = order.getProductsOrder();
        List<OptionAndQuantity> visibleProducts = productList.subList(0, Math.min(productList.size(), maxVisibleItems));

        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(context, visibleProducts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcvOrderDetail.setLayoutManager(layoutManager);
        binding.rcvOrderDetail.setAdapter(orderProductAdapter);
    }

    @Override
    public int getItemCount() {
        if(orderList != null){
            return  orderList.size();
        }
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        LayoutItemOrderBinding binding;
        public OrderViewHolder(LayoutItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
