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
import com.example.doan_shopsmartphone.ultil.OnVoucherSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class VoucherSCAdapter extends RecyclerView.Adapter<VoucherSCAdapter.VoucherViewHolder>{
    private Context context;
    private List<Voucher> voucherlist;
    private ObjectUtil objectUtil;
    int selectedPosition = -1;
    private OnVoucherSelectedListener listener;
    public VoucherSCAdapter(List<Voucher> voucherlist, OnVoucherSelectedListener listener) {
        this.voucherlist = voucherlist;
        this.listener = listener;
    }

    public VoucherSCAdapter(List<Voucher> voucherlist, String initialSelectedId, OnVoucherSelectedListener listener) {
        this.voucherlist = voucherlist;
        this.listener = listener;
        if (initialSelectedId != null && voucherlist != null) {
            for (int i = 0; i < voucherlist.size(); i++) {
                if (initialSelectedId.equals(voucherlist.get(i).get_id())) {
                    this.selectedPosition = i;
                    break;
                }
            }
        }
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

        holder.select.setSelected(position == selectedPosition);
        holder.tvSale.setText(store.getCode());
        holder.tvprice.setText(store.getDiscountValue()+"Đ");
        holder.name.setText(store.getTitle());
        holder.count.setText("x"+store.getQuantity());
        boolean isEligible = isVoucherValids(store);
        if (!isEligible) {
            // TRƯỜNG HỢP: Voucher không đủ điều kiện (Hết hạn hoặc hết lượt)
            holder.itemView.setAlpha(0.3f); // Mờ hẳn đi
            holder.itemView.setEnabled(false); // Chặn tương tác
        } else {
            // TRƯỜNG HỢP: Voucher còn hạn và còn lượt
            holder.itemView.setEnabled(true);
            holder.select.setVisibility(View.VISIBLE);

            // Xử lý mờ theo logic "Đã chọn 1 cái thì cái còn lại mờ đi"
            if (selectedPosition == -1) {
                holder.itemView.setAlpha(1.0f); // Chưa chọn cái nào -> Sáng rõ
            } else {
                if (selectedPosition == position) {
                    holder.itemView.setAlpha(1.0f); // Cái đang chọn -> Sáng rõ
                } else {
                    holder.itemView.setAlpha(0.5f); // Cái khác -> Mờ nhẹ
                }
            }
        }
        if (store.getExpiryDate() != null) {
            String formattedDate = formatDate(store.getExpiryDate()); // Gọi hàm format ở trên
            holder.date.setText("HSD: " + formattedDate);
        }


        // 3. Xử lý sự kiện Click
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;

            if (selectedPosition == position) {
                selectedPosition = -1; // Bỏ chọn
            } else {
                selectedPosition = position; // Chọn mới
            }
            // GỬI DỮ LIỆU LÊN CHA
            if (listener != null) {
                if (selectedPosition == -1) {
                    listener.onVoucherSelected(null);
                } else {
                    listener.onVoucherSelected(store);
                }
            }

            notifyDataSetChanged();
        });


    }

    @Override
    public int getItemCount() {
        if(voucherlist != null){
            return  voucherlist.size();
        }
        return 0;
    }

    public Voucher getSelectedVoucher() {
        if (selectedPosition != -1 && selectedPosition < voucherlist.size()) {
            return voucherlist.get(selectedPosition);
        }
        return null; // Trả về null nếu chưa chọn cái nào
    }

    public boolean isVoucherValids(Voucher voucher) {
        try {
            // 1. Kiểm tra số lượng
            if (voucher.getQuantity() <= 0) return false;

            // 2. Kiểm tra ngày hết hạn
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date expiryDate = inputFormat.parse(voucher.getExpiryDate());
            Date currentDate = new Date(); // Ngày hiện tại

            if (expiryDate == null || expiryDate.before(currentDate)) {
                return false; // Đã hết hạn
            }
            return true; // Thỏa mãn tất cả điều kiện
        } catch (Exception e) {
            return false;
        }
    }
    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvSale, tvprice, tvDate, name,count,date;
        ConstraintLayout select;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvprice = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            tvSale = itemView.findViewById(R.id.sale);
            tvDate = itemView.findViewById(R.id.date);
            select = itemView.findViewById(R.id.btnSelectVoucher);
            count = itemView.findViewById(R.id.count);
            date = itemView.findViewById(R.id.date);
        }
    }

    private String formatDate(String dateString) {
        try {
            // Định dạng nhận vào từ Server (Mongoose Date)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            // Định dạng muốn hiển thị ra màn hình
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "N/A"; // Nếu lỗi thì hiện N/A
        }
    }

     //Hàm kiểm tra điều kiện Voucher

}
