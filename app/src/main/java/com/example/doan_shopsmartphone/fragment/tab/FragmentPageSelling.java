package com.example.doan_shopsmartphone.fragment.tab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ProductByCategoryAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentPageSellingBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.ProductByCategory;
import com.example.doan_shopsmartphone.model.response.ProductByCategoryReponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPageSelling extends Fragment implements ObjectUtil {
    private ProductByCategoryAdapter productAdapter;
    private List<ProductByCategory> productList;
    private FragmentPageSellingBinding binding;

    public FragmentPageSelling() {

    }

    public static FragmentPageSelling newInstance(String param1, String param2) {
        FragmentPageSelling fragment = new FragmentPageSelling();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPageSellingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initController();
        callApiProductByCategory();
    }

    private void initController() {
    }

    private void initView() {
        productList = new ArrayList<>();
        productAdapter = new ProductByCategoryAdapter(getActivity(), productList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recycleProductSelling.setLayoutManager(linearLayoutManager);
        binding.recycleProductSelling.setAdapter(productAdapter);
    }
    private void callApiProductByCategory() {
        Log.d("checkkbuggggg", "davaoday");
        binding.progressBar.setVisibility(View.VISIBLE);
        BaseApi.API.getListProductByCategory(AccountUltil.TOKEN).enqueue(new Callback<ProductByCategoryReponse>() {
            @Override
            public void onResponse(Call<ProductByCategoryReponse> call, Response<ProductByCategoryReponse> response) {
                Log.d("checkkbuggggg", "davaoday1");

                if (response.isSuccessful()) { // chỉ nhận đầu status 200
                    ProductByCategoryReponse reponse = response.body();
                    Log.d(TAG.toString, "onResponse-ListProductByCategory: " + reponse.toString());
                    if (reponse.getCode() == 200) {
                        for (ProductByCategory productByCategory : reponse.getResult()) {
                            if (productByCategory.getProduct().size() > 0) {
                                productList.add(productByCategory);
                            }
                        }
                        productAdapter.setListProductType(productList);
                    }
                } else { // nhận các đầu status #200
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-register: " + errorMessage);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductByCategoryReponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onclickObject(Object object) {
        Product product = (Product) object;
        String id = product.getId();
        String averageRate = String.valueOf(product.getAverageRate());
        String sellproduct = String.valueOf(product.getSoldQuantity());
        String reviewcount = String.valueOf(product.getReviewCount());
        Double minPrice = (product.getMinPrice());
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}