package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import java.util.List;


public class VoucherSCAdapter extends RecyclerView.Adapter<VoucherSCAdapter.VoucherViewHolder>{
    private Context context;
    private List<Voucher> voucherlist;
    private ObjectUtil objectUtil;
    int selectedPosition = -1;
    private String productId;
    private int productPrice;
    public VoucherSCAdapter(Context context, List<Voucher> list, String productId, int productPrice) {
        this.context = context;
        this.voucherlist = list;
        this.productId = productId;
        this.productPrice = productPrice;
    }

    public void setListOrder(List<Voucher> voucherList) {
        this.voucherlist = voucherList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher store = voucherlist.get(position);
        boolean isEligible = checkEligibility(store);
        holder.itemView.setAlpha(isEligible ? 1.0f : 0.5f);
        holder.select.setSelected(position == selectedPosition);
        holder.select.setSelected(position == selectedPosition);
        holder.tvSale.setText(store.getCode());
        holder.tvprice.setText("Gia"+store.getMinOrderValue());
        holder.name.setText(store.getTitle());
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            if (!isEligible) {
                Toast.makeText(context, "Sản phẩm không đủ điều kiện áp dụng mã này", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedPosition == position) {
                // NẾU CLICK VÀO ITEM ĐANG CHỌN -> BỎ CHỌN
                selectedPosition = -1;

            } else {
                // NẾU CLICK VÀO ITEM KHÁC -> CHỌN ITEM ĐÓ
                selectedPosition = position;

            }

            // Cập nhật giao diện cho item cũ và item mới
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

        });
    }

    @Override
    public int getItemCount() {
        if(voucherlist != null){
            return  voucherlist.size();
        }
        return 0;
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvSale, tvprice, tvDate, name;
        ConstraintLayout select;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvprice = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            tvSale = itemView.findViewById(R.id.sale);
            tvDate = itemView.findViewById(R.id.date);
            select = itemView.findViewById(R.id.btnSelectVoucher);
        }
    }

    // Hàm kiểm tra điều kiện Voucher
    private boolean checkEligibility(Voucher v) {
        // Kiểm tra tiền tối thiểu
        if (productPrice < v.getMinOrderValue()) return false;

        // Kiểm tra sản phẩm có trong danh sách áp dụng không (nếu mảng không rỗng)
        if (v.getApplicableProducts() != null && !v.getApplicableProducts().isEmpty()) {
            return v.getApplicableProducts().contains(productId);
        }
        return true; // Toàn sàn
    }
}
