package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.LayoutItemCartPayBinding;
import com.example.doan_shopsmartphone.databinding.LayoutItemDetailOderBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;

import java.text.DecimalFormat;
import java.util.List;

public class DetailOderItemAdapter extends RecyclerView.Adapter<DetailOderItemAdapter.DetailOderItemViewHolder>{
    private Context context;
    private List<OptionAndQuantity> cartList;

    public DetailOderItemAdapter(Context context, List<OptionAndQuantity> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public DetailOderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemDetailOderBinding binding = LayoutItemDetailOderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DetailOderItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailOderItemViewHolder holder, int position) {
        OptionAndQuantity cart = cartList.get(position);
        Log.e( "qwess: ",""+cart.toString() );
        holder.binding.tvProductName.setText(cart.getOptionProduct().getProduct().getName() + "");
        holder.binding.tvNameColor.setText("Phân loại: " + cart.getOptionProduct().getNameColor());
        holder.binding.tvQuantity.setText("Số lương: x" + cart.getQuantity());
        DecimalFormat df = new DecimalFormat("###,###,###");
        double checkgia = (double) (100 - cart.getOptionProduct().getDiscountValue()) / 100;
        int gia =(cart.getOptionProduct().getPrice());
        Double gia1 = gia*checkgia;
        holder.binding.tvPrice.setText(df.format(gia1) + " đ");
        Glide.with(context).load(cart.getOptionProduct().getImage()).into(holder.binding.imgProduct);
    }

    @Override
    public int getItemCount() {
        if(cartList != null) {
            return cartList.size();
        }
        return 0;
    }

    public class DetailOderItemViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemDetailOderBinding binding;
        public DetailOderItemViewHolder(LayoutItemDetailOderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
