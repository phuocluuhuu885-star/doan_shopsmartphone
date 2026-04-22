package com.example.doan_shopsmartphone.view.voucher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CartParentAdapter;
import com.example.doan_shopsmartphone.adapter.VoucherSCAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityVoucherScreenBinding;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.VoucherGroup;
import com.example.doan_shopsmartphone.model.body.CartItem;
import com.example.doan_shopsmartphone.model.response.VoucherRequest;
import com.example.doan_shopsmartphone.model.response.VoucherResponse;
import com.example.doan_shopsmartphone.view.buy_product.PayActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VoucherScreen extends AppCompatActivity {
    ActivityVoucherScreenBinding binding;
    private CartParentAdapter adapterr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadAllVouchers();

    }

    private void init() {
        // Nhận dữ liệu từ Adapter Pay truyền sang
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnApplyVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Voucher> selectedVouchers = adapterr.getAllSelectedVouchers();
                if (selectedVouchers == null) {
                    selectedVouchers = new ArrayList<>();
                }
                
                // 2. Tạo intent để chứa dữ liệu trả về
                Intent resultIntent = new Intent();

                // Gửi cả danh sách đi (có thể rỗng nếu người dùng đã bỏ chọn)
                resultIntent.putExtra("LIST_VOUCHER_SELECTED", selectedVouchers);

                // 3. Đánh dấu thành công và gửi dữ liệu
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }
    private void loadAllVouchers() {
        ArrayList<String> productIds = getIntent().getStringArrayListExtra("LIST_PRODUCT_ID");
        if (productIds == null || productIds.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào để áp dụng voucher", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng request nếu API của bạn yêu cầu Body là một Object
        // VoucherRequest request = new VoucherRequest(productIds);
        VoucherRequest request = new VoucherRequest(productIds);
        // Gọi API (Lưu ý: Truyền productIds vào)
        BaseApi.API.getVouchersByList(request).enqueue(new Callback<VoucherResponse>() {
            @Override
            public void onResponse(Call<VoucherResponse> call, Response<VoucherResponse> response) {
                if ( response.code() == 200 ) {
                    VoucherResponse voucherResponse = response.body();

                    if (voucherResponse.isSuccess()) {
                        // Lấy danh sách các nhóm Voucher theo sản phẩm
                        List<VoucherGroup> data = voucherResponse.getData();

                        if (data != null && !data.isEmpty()) {
                            // Cài đặt Adapter cho RecyclerView chính (Cái nhìn thấy trên màn hình)
                            setupRecyclerView(data);
                        } else {
                            Toast.makeText(VoucherScreen.this, "Hiện không có voucher khả dụng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("voucherAPI: ", "Server trả về lỗi: " + voucherResponse.getMessage());
                    }
                } else {
                    Log.e("voucherAPI: ", "Lỗi kết nối hoặc mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VoucherResponse> call, Throwable t) {
                Log.e("voucherAPI: ", "Lỗi hệ thống: " + t.getMessage());
                Toast.makeText(VoucherScreen.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<VoucherGroup> data) {
        binding.rcvVoucherStore.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter cha (Chứa tên Sản phẩm và danh sách Voucher con)
         adapterr = new CartParentAdapter( this,data);
        binding.rcvVoucherStore.setAdapter(adapterr);
    }
}