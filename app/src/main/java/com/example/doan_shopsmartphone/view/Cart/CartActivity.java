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
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.OptionOfListCart;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.view.buy_product.PayActivity;
import com.example.doan_shopsmartphone.databinding.ActivityCartBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ApiUtil;
import com.example.doan_shopsmartphone.ultil.CartInterface;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.TAG;
import com.example.doan_shopsmartphone.ultil.swipe.ItemTouchHelperListener;
import com.example.doan_shopsmartphone.ultil.swipe.RecycleViewItemTouchHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartInterface, ItemTouchHelperListener {
    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private int totalPrice = 0;
    private int paymentMethods = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();
        // Hàm này có sẵn ở đâu cx gọi đc
        ApiUtil.getAllCart(this, cartAdapter);
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
        Log.e( "idPro: ",CartUtil.listCartCheck.toString() );
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
        BaseApi.API.deleteCartItem(token,cartId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    assert serverResponse != null;
                    Log.d(TAG.toString, "onResponse-deleteCartItem: " + serverResponse.toString());
                    if(serverResponse.getCode() == 200) {
                        cartAdapter.removeItem(indexDelete);
                        CartUtil.listCartCheck.remove(cart);
                        setTotalPrice();
                        if(CartUtil.listCart.size() == 0) {
                            binding.tvDrum.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvDrum.setVisibility(View.GONE);
                        }
                    }
                } else { // nhận các đầu status #200
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-deleteCartItem: " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-deleteCartItem: " + t.toString());
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //updateCart();
        onBackActivity();
    }
    private void updateCart() {
        int total = CartUtil.listCart.size();
        if (total == 0) return;

        AtomicInteger finished = new AtomicInteger(0);
        AtomicBoolean anyFailed = new AtomicBoolean(false);

        for (int i = 0; i < CartUtil.listCart.size(); i++) {
            updateQuantityCart(CartUtil.listCart.get(i), finished, anyFailed, total);
        }
    }

    private void updateQuantityCart(OptionAndQuantity cart, AtomicInteger finished, AtomicBoolean anyFailed, int total) {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        String cartId = cart.getId();
        int quantity = cart.getQuantity();


        BaseApi.API.updateQuantityCartItem(token,cartId,quantity).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    assert serverResponse != null;
                    Log.d("Server cart trả về", "onResponse: " + serverResponse.getCode());
                    Log.d(TAG.toString, "onResponse-updateQuantityCartItem: " + serverResponse.toString());
                    if(serverResponse.getCode() == 200) {
                        Log.d(TAG.toString, "onResponse-updateQuantityCartItem: " + serverResponse.getCode());
                    } else {
                        anyFailed.set(true);
                        Toast.makeText(CartActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else { // nhận các đầu status #200
                    anyFailed.set(true);
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-updateQuantityCartItem: " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (finished.incrementAndGet() == total && !anyFailed.get()) {
                    Intent intent = new Intent(CartActivity.this, PayActivity.class);
                    intent.putExtra("totalPrice" , totalPrice);
                    intent.putExtra("paymentMethods", paymentMethods);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                anyFailed.set(true);
                Toast.makeText(CartActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-updateQuantityCartItem: " + t.toString());

                if (finished.incrementAndGet() == total && !anyFailed.get()) {
                    Intent intent = new Intent(CartActivity.this, PayActivity.class);
                    intent.putExtra("totalPrice" , totalPrice);
                    intent.putExtra("paymentMethods", paymentMethods);
                    startActivity(intent);
                }
            }
        });
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