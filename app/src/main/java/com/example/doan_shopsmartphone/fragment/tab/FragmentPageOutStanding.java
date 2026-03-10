package com.example.doan_shopsmartphone.fragment.tab;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ProductBestSellerAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentPageOutStandingBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.response.ProductResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.view.product_screen.DetailProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentPageOutStanding extends Fragment implements ObjectUtil {
    private List<Product> list;
    private ProductBestSellerAdapter adapter;
    private FragmentPageOutStandingBinding binding;

    public FragmentPageOutStanding() {
    }

    public static FragmentPageOutStanding newInstance(String param1, String param2) {
        FragmentPageOutStanding fragment = new FragmentPageOutStanding();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPageOutStandingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
        adapter = new ProductBestSellerAdapter(getContext(), list, this);
        binding.recyProBestSeller.setAdapter(adapter);
        callApiBestSeller();
    }

    private void callApiBestSeller() {
        binding.progressBar.setVisibility(View.VISIBLE);
        BaseApi.API.getListAllProduct(true, AccountUltil.USER.getToken()).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                binding.progressBar.setVisibility(View.GONE); // Ẩn progressBar sau khi nhận được dữ liệu
                Log.d("checkkbuggggg", "onResponse: "+response.body());
                if (response.isSuccessful()) {
                    ProductResponse productResponse = response.body();
                    List<Product> listProduct = productResponse.getResult();

                    // Sắp xếp danh sách sản phẩm theo soldQuantity giảm dần
                    Collections.sort(listProduct, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            return p2.getSoldQuantity() - p1.getSoldQuantity();
                        }
                    });

                    // Chỉ lấy top 10 sản phẩm
                    List<Product> topProducts = listProduct.subList(0, Math.min(listProduct.size(), 10));

                    adapter.setListProductBestSeller(topProducts);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không Có Dữ Liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE); // Ẩn progressBar nếu gặp lỗi
            }
        });
    }

    @Override
    public void onclickObject(Object object) {
        Product product = (Product) object;
        String id = product.getId();
        String averageRate = String.valueOf(product.getAverageRate());
        String sellproduct = String.valueOf(product.getSoldQuantity());
        String reviewcount = String.valueOf(product.getReviewCount());
        Double minPrice = (product.getMinPrice());
        Intent intent = new Intent(getActivity(), DetailProduct.class);
        intent.putExtra("id_product", id);
        intent.putExtra("sold_quantity",sellproduct);
        intent.putExtra("rating_start",averageRate);
        intent.putExtra("review_count",reviewcount);
        intent.putExtra("minPrice",minPrice);
        Log.d("rating_start", "onclickObject: "+averageRate);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}