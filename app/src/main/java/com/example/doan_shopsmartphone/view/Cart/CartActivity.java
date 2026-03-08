package com.example.doan_shopsmartphone.view.Cart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.MainActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CartAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityCartBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartInterface;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.swipe.ItemTouchHelperListener;
import com.example.doan_shopsmartphone.ultil.swipe.RecycleViewItemTouchHelper;
import com.example.doan_shopsmartphone.view.voucher.VoucherScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartInterface, ItemTouchHelperListener {
    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private int totalPrice = 0;
    private int paymentMethods = 1;
    private static final int REQUEST_CODE_CHANGE_PAYMENT_METHODS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();
        // Hàm này có sẵn ở đâu cx gọi đc
       // ApiUtil.getAllCart(this, cartAdapter);
        if(CartUtil.listCart.size() == 0) {
            binding.tvDrum.setVisibility(View.VISIBLE);
        } else {
            binding.tvDrum.setVisibility(View.GONE);
        }
    }

    private void initController() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackActivity();
               // updateCart();
            }
        });

        binding.listVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, VoucherScreen.class);
                startActivity(intent);
            }
        });


        binding.listThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("paymentMethods", paymentMethods);
                startActivityForResult(intent, REQUEST_CODE_CHANGE_PAYMENT_METHODS);
            }
        });
        binding.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CartUtil.listCartCheck.size() > 0) {

                    updateCart();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                } else {
                    Toast.makeText(CartActivity.this, "Mời bạn chọn sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHANGE_PAYMENT_METHODS && resultCode == RESULT_OK) {
            paymentMethods = data.getIntExtra("paymentMethods", 1);
            // Cập nhật UI hoặc thực hiện các tác vụ khác với giá trị paymentMethods mới
            if (paymentMethods == 1){
                Toast.makeText(this, "Bạn đã chọn thanh toán khi nhận hàng", Toast.LENGTH_SHORT).show();
            }
            if (paymentMethods == 2 ){
                Toast.makeText(this, "Bạn đã chọn thanh toán qua ví ZaloPay", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initView() {
        // Xóa toàn bộ list đc chọn cũ
        CartUtil.listCartCheck.clear();

        // recycleView
        cartAdapter = new CartAdapter(this, CartUtil.listCart, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rcvCart.setLayoutManager(layoutManager);
        binding.rcvCart.setAdapter(cartAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvCart.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecycleViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvCart);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void setTotalPrice() {
        totalPrice = 0;
        for (int i = 0; i < CartUtil.listCartCheck.size(); i++) {
            double checkvoucher =  (double) (100 - CartUtil.listCartCheck.get(i).getOptionProduct().getDiscountValue()) /100;
            int price = CartUtil.listCartCheck.get(i).getOptionProduct().getPrice();
            Double price1 = price *checkvoucher;
            int quantity = CartUtil.listCartCheck.get(i).getQuantity();
            totalPrice += price1 * quantity;
        }
        DecimalFormat df = new DecimalFormat("###,###,###");
        binding.tvTotalPrice.setText(df.format(totalPrice) + " đ");
    }

    @Override
    public void onclickMinus(Object object, int position) {
        OptionAndQuantity cart = (OptionAndQuantity) object;
        int quantity = cart.getQuantity();
        if(quantity > 1) {
            quantity -= 1;
            CartUtil.listCart.get(position).setQuantity(quantity);
            cartAdapter.setListCart(CartUtil.listCart);
            setTotalPrice();
        }
    }

    @Override
    public void onclickPlus(Object object, int position) {
        OptionAndQuantity cart = (OptionAndQuantity) object;
        int quantity = cart.getQuantity();
        if(quantity>=cart.getOptionProduct().getQuantity()){
            Toast.makeText(this, "Vượt quá số lượng trong kho hàng", Toast.LENGTH_SHORT).show();
        }else{
            quantity += 1;
            CartUtil.listCart.get(position).setQuantity(quantity);
            cartAdapter.setListCart(CartUtil.listCart);
            setTotalPrice();
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof CartAdapter.CartViewHolder) {
            OptionAndQuantity cart = CartUtil.listCart.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();
            deleteCart(cart, indexDelete);
        }
    }

    private void deleteCart(OptionAndQuantity cart,int indexDelete) {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        String cartId = cart.getId();

        binding.progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //updateCart();
        onBackActivity();
    }
    private void updateCart() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < CartUtil.listCart.size(); i++) {
            int position = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    updateQuantityCart(CartUtil.listCart.get(position));
                }
            });
        }
    }
    private void updateQuantityCart(OptionAndQuantity cart) {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        String cartId = cart.getId();
        int quantity = cart.getQuantity();



    }
    private void onBackActivity() {
        // Đoạn này chưa hiểu lắm nhưng kể cả truyền cho màn hình nào thì tất cả
        // registerForActivityResult onBack đểu đc chạy
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        intent.putExtra("data_cart_size", CartUtil.listCart.size());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}