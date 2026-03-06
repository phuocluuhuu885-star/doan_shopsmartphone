package com.example.doan_shopsmartphone.fragment.tab;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ProductSaleAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentPageDiscountBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageDiscount extends Fragment implements ObjectUtil {

    private List<Product> list;
    private ProductSaleAdapter adapter;
    private FragmentPageDiscountBinding binding;

    public FragmentPageDiscount() {
    }

    public static FragmentPageDiscount newInstance(String param1, String param2) {
        FragmentPageDiscount fragment = new FragmentPageDiscount();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPageDiscountBinding.inflate(inflater,container,false);
        list = new ArrayList<>();
        adapter = new ProductSaleAdapter(getContext(), list, this);
        binding.recyProSale.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
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
    }

}