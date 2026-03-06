package com.example.doan_shopsmartphone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.databinding.LayoutItemProductBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private Context context;
    private List<Product> productList;
    private List<Product> filteredItems;
    private ObjectUtil objectUtil;

    public ProductAdapter(Context context, List<Product> productList, ObjectUtil objectUtil) {
        this.context = context;
        this.productList = productList;
        this.objectUtil = objectUtil;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        this.filteredItems = new ArrayList<>(productList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemProductBinding binding = LayoutItemProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.binding.tvName.setText(product.getName());
        DecimalFormat df = new DecimalFormat("###,###,###");
        holder.binding.tvPrice.setText(df.format(product.getMinPrice()) + " đ");


        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkfind ", "onClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        if(productList != null) {
            return productList.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private LayoutItemProductBinding binding;
        public ProductViewHolder(LayoutItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterItem(String query) {

    }
}
