package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.LayoutItemOrderProductBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;

import java.text.DecimalFormat;
import java.util.List;



public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder> {

    private Context context;
    private List<OptionAndQuantity> listOption;

    public OrderProductAdapter(Context context, List<OptionAndQuantity> listOption) {
        this.context = context;
        this.listOption = listOption;
    }

    public void setListOption(List<OptionAndQuantity> listOption) {
        this.listOption = listOption;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemOrderProductBinding binding = LayoutItemOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OrderProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        OptionAndQuantity option = listOption.get(position);
        if (option == null || option.getOptionProduct() == null) {
            holder.binding.tvNameProduct.setText("Sản phẩm không tồn tại");
            holder.binding.tvNameColor.setText("Lỗi dữ liệu");
            holder.binding.tvQuantityPrice.setText("");
            holder.binding.imgProduct.setImageResource(R.drawable.error);
            return;
        }

        String productName = "Sản phẩm không tên";
        if (option.getOptionProduct().getProduct() != null) {
            productName = option.getOptionProduct().getProduct().getName();
        }
        holder.binding.tvNameProduct.setText(productName);

        DecimalFormat df = new DecimalFormat("###,###,###");
        int discountvalue = option.getDiscount_value();
        
        if (discountvalue == 0) {
            holder.binding.tvQuantityPrice.setText(option.getQuantity() + " x " + df.format(option.getOptionProduct().getPrice()) + "đ");
        } else {
            double voucher = (double) (100 - discountvalue) / 100;
            int gia = option.getOptionProduct().getPrice();
            double gia1 = gia * voucher;
            holder.binding.tvQuantityPrice.setText(option.getQuantity() + " x " + df.format(gia1) + "đ");
        }

        holder.binding.tvNameColor.setText("Loại: " + option.getOptionProduct().getNameColor());

        Glide.with(context)
                .load(option.getOptionProduct().getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.binding.imgProduct);
    }

    @Override
    public int getItemCount() {
        if(listOption != null){
            return listOption.size();
        }
        return 0;
    }

    public class OrderProductViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemOrderProductBinding binding;
        public OrderProductViewHolder(LayoutItemOrderProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
