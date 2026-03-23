package com.example.doan_shopsmartphone.view.buy_product;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.example.doan_shopsmartphone.model.Notifi;
import com.example.doan_shopsmartphone.model.OptionOfListCart;
import com.example.doan_shopsmartphone.model.OrderResult;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.body.NotificationBody;
import com.example.doan_shopsmartphone.model.response.NotificationResponse;
import com.example.doan_shopsmartphone.model.response.NotificationResult;
import com.example.doan_shopsmartphone.view.success_screen.OrderSuccessActivity;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CartPayAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityPayBinding;
import com.example.doan_shopsmartphone.model.CreateOrder;
import com.example.doan_shopsmartphone.model.Info;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.body.PurchaseBody;
import com.example.doan_shopsmartphone.model.response.InfoResponse;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;
import com.example.doan_shopsmartphone.view.voucher.VoucherScreen;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PayActivity extends AppCompatActivity {
    private ActivityPayBinding binding;
    private ProgressLoadingDialog loadingDialog;
    private List<Info> infoList;
    private Info info;
    private CartPayAdapter cartPayAdapter;
    private int totalPrice;

    private int paymentMethods;

    private List<String> productIds;
    private static final int REQUEST_CODE_ORDER_SUCCESS = 1;


    ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Nhận lại danh sách voucher
                    ArrayList<Voucher> selectedList = (ArrayList<Voucher>) result.getData().getSerializableExtra("LIST_VOUCHER_SELECTED");

                    if (selectedList != null) {
                        Log.e( "da: ", selectedList.get(0).toString() );
                        calculateMultiVoucher(selectedList);
                        // Chạy hàm tính toán tổng tiền cho từng sản phẩm như mình đã hướng dẫn ở trên
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        for(int i =0;i<CartUtil.listCartCheck.size();i++){
            CartUtil.listCartCheck.get(i).setDiscount_value(CartUtil.listCartCheck.get(i).getOptionProduct().getDiscountValue());
        }


        //ZaloPay create
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2554, Environment.SANDBOX);

        //Take paymentMethods
        paymentMethods = getIntent().getIntExtra("paymentMethods", 0);
        if (paymentMethods == 1){
            binding.txtPaymentMethods.setText("Thanh toán khi nhận hàng");

        }
        if (paymentMethods == 2){
            binding.txtPaymentMethods.setText("Thanh toán qua ví ZaloPay");
        }

         productIds = CartUtil.listCartCheck.stream()
                .map(cartItem -> cartItem.getOptionProduct()) // Lấy Object OptionOfListCart
                .filter(Objects::nonNull)                     // Kiểm tra null để tránh crash
                .map(option -> option.getProduct())           // Lấy Object Product
                .filter(Objects::nonNull)                     // Kiểm tra null tiếp
                .map(product -> product.getId())              // Cuối cùng lấy Product ID
                .collect(Collectors.toList());
        Log.e( "idPro: ",productIds.toString() );
        initView();
        initController();
        urlGetAllInfo();
    }

    private void urlGetAllInfo() {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        loadingDialog.show();
        BaseApi.API.getInfo(token).enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    InfoResponse infoResponse = response.body();
                    Log.d(TAG.toString, "onResponse-getInfo: " + infoResponse.toString());
                    if(infoResponse.getCode() == 200 || infoResponse.getCode() == 201) {
                        infoList = infoResponse.getResult();
                        // setDataAddress
                        setDataInfo();
                    }
                } else { // nhận các đầu status #200
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-getInfo: " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<InfoResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-getInfo: " + t.toString());
                loadingDialog.dismiss();
            }
        });
    }
    private void setDataInfo() {
        if(infoList.size() == 0) {
            binding.tvInfoUser.setText("Chưa có địa chỉ mời bạn tạo");
            return;
        }
        info = infoList.get(0);
        for (int i = 0; i < infoList.size(); i++) {
            if(infoList.get(i).getChecked()) {
                info = infoList.get(i);
                break;
            }
        }
        if(info != null) {
            binding.tvInfoUser.setText(info.getName() + " | " + info.getPhoneNumber() + " | " + info.getAddress());
        } else {
            binding.tvInfoUser.setText("Chưa có địa chỉ mời bạn tạo");
        }
    }
    private void initController() {
        binding.layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, AddressActivity.class);
                mActivityResultLauncher.launch(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        binding.onVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, VoucherScreen.class);
                intent.putStringArrayListExtra("LIST_PRODUCT_ID", (ArrayList<String>) productIds);
                voucherLauncher.launch(intent);
            }
        });
        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //---------------Chưa chọn phương thức thanh toán---------------------
                if (paymentMethods == 0){
                    Toast.makeText(PayActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                }

                //---------------Zalo Pay----------------
                if (paymentMethods == 2){
                    if(CartUtil.listCartCheck.size() > 0) {
                        zaloRequest();
                        Log.d("thanhtoan", "phuong thuc: zalopay ");
                        Log.d(TAG.toString, "onClick: paymentMethods "+paymentMethods);

                    } else {
                        Toast.makeText(PayActivity.this, "Chưa có sản phẩm nào", Toast.LENGTH_SHORT).show();
                    }

                }
                //-----------------Thanh toán khi nhận hàng-----------------
                else if (paymentMethods == 1) {
                    Log.d("thanhtoan", "phuong thuc: nhan hang ");

                    if(CartUtil.listCartCheck.size() > 0) {
                        urlCreateOrder();
                    } else {
                        Toast.makeText(PayActivity.this, "Chưa có sản phẩm nào", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });
    }
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //    info = bundle.getSerializable("obje   ct_info", Info.class);
                        //}
                        // Cách nhận ở mọi phiên bản
                        if (bundle.containsKey("object_info")) {
                            Object objectInfo = bundle.get("object_info");
                            if (objectInfo instanceof Info) {
                                info = (Info) objectInfo;
                            }
                        }
                        binding.tvInfoUser.setText(info.getName() + " | " + info.getPhoneNumber() + " | " + info.getAddress());
                    }
                }
            });
    //----------------Normal payment----------------------

    private void urlCreateOrder() {
        if(validatePurchare()) {
//            String token = AccountUltil.BEARER + AccountUltil.TOKEN;
            String token = AccountUltil.BEARER + AccountUltil.getToken(this);

            PurchaseBody purchaseBody = new PurchaseBody();
            purchaseBody.setInfoId(info.getId());
            purchaseBody.setUserId(AccountUltil.USER.getId());
            purchaseBody.setProductsOrder(CartUtil.listCartCheck);

            loadingDialog.show();
            BaseApi.API.createOrder(token, purchaseBody).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    Log.d("JSON_THO", new Gson().toJson(response.body()));
                    if(response.isSuccessful()){ // chỉ nhận đầu status 200
                        ServerResponse serverResponse = response.body();
                        Log.d(TAG.toString, "onResponse-createOrder: " + serverResponse.toString());
                        if(serverResponse.getCode() == 200 || serverResponse.getCode() == 201) {
                            Toast.makeText(PayActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            removeDataCart();
                            OrderResult orderData = serverResponse.getResult();
                            urlCreateNotification(orderData.getId());
                            if (orderData != null) {
                                String id = orderData.getId();
                                String transId = orderData.getAppTransId();
                                Log.d("DEBUG", "ID nhận được: " + serverResponse.toString()+"  id"+ id);
                            } else {
                                Log.d("DEBUG", "Đối tượng Result bị null!");
                            }
//                            CartUtil.listCartCheck.clear();
                        }
                    } else { // nhận các đầu status #200
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Log.d(TAG.toString, "onResponse-createOrder: " + errorMessage);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }catch (IOException e){
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG.toString, "onFailure-createOrder: " + t.toString());
                    loadingDialog.dismiss();
                }
            });
        }
    }

    private void urlCreateNotification(String orderId) {
        // 1. Chuẩn bị dữ liệu (Ví dụ mẫu)
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        // Chuẩn bị dữ liệu gửi lên
        NotificationBody body = new NotificationBody(
                AccountUltil.USER.getId(),   // Người gửi (User)
                AccountUltil.USER.getId(),           // ID Admin hoặc Shop (Receiver)
                orderId,                     // ID đơn hàng vừa tạo
                "Bạn có đơn hàng mới mã: " + orderId,
                "wfc"                        // Type: Chờ xác nhận
        );

        // Đảm bảo Interface BaseApi định nghĩa: Call<NotificationResponse> createNotification(...)

        BaseApi.API.createNotification(token, body).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy object NotificationResponse ra
                    NotificationResponse serverRes = response.body();

                    // Lấy kết quả Notifi bên trong (biến 'result' trong class NotificationResponse)
                    Notifi noti = serverRes.getResult();

                    if (noti != null) {
                        Log.d("DEBUG", "Thông báo đã lưu: " + noti.getId() + " - " + noti.getContent());
                        Toast.makeText(PayActivity.this, "Tạo thông báo thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("DEBUG", "Server trả lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e("DEBUG", "Lỗi mạng hoặc lổi Parse: " + t.getMessage());
            }
        });
    }

    private void removeDataCart() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < CartUtil.listCartCheck.size(); i++) {
            int position = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    deleteCart(CartUtil.listCartCheck.get(position));
                }
            });
        }
    }
    private void deleteCart(OptionAndQuantity cart) {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        String cartId = cart.getId();
        loadingDialog.show();
        BaseApi.API.deleteCartItem(token, cartId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    Log.d(TAG.toString, "onResponse-deleteCartItem: " + serverResponse.toString());
                    if(serverResponse.getCode() == 200) {
                        Intent intent = new Intent(PayActivity.this, OrderSuccessActivity.class);
                        startActivity(intent);
                        CartUtil.listCart.removeAll(CartUtil.listCartCheck);
                        CartUtil.listCartCheck.clear();
                        finishAffinity();
                    }
                } else { // nhận các đầu status #200
                    try {
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
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(PayActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-deleteCartItem: " + t.toString());
                loadingDialog.dismiss();
            }
        });
    }

    //---------------- End normal payment----------------------


    //----------------Zalo----------------------

    private void urlCreateOrderZalo() {
        if(validatePurchare()) {
//            String token = AccountUltil.BEARER + AccountUltil.TOKEN;
            String token = AccountUltil.BEARER + AccountUltil.getToken(this);

            PurchaseBody purchaseBody = new PurchaseBody();
            purchaseBody.setInfoId(info.getId());
            purchaseBody.setUserId(AccountUltil.USER.getId());
            purchaseBody.setProductsOrder(CartUtil.listCartCheck);
            purchaseBody.setPayment_status(true);
            loadingDialog.show();
            BaseApi.API.createOrderByZalo(token, purchaseBody).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    loadingDialog.dismiss();
                    if(response.isSuccessful()){ // chỉ nhận đầu status 200
                        ServerResponse serverResponse = response.body();

                        if(serverResponse.getCode() == 200 || serverResponse.getCode() == 201) {
                            Toast.makeText(PayActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            //zaloRequest();

//                            String orderId = serverResponse.getOrder().getId(); // Lấy ID của đơn hàng từ serverResponse
//                           Log.d(TAG.toString, "onResponse: "+orderId);
//                                            Intent intent = new Intent(PayActivity.this, OrderSuccessActivity.class);
//                                            startActivity(intent);
//                            CartUtil.listCartCheck.clear();
                            //Order order = serverResponse.getOrder();
//                            Log.d("Don hang vua tao", "onResponse-createOrder: " + order);
                        }

                    } else { // nhận các đầu status #200
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Log.d(TAG.toString, "onResponse-createOrder: " + errorMessage);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }catch (IOException e){
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG.toString, "onFailure-createOrder: " + t.toString());
                    loadingDialog.dismiss();
                }
            });
        }
    }
    private void zaloRequest(){
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(String.valueOf(totalPrice));
            String code = data.getString("return_code");
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(PayActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        Log.d(TAG.toString, "onPaymentSucceeded: Thanh toan thanh cong");
                        removeDataCartZalo();
                        urlCreateOrderZalo();
//                        Intent intent = new Intent(PayActivity.this, OrderSuccessActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE_ORDER_SUCCESS);
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Log.d(TAG.toString, "onPaymentCanceled: Huy thanh toan");
                        urlCreateOrder();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d(TAG.toString, "onPaymentError: Thanh toan that bai");
                        urlCreateOrder();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ORDER_SUCCESS && resultCode == RESULT_OK) {
            // Xử lý khi quay trở lại từ màn hình OrderSuccessActivity
            // Thanh toán thành công
            Log.d(TAG.toString, "onActivityResult: Thanh toan thanh cong");
        }
    }

    private void removeDataCartZalo() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < CartUtil.listCartCheck.size(); i++) {
            int position = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    deleteCartZalo(CartUtil.listCartCheck.get(position));
                }
            });
        }
    }
    private void deleteCartZalo(final OptionAndQuantity cart) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        String cartId = cart.getId();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
        //loadingDialog.show();

        BaseApi.API.deleteCartItem(token, cartId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    Log.d(TAG.toString, "onResponse-deleteCartItem: " + serverResponse.toString());
                    if(serverResponse.getCode() == 200) {
//                        Intent intent = new Intent(PayActivity.this, OrderSuccessActivity.class);
//                        startActivity(intent);
                        Intent intent = new Intent(PayActivity.this, OrderSuccessActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_ORDER_SUCCESS);
                        CartUtil.listCart.removeAll(CartUtil.listCartCheck);
                        CartUtil.listCartCheck.clear();
                        finishAffinity();
                    }
                } else { // nhận các đầu status #200
                    try {
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
                loadingDialog.dismiss();
            }


            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(PayActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-deleteCartItem: " + t.toString());
                loadingDialog.dismiss();
            }
        });
    }
    //----------------End Zalo----------------------

    private boolean validatePurchare() {
        if(info == null) {
            Toast.makeText(this, "Mời nhập địa chỉ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void initView() {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        totalPrice = getIntent().getIntExtra("totalPrice", 0);
        binding.tvTotalPrice.setText(formatter.format(totalPrice) + " Đ");
        binding.disscount.setText(formatter.format(0) + " Đ");
        binding.totalOder.setText(formatter.format(totalPrice) + " Đ");
        binding.totalDisscount.setText(formatter.format(0) + " Đ");
        loadingDialog = new ProgressLoadingDialog(this);
        infoList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rcvProduct.setLayoutManager(linearLayoutManager);
        cartPayAdapter = new CartPayAdapter(this, CartUtil.listCartCheck);
        Log.e( "qwep: ",""+CartUtil.listCartCheck.toString() );
        binding.rcvProduct.setAdapter(cartPayAdapter);
    }

    private void calculateMultiVoucher(ArrayList<Voucher> selectedVouchers) {
        long totalOrderPrice = 0; // Tổng tiền cuối cùng của cả hóa đơn
        long totalDiscount = 0;   // Tổng số tiền được giảm giá

        // 1. Duyệt qua từng sản phẩm trong giỏ hàng của bạn
        for (OptionAndQuantity item : CartUtil.listCartCheck) {
            // Tính giá gốc của dòng sản phẩm này (Giá x Số lượng)
            long itemOriginalPrice = (long) item.getOptionProduct().getPrice() * item.getQuantity();
            long discountForThisItem = 0;

            // 2. Tìm xem trong danh sách Voucher đã chọn, cái nào áp dụng cho sản phẩm này
            for (Voucher v : selectedVouchers) {
                // Kiểm tra: Nếu voucher này có chứa ID của sản phẩm hiện tại

                    if (v.getDiscountType() == 1) { // Loại 1: Giảm theo %
                        discountForThisItem = (itemOriginalPrice * v.getDiscountValue()) / 100;


                    } else { // Loại 2: Giảm thẳng tiền mặt
                        discountForThisItem = v.getDiscountValue();
                    }

                    // Vì mỗi sản phẩm thường chỉ áp dụng 1 voucher, nên tìm thấy là thoát vòng lặp voucher
            }

            // 3. Tính giá sau khi giảm cho sản phẩm này
            long priceAfterDiscount = itemOriginalPrice - discountForThisItem;
            if (priceAfterDiscount < 0) priceAfterDiscount = 0;

            // 4. Cộng dồn vào tổng hóa đơn
            totalOrderPrice += priceAfterDiscount;
            totalDiscount += discountForThisItem;
            updatePriceUI(totalOrderPrice, totalDiscount);
        }

        // 5. Cập nhật lên giao diện (UI)
    }

    private void updatePriceUI(long totalPay, long totalDiscount) {
        // Định dạng số có dấu phân cách nghìn (1,000,000)
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        binding.tvTotalPrice.setText(formatter.format(totalPay) + " Đ");
        binding.disscount.setText(formatter.format(totalDiscount) + " Đ");
        binding.totalOder.setText(formatter.format(totalPay) + " Đ");
        binding.totalDisscount.setText(formatter.format(totalDiscount) + " Đ");
        // Lưu giá trị cuối cùng vào một biến toàn cục để gửi lên Server khi bấm "Mua hàng"
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}