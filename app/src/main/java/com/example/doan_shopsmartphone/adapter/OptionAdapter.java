package com.example.doan_shopsmartphone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.example.doan_shopsmartphone.databinding.LayoutIteamOptionProductBinding;
import com.example.doan_shopsmartphone.model.OptionProduct;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.ultil.OptionUtil;


public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder>{
    private final Context context;
    private List<OptionProduct> list;
    private OptionUtil optionUtil;
    private ObjectUtil objectUtil;
    private int selectedPosition = -1;

    public OptionAdapter(Context context, List<OptionProduct> list) {
        this.context = context;
        this.list = list;
    }

    public void setOptionUtil(OptionUtil optionUtil) {
        this.optionUtil = optionUtil;
        notifyDataSetChanged();
    }
    public void setObjectUtil(ObjectUtil objectUtil) {
        this.objectUtil = objectUtil;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataListOptionProduct(List<OptionProduct> list){
        this.list=list;
        this.selectedPosition = -1; // Reset selection on new data
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetSelection() {
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutIteamOptionProductBinding binding = LayoutIteamOptionProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OptionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        OptionProduct optionProduct = list.get(position);
        holder.binding.tvColorOption.setText(optionProduct.getNameColor());
//        holder.binding.tvHetHang1.setVisibility(View.GONE);
//        holder.binding.tvHetHang2.setVisibility(View.GONE);
        Glide.with(context).load(optionProduct.getImage()).into(holder.binding.imgIteamOption);

        if(objectUtil != null) {
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Làm mới item cũ và item mới để cập nhật lại border
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Các logic xử lý interface của bạn
                if(optionUtil != null) {
                    optionUtil.onclickOption(optionProduct);
                }
                if(objectUtil != null) {
                    objectUtil.onclickObject(optionProduct);
                }
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            optionUtil.deleteOption(optionProduct);
        });
    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {
        private final LayoutIteamOptionProductBinding binding;
        public OptionViewHolder(LayoutIteamOptionProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

