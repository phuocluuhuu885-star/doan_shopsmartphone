package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.VoucherDetail;

import java.util.List;



public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder>{
    Context context;
    List<VoucherDetail> list;

    public VoucherAdapter(Context context, List<VoucherDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_voucher,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoucherDetail voucher = list.get(position);
        if (voucher.getDiscountType()==1){
            holder.textSale.setText(voucher.getDiscountValue()+"%");
        }else {
            holder.textSale.setText(voucher.getDiscountValue()+"K");
        }


    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textSale;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSale = itemView.findViewById(R.id.textVoucher);
        }
    }
}
