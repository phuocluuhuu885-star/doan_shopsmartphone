package com.example.doan_shopsmartphone.view.product_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CommentAdapter;
import com.example.doan_shopsmartphone.databinding.ActivityCommentBinding;
import com.example.doan_shopsmartphone.model.Comment;
import com.example.doan_shopsmartphone.model.ProductDetail;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;
    private ProductDetail productDetail;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initController();
        getListComment();
    }

    private void initController() {
        binding.btnSend.setOnClickListener(view -> {
        });

        binding.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });
    }



    private void getListComment() {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);

        binding.progressBar.setVisibility(View.VISIBLE);

    }

    private void initView() {
        productDetail = new ProductDetail();
        productDetail.setId(getIntent().getStringExtra("id_product"));
        productDetail.setName(getIntent().getStringExtra("name"));
        Log.d(TAG.toString, "setId: " + productDetail.getId());
        Log.d(TAG.toString, "setName: " + productDetail.getName());


        commentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(CommentActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rcvComment.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(CommentActivity.this, commentList);
        binding.rcvComment.setAdapter(commentAdapter);

        Glide.with(this)
                .load(AccountUltil.USER.getAvatar())
                .placeholder(R.drawable.loading)
                .error(R.drawable.avatar1)
                .into(binding.imgAvartar);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
//    }

}