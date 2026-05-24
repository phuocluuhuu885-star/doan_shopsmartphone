package com.example.doan_shopsmartphone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.OptionProduct;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.ProductDetail;
import com.example.doan_shopsmartphone.model.response.DetailProductResponse;
import com.example.doan_shopsmartphone.ultil.CartInterface;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<OptionAndQuantity> listCart;
    private CartInterface cartInterface;

    public CartAdapter(Context context, List<OptionAndQuantity> listCart, CartInterface cartInterface) {
        this.context = context;
        this.listCart = listCart;
        this.cartInterface = cartInterface;
    }

    public void setListCart(List<OptionAndQuantity> listCart) {
        this.listCart = listCart;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_cart,parent,false);
        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OptionAndQuantity cart = listCart.get(position);
        if(cart == null) {
            return;
        }
        holder.tvName.setText(cart.getOptionProduct().getProduct().getName());











//        // test
//        if(cart.getOptionProduct().getProduct() != null){
//            holder.tvName.setText(cart.getOptionProduct().getProduct().getName());
//        }else{
//            holder.tvName.setText("Test Product");
//        }
//        // test







        DecimalFormat df = new DecimalFormat("###,###,###");
        Double checkgia = (double)(100-cart.getOptionProduct().getDiscountValue())/100;
        Double gia = cart.getOptionProduct().getPrice() *checkgia;
        holder.tvPrice.setText(df.format(gia) + "đ");
        holder.tvQuantity.setText(cart.getQuantity() + "");
        holder.tvColorOption.setText("Phân loại: " + cart.getOptionProduct().getNameColor());

        Glide.with(context)
                .load(cart.getOptionProduct().getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.imgProduct);

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartInterface.onclickMinus(cart,position);
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartInterface.onclickPlus(cart,position);
            }
        });
        holder.chkPurchase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    CartUtil.listCartCheck.add(cart);
                    cartInterface.setTotalPrice();
                } else {
                    CartUtil.listCartCheck.remove(cart);
                    cartInterface.setTotalPrice();
                }
            }
        });

        View.OnClickListener openDetailListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        };

        holder.imgProduct.setOnClickListener(openDetailListener);
        holder.tvName.setOnClickListener(openDetailListener);
        holder.tvColorOption.setOnClickListener(openDetailListener);
        holder.tvPrice.setOnClickListener(openDetailListener);
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
            if (optionScreen.toLowerCase().contains("thay màn")) {
                tvSpecScreen.setText(optionScreen);
            } else {
                tvSpecScreen.setText(optionScreen + " (đã thay màn)");
            }
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
        if(listCart!= null) {
            return listCart.size();
        }
        return 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private CheckBox chkPurchase;
        private ImageView imgProduct;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvQuantity;
        private TextView btnMinus;
        private TextView btnPlus;
        private TextView tvColorOption;

        public LinearLayout layoutForeground;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            chkPurchase = itemView.findViewById(R.id.chkPurchase);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            layoutForeground = itemView.findViewById(R.id.layoutForeground);
            tvColorOption = itemView.findViewById(R.id.tvColorOption);
        }
    }
    public void removeItem(int index) {
        listCart.remove(index);
        notifyItemRemoved(index);
    }
    public void undoItem(OptionAndQuantity cart, int index) {
        listCart.add(index, cart);
        notifyItemInserted(index);
    }
}
