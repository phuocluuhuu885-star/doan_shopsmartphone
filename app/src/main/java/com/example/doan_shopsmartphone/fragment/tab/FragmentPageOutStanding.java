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
import com.example.doan_shopsmartphone.databinding.FragmentPageOutStandingBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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