package com.example.doan_shopsmartphone.fragment.tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ProductByCategoryAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentPageSellingBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.ProductByCategory;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        return inflater.inflate(R.layout.fragment_page_selling, container, false);
    }

    private void initController() {
    }

    private void initView() {
        productList = new ArrayList<>();
        productAdapter = new ProductByCategoryAdapter(getActivity(), productList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recycleProductMain.setLayoutManager(linearLayoutManager);
        binding.recycleProductMain.setAdapter(productAdapter);
    }
    private void callApiProductByCategory() {
        Log.d("checkkbuggggg", "davaoday");
        binding.progressBar.setVisibility(View.VISIBLE);

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