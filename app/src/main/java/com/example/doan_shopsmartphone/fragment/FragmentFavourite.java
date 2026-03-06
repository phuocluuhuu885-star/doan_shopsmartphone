package com.example.doan_shopsmartphone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.FavouriteProductAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentFavouriteBinding;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavourite extends Fragment implements ObjectUtil {
    private List<Product> listProduct = new ArrayList<>();
    private List<Product> listProductFavourite= new ArrayList<>();
    private FavouriteProductAdapter favouriteProductAdapter;
    private FragmentFavouriteBinding binding;
    public FragmentFavourite() {
        // Required empty public constructor
    }


    public static FragmentFavourite newInstance(String param1, String param2) {
        FragmentFavourite fragment = new FragmentFavourite();
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
        binding = FragmentFavouriteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backDetailProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProfile profile = new FragmentProfile();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.framelayout, profile)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recycleProductFavourite.setLayoutManager(layoutManager);
        listProductFavourite = new ArrayList<>();
        favouriteProductAdapter = new FavouriteProductAdapter(getContext(), listProductFavourite, this);
        favouriteProductAdapter.setProductList(listProductFavourite);
        binding.recycleProductFavourite.setAdapter(favouriteProductAdapter);

    }

    @Override
    public void onclickObject(Object object) {
        Product product = (Product) object;
        String id = product.getId();
        String averageRate = String.valueOf(product.getAverageRate());
        String sellproduct = String.valueOf(product.getSoldQuantity());
        String reviewcount = String.valueOf(product.getReviewCount());
        Double minPrice = (product.getMinPrice());
        Intent intent = new Intent();
        intent.putExtra("id_product", id);
        intent.putExtra("sold_quantity",sellproduct);
        intent.putExtra("rating_start",averageRate);
        intent.putExtra("review_count",reviewcount);
        intent.putExtra("minPrice",minPrice);
        Log.d("checkkgiaaa", "checkkkigaaaaa: "+minPrice);
        getActivity().startActivity(intent);
    }
}