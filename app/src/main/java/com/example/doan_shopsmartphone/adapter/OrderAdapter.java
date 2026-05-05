package com.example.doan_shopsmartphone.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.LayoutItemOrderBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.response.ListComment1Response;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.ultil.TAG;
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
        if (order == null) return;

        List<String> listName = new ArrayList<>();
        List<String> listidProduct = new ArrayList<>();
        
        if (order.getProductsOrder() != null) {
            for (int i = 0; i < order.getProductsOrder().size(); i++) {
                OptionAndQuantity opq = order.getProductsOrder().get(i);
                if (opq != null && opq.getOptionProduct() != null && opq.getOptionProduct().getProduct() != null) {
                    listName.add(opq.getOptionProduct().getProduct().getName());
                    listidProduct.add(opq.getOptionProduct().getProduct().getId());
                } else {
                    listName.add("Sản phẩm không xác định");
                    listidProduct.add("");
                }
            }
        }

        holder.binding.tvOrderId.setText("Đơn hàng: " + (order.getId() != null ? order.getId() : "N/A"));
        DecimalFormat df = new DecimalFormat("###,###,###");
        holder.binding.tvTotalPrice.setText(df.format(order.getTotalPrice()) + "đ");
        holder.binding.tvStatus.setText(order.getStatus());
        holder.binding.tvQuantityTypeProduct.setText((order.getProductsOrder() != null ? order.getProductsOrder().size() : 0) + " loại sản phẩm");

        String statusText = order.getStatus();
        if (TAG.CANCELLED.equals(statusText) && !TextUtils.isEmpty(order.getReason())) {
            holder.binding.tvReason.setVisibility(View.VISIBLE);
            holder.binding.tvReason.setText("Lý do: " + order.getReason());
        } else {
            holder.binding.tvReason.setVisibility(View.GONE);
        }

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

        holder.binding.btnItem.setOnClickListener(view -> {
            if (status == 0) { // chỉ tab chờ xác nhận mới cho huỷ
                showCancelDialog(order, position);
            } else {
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
                        BaseApi.API.getReview(productid,order.getId(),AccountUltil.USER.getId()).enqueue(new Callback<ListComment1Response>() {
                            @Override
                            public void onResponse(Call<ListComment1Response> call, Response<ListComment1Response> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ListComment1Response listCommentResponse = response.body();
                                    if (listCommentResponse.getData() != null) {
                                        Toast.makeText(adapter.getContext(), "Bạn Đã Đánh Giá Sản Phẩm Này,Vui Lòng Sửa", Toast.LENGTH_SHORT).show();
                                        ratingBar.setRating(listCommentResponse.getData().getRate());
                                        commentName.setText(listCommentResponse.getData().getName());
                                        commentEditText.setText(listCommentResponse.getData().getContent());
                                        postButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String token = AccountUltil.BEARER + AccountUltil.getToken(context);
                                                String comment = commentEditText.getText().toString();
                                                String name = commentName.getText().toString();
                                                if (TextUtils.isEmpty(name)) {
                                                    Toast.makeText(context, "Vui Lòng Nhập Tên", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    float rating = ratingBar.getRating();
                                                    int selectedPosition = spinner.getSelectedItemPosition();
                                                    String productid= listidProduct.get(selectedPosition);
                                                    BaseApi.API.updateComment(token,listCommentResponse.getData().getId(),productid,order.getId(),AccountUltil.USER.getId(),comment, (int) rating).enqueue(new Callback<ServerResponse>() {
                                                        @Override
                                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                            Toast.makeText(context, "Sửa Đánh Giá Thành Công", Toast.LENGTH_SHORT).show();
                                                        }
                                                        @Override
                                                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                        }
                                                    });
                                                    dialog.dismiss();
                                                }

                                            }

                                        });
                                    } else {


                                    }
                                } else {
                                    Log.d("test gọi dữ liệu", "Response không thành công hoặc body là null");
                                    Log.d("test gọi dữ liệu", "onItemSelected: + đã vào hàm thay đổi1");

                                    postButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String token = AccountUltil.BEARER + AccountUltil.getToken(context);
                                            String comment = commentEditText.getText().toString();
                                            String name = commentName.getText().toString();
                                            float rating = ratingBar.getRating();
                                            if (TextUtils.isEmpty(name)) {
                                                Toast.makeText(context, "Vui Lòng Nhập Tên", Toast.LENGTH_SHORT).show();

                                            }else{
                                                BaseApi.API.createComment(token,productid,productid,order.getId(),AccountUltil.USER.getId(),comment,name, (int) rating).enqueue(new Callback<ServerResponse>() {
                                                    @Override
                                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                        Toast.makeText(context, "Đánh Giá Thành Công", Toast.LENGTH_SHORT).show();
                                                    }
                                                    @Override
                                                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                    }

                                                });
                                                dialog.dismiss();

                                            }


                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ListComment1Response> call, Throwable t) {
                                Log.d("bugg get d", "onFailure: "+t);
                            }
                        });

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
    // ===== [ADD] ===== dialog huỷ
    private void showCancelDialog(Order order, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_order);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        RadioGroup rgReasons = dialog.findViewById(R.id.rg_reasons);
        TextInputEditText edtReason = dialog.findViewById(R.id.edt_reason);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        ImageView btnClose = dialog.findViewById(R.id.btn_close);

        btnConfirm.setEnabled(false);

        if (edtReason != null) {
            edtReason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String reason = s == null ? "" : s.toString().trim();
                    btnConfirm.setEnabled(reason.length() >= 5);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && edtReason != null) {
                RadioButton selectedRadioButton = dialog.findViewById(checkedId);
                if (selectedRadioButton != null) {
                    // Tự điền vào ô nhập lý do (người dùng vẫn có thể sửa)
                    edtReason.setText(selectedRadioButton.getText().toString());
                }
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String reason = edtReason == null ? "" : edtReason.getText().toString().trim();
            if (reason.length() < 5) {
                Toast.makeText(context, "Lý do hủy phải từ 5 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }
            btnConfirm.setEnabled(false);
            callApiCancel(order, reason, position, dialog, btnConfirm);
        });

        dialog.show();
    }
    // ===== [ADD] ===== call API huỷ
    private void callApiCancel(Order order, String reason, int position, Dialog dialog, Button btnConfirm) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(context);

        BaseApi.API.updateOrderStatus(token, order.getId(), TAG.CANCELLED, reason)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {

                            Toast.makeText(context, "Huỷ thành công", Toast.LENGTH_SHORT).show();

                            // update UI
                            orderList.remove(position);
                            notifyItemRemoved(position);

                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Huỷ thất bại", Toast.LENGTH_SHORT).show();
                            btnConfirm.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        btnConfirm.setEnabled(true);
                    }
                });
    }
}
