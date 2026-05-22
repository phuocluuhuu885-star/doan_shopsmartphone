package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.ProductDetail;
import com.example.doan_shopsmartphone.model.OptionProduct;
import com.example.doan_shopsmartphone.model.response.DetailProductResponse;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



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
                    ProgressLoadingDialog progressDialog = new ProgressLoadingDialog(context);
                    progressDialog.show();

                    BaseApi.API.getDetailProduct(product.getId()).enqueue(new Callback<DetailProductResponse>() {
                        @Override
                        public void onResponse(Call<DetailProductResponse> call, Response<DetailProductResponse> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                ProductDetail productDetail = response.body().getResult();
                                showProductDetailDialog(productDetail, cart.getOptionProduct().getId(), cart.getQuantity());
                            } else {
                                Toast.makeText(context, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DetailProductResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showProductDetailDialog(ProductDetail productDetail, String optionId, int quantity) {
        if (context == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_checkout_product_detail, null);
        dialog.setContentView(dialogView);

        ImageView imgOption = dialogView.findViewById(R.id.imgDialogOption);
        TextView tvProductName = dialogView.findViewById(R.id.tvDialogProductName);
        TextView tvProductColor = dialogView.findViewById(R.id.tvDialogProductColor);
        TextView tvQuantity = dialogView.findViewById(R.id.tvDialogQuantity);
        TextView tvPrice = dialogView.findViewById(R.id.tvDialogPrice);
        TextView tvOriginalPrice = dialogView.findViewById(R.id.tvDialogOriginalPrice);

        TextView tvSummaryRam = dialogView.findViewById(R.id.tvSummaryRam);
        TextView tvSummaryIntegrity = dialogView.findViewById(R.id.tvSummaryIntegrity);
        TextView tvSummaryStorage = dialogView.findViewById(R.id.tvSummaryStorage);
        TextView tvSummaryCondition = dialogView.findViewById(R.id.tvSummaryCondition);
        TextView tvSummaryBattery = dialogView.findViewById(R.id.tvSummaryBattery);

        TextView btnMoreSpecs = dialogView.findViewById(R.id.btnMoreSpecs);
        TableLayout tableGeneralSpecs = dialogView.findViewById(R.id.tableGeneralSpecs);

        TextView tvSpecScreen = dialogView.findViewById(R.id.tvDialogSpecScreen);
        TextView tvSpecCamera = dialogView.findViewById(R.id.tvDialogSpecCamera);
        TextView tvSpecChipset = dialogView.findViewById(R.id.tvDialogSpecChipset);
        TextView tvSpecOS = dialogView.findViewById(R.id.tvDialogSpecOS);
        TextView tvSpecBattery = dialogView.findViewById(R.id.tvDialogSpecBattery);
        TextView tvSpecConnection = dialogView.findViewById(R.id.tvDialogSpecConnection);

        TextView tvDialogWarranty = dialogView.findViewById(R.id.tvDialogWarranty);
        Button btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);

        // Find matching option
        OptionProduct selectedOption = null;
        if (productDetail.getOption() != null) {
            for (OptionProduct opt : productDetail.getOption()) {
                if (opt.getId() != null && opt.getId().equals(optionId)) {
                    selectedOption = opt;
                    break;
                }
            }
        }

        if (selectedOption == null) {
            Toast.makeText(context, "Không tìm thấy cấu hình option phù hợp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set general data
        tvProductName.setText(productDetail.getName() + "");
        tvProductColor.setText("Phân loại: " + selectedOption.getNameColor());
        tvQuantity.setText("Số lượng: x" + quantity);

        DecimalFormat df = new DecimalFormat("###,###,###");
        double discount = (double) (100 - selectedOption.getDiscountValue()) / 100;
        int finalPriceVal = (int) (selectedOption.getPrice() * discount);

        tvPrice.setText(df.format(finalPriceVal) + " đ");
        if (selectedOption.getDiscountValue() > 0) {
            tvOriginalPrice.setText(df.format(selectedOption.getPrice()) + " đ");
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            tvOriginalPrice.setVisibility(View.VISIBLE);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }

        Glide.with(context).load(selectedOption.getImage()).into(imgOption);

        // Option detail specs
        tvSummaryRam.setText("RAM: " + (selectedOption.getRam() != null ? selectedOption.getRam() : "--"));
        tvSummaryStorage.setText("Bộ nhớ: " + (selectedOption.getStorageCapacity() != null ? selectedOption.getStorageCapacity() : "--"));
        tvSummaryIntegrity.setText("Tình trạng: " + (selectedOption.getIsOriginal() != null ? selectedOption.getIsOriginal() : "--"));
        tvSummaryCondition.setText("Ngoại hình: " + (selectedOption.getConditionPercent() != null ? selectedOption.getConditionPercent() + "%" : "--"));
        tvSummaryBattery.setText("Pin: " + (selectedOption.getBatteryHealth() != null ? selectedOption.getBatteryHealth() : "--"));

        // General specs
        String optionScreen = selectedOption.getScreen();
        if (optionScreen != null && !optionScreen.trim().isEmpty()) {
            tvSpecScreen.setText(optionScreen);
        } else {
            tvSpecScreen.setText(productDetail.getScreen() != null ? productDetail.getScreen() : "N/A");
        }
        tvSpecCamera.setText(productDetail.getCamera() != null ? productDetail.getCamera() : "N/A");
        tvSpecChipset.setText(productDetail.getChipset() != null ? productDetail.getChipset() : "N/A");
        tvSpecOS.setText(productDetail.getOperatingSystem() != null ? productDetail.getOperatingSystem() : "N/A");
        tvSpecBattery.setText(productDetail.getBattery() != null ? productDetail.getBattery() : "N/A");
        tvSpecConnection.setText(productDetail.getConnection() != null ? productDetail.getConnection() : "N/A");

        // Warranty duration
        String warranty = selectedOption.getWarrantyTime();
        if (warranty != null && !warranty.trim().isEmpty()) {
            tvDialogWarranty.setText(warranty + " bảo hành chính hãng");
        } else {
            tvDialogWarranty.setText("Bảo hành chính hãng");
        }

        // Click handlers
        btnMoreSpecs.setOnClickListener(v -> {
            if (tableGeneralSpecs.getVisibility() == View.GONE) {
                tableGeneralSpecs.setVisibility(View.VISIBLE);
                btnMoreSpecs.setText("Thu gọn thông số ▴");
            } else {
                tableGeneralSpecs.setVisibility(View.GONE);
                btnMoreSpecs.setText("Xem thêm thông số kỹ thuật ▾");
            }
        });

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
