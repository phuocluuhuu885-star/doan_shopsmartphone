package com.example.doan_shopsmartphone.view.buy_product;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Locale;
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
import com.example.doan_shopsmartphone.view.Cart.ChangePaymentMethodsActivity;
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
    private ArrayList<Voucher> selectedVouchersList = new ArrayList<>();

    private List<String> productIds;
    private static final int REQUEST_CODE_ORDER_SUCCESS = 1;


    ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Nhận lại danh sách voucher
                    ArrayList<Voucher> selectedList = (ArrayList<Voucher>) result.getData().getSerializableExtra("LIST_VOUCHER_SELECTED");

                    if (selectedList != null) {
                        selectedVouchersList = selectedList; // Lưu lại danh sách đã chọn
                        if (!selectedList.isEmpty()) {
                            Log.e("voucher_selected", selectedList.toString());
                        }
                        calculateMultiVoucher(selectedList);
                        if (cartPayAdapter != null) {
                            cartPayAdapter.notifyDataSetChanged();
                        }
                        // Chạy hàm tính toán tổng tiền cho từng sản phẩm như mình đã hướng dẫn ở trên
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> paymentMethodsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            int selected = result.getData().getIntExtra("paymentMethods", paymentMethods);
                            paymentMethods = selected;
                            updatePaymentMethodsUI();
                        }
                    }
            );

    private void updatePaymentMethodsUI() {
        if (paymentMethods == 1) {
            binding.txtPaymentMethods.setText("Thanh toán khi nhận hàng");
        } else if (paymentMethods == 2) {
            binding.txtPaymentMethods.setText("Thanh toán qua ví ZaloPay");
        }
    }

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
            updatePaymentMethodsUI();

        }
        if (paymentMethods == 2){
            updatePaymentMethodsUI();
        }

         productIds = CartUtil.listCartCheck.stream()
                .map(cartItem -> cartItem.getOptionProduct()) // Lấy Object OptionOfListCart
                .filter(Objects::nonNull)                     // Kiểm tra null để tránh crash
                .map(option -> option.getProduct())           // Lấy Object Product
                .filter(Objects::nonNull)                     // Kiểm tra null tiếp
                .map(product -> product.getId())              // Cuối cùng lấy Product ID
                .distinct()                                   // Chỉ lấy các ID duy nhất để nhóm voucher
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
        if(infoList == null || infoList.size() == 0) {
            binding.tvInfoUser.setText("Chưa có địa chỉ mời bạn tạo");
            info = null;
            return;
        }
        info = null;
        for (int i = 0; i < infoList.size(); i++) {
            if(infoList.get(i).getChecked()) {
                info = infoList.get(i);
                break;
            }
        }
        if(info != null) {
            binding.tvInfoUser.setText(info.getName() + " | " + info.getPhoneNumber() + " | " + info.getAddress());
        } else {
            binding.tvInfoUser.setText("Vui lòng chọn địa chỉ giao hàng");
        }
    }
    private void initController() {
        binding.layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, AddressActivity.class);
                if (info != null && info.getId() != null) {
                    intent.putExtra("CURRENT_SELECTED_ADDR_ID", info.getId());
                }
                mActivityResultLauncher.launch(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        binding.onVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, VoucherScreen.class);
                intent.putStringArrayListExtra("LIST_PRODUCT_ID", (ArrayList<String>) productIds);
                intent.putExtra("LIST_VOUCHER_SELECTED", selectedVouchersList); // Truyền danh sách đã chọn sang
                voucherLauncher.launch(intent);
            }
        });

        binding.layoutPaymentMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, ChangePaymentMethodsActivity.class);
                intent.putExtra("paymentMethods", paymentMethods);
                paymentMethodsLauncher.launch(intent);
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
                        if (intent == null || intent.getExtras() == null) return;
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
                        if (info != null) {
                            binding.tvInfoUser.setText(info.getName() + " | " + info.getPhoneNumber() + " | " + info.getAddress());
                        }
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
        String productPreview = buildOrderProductPreview();
        String content = "Bạn có đơn hàng mới: " + (productPreview.isEmpty() ? orderId : productPreview);

        NotificationBody body = new NotificationBody(
                AccountUltil.USER.getId(),   // Người gửi (User)
                AccountUltil.USER.getId(),           // ID Admin hoặc Shop (Receiver)
                orderId,                     // ID đơn hàng vừa tạo
                content,
                "wfc"                        // Type: Chờ xác nhận
        );

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

    private String buildOrderProductPreview() {
        if (CartUtil.listCartCheck == null || CartUtil.listCartCheck.isEmpty()) {
            return "";
        }

        List<String> productNames = new ArrayList<>();
        for (OptionAndQuantity cartItem : CartUtil.listCartCheck) {
            if (cartItem == null || cartItem.getOptionProduct() == null || cartItem.getOptionProduct().getProduct() == null) {
                continue;
            }
            String name = cartItem.getOptionProduct().getProduct().getName();
            if (name != null && !name.trim().isEmpty()) {
                productNames.add(name.trim());
            }
            if (productNames.size() >= 2) {
                break;
            }
        }

        if (productNames.isEmpty()) {
            return "";
        }

        if (CartUtil.listCartCheck.size() > 2) {
            return TextUtils.join(", ", productNames) + ", ...";
        }

        return TextUtils.join(", ", productNames);
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
        int totalOrderPrice = 0; // Tổng tiền cuối cùng của cả hóa đơn
        int totalDiscount = 0;   // Tổng số tiền được giảm giá
        Map<String, Integer> voucherMap = new HashMap<>(); // Lưu trữ tiền giảm cho từng mã code

        if (selectedVouchers == null) {
            selectedVouchers = new ArrayList<>();
        }

        // 1. Nhóm các sản phẩm trong giỏ hàng theo Product ID
        Map<String, List<OptionAndQuantity>> groupedItems = new HashMap<>();
        for (OptionAndQuantity item : CartUtil.listCartCheck) {
            if (item == null || item.getOptionProduct() == null || item.getOptionProduct().getProduct() == null) {
                // Trường hợp không có sản phẩm (có thể là lỗi dữ liệu)
                continue;
            }
            String pId = item.getOptionProduct().getProduct().getId();
            if (!groupedItems.containsKey(pId)) {
                groupedItems.put(pId, new ArrayList<>());
            }
            groupedItems.get(pId).add(item);
        }

        // 2. Duyệt qua từng nhóm sản phẩm để áp dụng voucher
        for (Map.Entry<String, List<OptionAndQuantity>> entry : groupedItems.entrySet()) {
            List<OptionAndQuantity> itemsInGroup = entry.getValue();
            if (itemsInGroup.isEmpty()) continue;

            // Tìm voucher cho cả nhóm (dùng sản phẩm đầu tiên làm đại diện vì cùng Product ID)
            Voucher matchedVoucher = findVoucherForItem(itemsInGroup.get(0), selectedVouchers);

            // Tính tổng giá trị của nhóm sau khi trừ giảm giá mặc định của từng màu
            int totalGroupBasePrice = 0;
            Map<OptionAndQuantity, Integer> itemBasePriceMap = new HashMap<>();
            Map<OptionAndQuantity, Integer> itemOptionDiscountMoneyMap = new HashMap<>();

            for (OptionAndQuantity item : itemsInGroup) {
                int unitPrice = (int) item.getOptionProduct().getPrice();
                int itemOriginalPrice = unitPrice * item.getQuantity();
                int optionDiscountPercent =(int) Math.max(0, item.getOptionProduct().getDiscountValue());
                int optionDiscountMoney = (itemOriginalPrice * optionDiscountPercent) / 100;
                int priceAfterOptionDiscount = Math.max(itemOriginalPrice - optionDiscountMoney, 0);

                totalGroupBasePrice += priceAfterOptionDiscount;
                itemBasePriceMap.put(item, priceAfterOptionDiscount);
                itemOptionDiscountMoneyMap.put(item, optionDiscountMoney);
            }

            // Tính tổng tiền voucher giảm cho cả nhóm (không tính riêng từng option)
            int totalVoucherDiscountForGroup = calculateVoucherMoney(matchedVoucher, totalGroupBasePrice);

            // Tích lũy vào voucherMap để hiển thị trên UI
            if (matchedVoucher != null && totalVoucherDiscountForGroup > 0) {
                String code = matchedVoucher.getCode();
                voucherMap.put(code, voucherMap.getOrDefault(code, 0) + totalVoucherDiscountForGroup);
            }

            // Phân bổ tiền giảm của voucher cho từng item trong nhóm
            for (OptionAndQuantity item : itemsInGroup) {
                int itemBasePrice = itemBasePriceMap.get(item);
                int itemOptionDiscountMoney = itemOptionDiscountMoneyMap.get(item);
                int itemOriginalPrice = (int) item.getOptionProduct().getPrice() * item.getQuantity();

                int itemVoucherDiscount = 0;
                if (totalGroupBasePrice > 0) {
                    // Phân bổ theo tỷ lệ giá trị của item trong nhóm
                    itemVoucherDiscount = (int) ((long) totalVoucherDiscountForGroup * itemBasePrice / totalGroupBasePrice);
                }

                int totalDiscountForThisItem = Math.min(itemOptionDiscountMoney + itemVoucherDiscount, itemOriginalPrice);
                int priceAfterDiscount = itemOriginalPrice - totalDiscountForThisItem;
                if (priceAfterDiscount < 0) priceAfterDiscount = 0;

                // Cập nhật % giảm giá cuối cùng để gửi lên backend
                int mergedPercent = 0;
                if (itemOriginalPrice > 0) {
                    mergedPercent = Math.round((totalDiscountForThisItem * 100f) / itemOriginalPrice);
                }
                mergedPercent = Math.max(0, Math.min(100, mergedPercent));
                item.setDiscount_value(mergedPercent);

                totalOrderPrice += priceAfterDiscount;
                totalDiscount += totalDiscountForThisItem;
            }
        }

        // 2. Tạo chuỗi chi tiết voucher
        StringBuilder details = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        for (Map.Entry<String, Integer> entry : voucherMap.entrySet()) {
            if (details.length() > 0) details.append("\n");
            details.append("[").append(entry.getKey()).append("]: -")
                    .append(formatter.format(entry.getValue())).append("đ");
        }

        // 3. Cập nhật lên giao diện
        updatePriceUI(totalOrderPrice, totalDiscount, details.toString());
    }

    private Voucher findVoucherForItem(OptionAndQuantity item, List<Voucher> selectedVouchers) {
        if (item == null || item.getOptionProduct() == null || item.getOptionProduct().getProduct() == null) {
            return null;
        }
        String productId = item.getOptionProduct().getProduct().getId();
        if (productId == null) return null;

        Voucher globalVoucher = null;
        for (Voucher voucher : selectedVouchers) {
            if (voucher == null) continue;
            List<Voucher.ProductObj> applicableProducts = voucher.getApplicableProducts();
            if (applicableProducts == null || applicableProducts.isEmpty()) {
                if (globalVoucher == null) globalVoucher = voucher;
                continue;
            }
            for (Voucher.ProductObj productObj : applicableProducts) {
                if (productObj != null && productId.equals(productObj.get_id())) {
                    return voucher;
                }
            }
        }
        return globalVoucher;
    }

    private int calculateVoucherMoney(Voucher voucher, int basePriceAfterOptionDiscount) {
        if (voucher == null || basePriceAfterOptionDiscount <= 0) {
            return 0;
        }

        if (voucher.getMinOrderValue() > 0 && basePriceAfterOptionDiscount < voucher.getMinOrderValue()) {
            return 0;
        }

        int voucherDiscountMoney;
        if (voucher.getDiscountType() == 1) { // giảm theo %
            voucherDiscountMoney = (basePriceAfterOptionDiscount * voucher.getDiscountValue()) / 100;
            if (voucher.getMaxDiscountValue() > 0) {
                voucherDiscountMoney = Math.min(voucherDiscountMoney, voucher.getMaxDiscountValue());
            }
        } else { // giảm tiền trực tiếp
            voucherDiscountMoney = voucher.getDiscountValue();
        }
        return Math.max(0, Math.min(voucherDiscountMoney, basePriceAfterOptionDiscount));
    }

    private void updatePriceUI(int totalPay, int totalDiscount, String voucherDetails) {
        // Định dạng số có dấu phân cách nghìn (1,000,000)
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        binding.tvTotalPrice.setText(formatter.format(totalPay) + " Đ");
        binding.disscount.setText(formatter.format(totalDiscount) + " Đ");
        binding.totalOder.setText(formatter.format(totalPay + totalDiscount) + " Đ"); // Tổng tiền hàng chưa giảm
        binding.totalDisscount.setText(formatter.format(totalDiscount) + " Đ");
        
        // Hiển thị chi tiết voucher
        if (voucherDetails != null && !voucherDetails.isEmpty()) {
            binding.tvVoucherDetails.setVisibility(View.VISIBLE);
            binding.tvVoucherDetails.setText(voucherDetails);
        } else {
            binding.tvVoucherDetails.setVisibility(View.GONE);
        }
        
        // Dùng cho luồng tạo đơn Zalo
        totalPrice = totalPay;
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