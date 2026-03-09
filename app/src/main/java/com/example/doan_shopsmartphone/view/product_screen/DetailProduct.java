package com.example.doan_shopsmartphone.view.product_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.OptionAdapter;
import com.example.doan_shopsmartphone.adapter.ProductAdapter;
import com.example.doan_shopsmartphone.adapter.VoucherAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityDetailProductBinding;
import com.example.doan_shopsmartphone.databinding.LayoutDialigOptionProductBinding;
import com.example.doan_shopsmartphone.model.Comment;
import com.example.doan_shopsmartphone.model.OptionProduct;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.ProductDetail;
import com.example.doan_shopsmartphone.model.Voucher;
import com.example.doan_shopsmartphone.model.body.YeuthichRequestBody;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;

import java.text.DecimalFormat;
import java.util.List;

public class DetailProduct extends AppCompatActivity implements ObjectUtil {
    @Override
    public void onclickObject(Object object) {

    }

    public interface YeuthichCallback {
        void onSuccess(String data);
        void onFailure(Throwable t);
    }
    private ActivityDetailProductBinding binding;
    private List<Product> productList;
    private List<Voucher> voucherList;
    private ProductAdapter productAdapter;
    private VoucherAdapter voucherAdapter;
    private ProgressLoadingDialog dialog;
    private AlertDialog dialogCmt;
    private ProductDetail productDetail;
    private List<OptionProduct> optionProductList;
    private OptionAdapter optionAdapter;
    private int quantityProduct = 1;
    private OptionProduct optionProduct;
    private LayoutDialigOptionProductBinding bindingOption;
    private final int totalPrice = 0;
    private String strDetailProduct = "";
    private boolean isShowDetail  = false;
    List<YeuthichRequestBody> listYeuthich ;
    DecimalFormat df = new DecimalFormat("###,###,###");
    String ratingstar;
    String sold_quantity;
    String review_count ;

    Double minPrice ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ratingstar = intent.getStringExtra("rating_start");
        sold_quantity = intent.getStringExtra("sold_quantity");
        review_count = intent.getStringExtra("review_count");
        minPrice = intent.getDoubleExtra("minPrice",1000);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initController(this);
        getVoucher();
        callApiDetailProduct();
        setDataSimilarProduct();
        setNumberCart();

    }

    private void setNumberCart() {}

    private void getVoucher() {}

    public void  clickReview(Context context){}

    private void showReviewDialog(List<Comment> listComment) {}

    public void callApiDetailProduct() {}

    private void setDetailProduct() {}

    private void initView() {}

    private void initController(Context context) {}

    private void setDataSimilarProduct(){}
}