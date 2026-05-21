package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.databinding.LayoutItemCartPayBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.view.Cart.CartActivity;
import com.example.doan_shopsmartphone.view.voucher.VoucherScreen;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.view.product_screen.DetailProduct;
import com.example.doan_shopsmartphone.R;

import java.text.DecimalFormat;
import java.util.List;



public class CartPayAdapter extends RecyclerView.Adapter<CartPayAdapter.CartPayViewHolder> {
    private Context context;
    private List<OptionAndQuantity> cartList;

    public CartPayAdapter(Context context, List<OptionAndQuantity> list) {
        this.context = context;
        this.cartList = list;
    }

    public void setCartList(List<OptionAndQuantity> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartPayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemCartPayBinding binding = LayoutItemCartPayBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CartPayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartPayViewHolder holder, int position) {
        OptionAndQuantity cart = cartList.get(position);
        holder.binding.tvProductName.setText(cart.getOptionProduct().getProduct().getName() + "");
        holder.binding.tvNameColor.setText("Phân loại: " + cart.getOptionProduct().getNameColor());
        holder.binding.tvQuantity.setText("Số lương: x" + cart.getQuantity());
        DecimalFormat df = new DecimalFormat("###,###,###");
        double checkgia = (double) (100 - cart.getOptionProduct().getDiscountValue()) / 100;
            int gia = (int) (cart.getOptionProduct().getPrice());
        Double gia1 = gia*checkgia;
        holder.binding.tvPrice.setText(df.format(gia1) + " đ");
        Glide.with(context).load(cart.getOptionProduct().getImage()).into(holder.binding.imgProduct);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = cart.getOptionProduct().getProduct();
                if (product != null) {
                    Intent intent = new Intent(context, DetailProduct.class);
                    intent.putExtra("id_product", product.getId());
                    intent.putExtra("sold_quantity", String.valueOf(product.getSoldQuantity()));
                    intent.putExtra("rating_start", String.valueOf(product.getAverageRate()));
                    intent.putExtra("review_count", String.valueOf(product.getReviewCount()));
                    intent.putExtra("minPrice", product.getMinPrice());
                    context.startActivity(intent);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(cartList != null) {
            return cartList.size();
        }
        return 0;
    }

    public class CartPayViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemCartPayBinding binding;
        public CartPayViewHolder(LayoutItemCartPayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
