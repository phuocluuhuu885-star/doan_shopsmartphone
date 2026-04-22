package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.VoucherGroup;
import com.example.doan_shopsmartphone.model.body.CartItem;
import com.example.doan_shopsmartphone.ultil.OnVoucherSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartParentAdapter extends RecyclerView.Adapter<CartParentAdapter.ViewHolder> implements OnVoucherSelectedListener {
    private List<VoucherGroup> cartItems;
    private Context context;
    private Voucher currentlySelectedVoucher = null;
    private Map<Integer, Voucher> selectedVouchersMap = new HashMap<>();

    // Hàm để lấy tất cả voucher đã chọn gửi về Activity
    public ArrayList<Voucher> getAllSelectedVouchers() {
        return new ArrayList<>(selectedVouchersMap.values());
    }
    public CartParentAdapter(Context context, List<VoucherGroup> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public CartParentAdapter(Context context, List<VoucherGroup> cartItems, List<Voucher> preSelectedVouchers) {
        this.context = context;
        this.cartItems = cartItems;
        if (preSelectedVouchers != null && cartItems != null) {
            for (int i = 0; i < cartItems.size(); i++) {
                VoucherGroup group = cartItems.get(i);
                for (Voucher v : preSelectedVouchers) {
                    // Kiểm tra xem voucher này có thuộc về nhóm sản phẩm này không
                    for (Voucher gVoucher : group.getVouchers()) {
                        if (v.get_id().equals(gVoucher.get_id())) {
                            selectedVouchersMap.put(i, gVoucher);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onVoucherSelected(Voucher voucher) {
        this.currentlySelectedVoucher = voucher;
        // Có thể gọi notifyDataSetChanged() ở đây nếu muốn làm mờ các nhóm khác
    }

    // ĐÂY LÀ HÀM BẠN CẦN GỌI Ở ACTIVITY
    public Voucher getSelectedVoucher() {
        return currentlySelectedVoucher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_voucher_child, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoucherGroup item = cartItems.get(position);
        holder.tvProductName.setText(item.getProductName());

        // Tìm xem trong nhóm này có voucher nào đã được chọn trước đó không
        Voucher preSelected = selectedVouchersMap.get(position);
        String initialId = (preSelected != null) ? preSelected.get_id() : null;

        // Thiết lập RecyclerView con cho Voucher
        VoucherSCAdapter childAdapter = new VoucherSCAdapter(item.getVouchers(), initialId, (Voucher voucher) -> {
            if (voucher != null) {
                selectedVouchersMap.put(position, voucher);
            } else {
                selectedVouchersMap.remove(position);
            }
        });
        holder.rcvVouchers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.rcvVouchers.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() { return cartItems.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        RecyclerView rcvVouchers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            rcvVouchers = itemView.findViewById(R.id.rcvVouchers);
        }
    }
}
