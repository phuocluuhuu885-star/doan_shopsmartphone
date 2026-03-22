package com.example.doan_shopsmartphone.view.oder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CartPayAdapter;
import com.example.doan_shopsmartphone.adapter.DetailOderItemAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.response.store.DetailBills;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartUtil;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOderActivity extends AppCompatActivity {
    private String orderId;
    private TextView tvStatusHeader, tvStatusDesc, tvUserName, tvAddress, tvTotalFinal, tvOrderId, tvTime;
    private View headerBackground;
    private RecyclerView rvProducts;
    private Button btnAction;
    private DetailOderItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_oder);

        // Lấy ID từ Intent (từ Notification hoặc List)
        orderId = getIntent().getStringExtra("ORDER_ID_KEY");
        initViews();
        controler();

    }

    private void initViews() {
        tvStatusHeader = findViewById(R.id.tvStatusHeader);
        tvUserName = findViewById(R.id.tvUserName);
        tvAddress = findViewById(R.id.tvAddress);
        tvTotalFinal = findViewById(R.id.tvTotalFinal);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTime = findViewById(R.id.tvCreateTime);
        rvProducts = findViewById(R.id.rvProductsOder);
    }

    private void controler(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProducts.setLayoutManager(linearLayoutManager);


        String token = AccountUltil.BEARER + AccountUltil.getToken(getApplicationContext());
        BaseApi.API.getDetailBill(token,orderId).enqueue(new Callback<DetailBills>() {
            @Override
            public void onResponse(Call<DetailBills> call, Response<DetailBills> response) {
                if(response.isSuccessful()){
                    DetailBills detailBills = response.body();
                    Order resultBuil = detailBills.getResult();
                    bindDataToUI(resultBuil);
                    Log.e( "qwe: ",""+resultBuil.getProductsOrder().toString() );

                    adapter = new DetailOderItemAdapter(DetailOderActivity.this, resultBuil.getProductsOrder());
                    rvProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<DetailBills> call, Throwable t) {

            }
        });
    }


    private void bindDataToUI(Order order) {
        // 1. Thông tin người nhận
        tvUserName.setText(order.getInfo().getName() + " | " + order.getInfo().getPhoneNumber());
        tvAddress.setText(order.getInfo().getAddress());

        // 2. Danh sách sản phẩm


        // 3. Tiền tệ (Định dạng 10,000,000 VNĐ)
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedPrice = "₫" + formatter.format(order.getTotalPrice());
        tvTotalFinal.setText(formattedPrice);

        // 4. Mã đơn và thời gian
        tvOrderId.setText("Mã đơn hàng: " + order.getId().toUpperCase());
        tvTime.setText("Thời gian đặt hàng: " + order.getCreatedAt());

        // 5. Xử lý trạng thái (Giống màu Shopee)
        tvStatusHeader.setText(order.getStatus());
        Log.e( "iiii: ",order.getStatus() );
    }

}