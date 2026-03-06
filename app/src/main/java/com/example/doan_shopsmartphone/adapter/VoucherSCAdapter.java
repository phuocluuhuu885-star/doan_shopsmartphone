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

import java.util.List;



public class VoucherSCAdapter extends RecyclerView.Adapter<VoucherSCAdapter.VoucherViewHolder>{
    private Context context;
    private List<Voucher> list;

    public VoucherSCAdapter(Context context, List<Voucher> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher store = list.get(position);
        holder.tvSale.setText(store.getSale());
        holder.tvprice.setText(store.getPrice());
        holder.tvDate.setText(store.getDate());
        holder.name.setText(store.getNameVoucher());

    }

    @Override
    public int getItemCount() {
        if(list != null){
            return  list.size();
        }
        return 0;
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvSale, tvprice, tvDate, name;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvprice = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            tvSale = itemView.findViewById(R.id.sale);
            tvDate = itemView.findViewById(R.id.date);
        }
    }
}
