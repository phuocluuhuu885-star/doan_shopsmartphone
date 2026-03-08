package com.example.doan_shopsmartphone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ProductAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentProductBinding;
import com.example.doan_shopsmartphone.model.Banner;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentProduct extends Fragment  implements ObjectUtil {
    private List<Product> listProdcut;
    private ProductAdapter productAdapter;
    private FragmentProductBinding binding;


    public FragmentProduct() {
        // Required empty public constructor
    }


    public static FragmentProduct newInstance() {
        FragmentProduct fragment = new FragmentProduct();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initController();
        callApiBanner();
        callApiGetListAllProducts();
        setNumberCart();
    }

    public void callApiGetListAllProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);

        //handle call api get list product
    }

    private void setNumberCart() {
        // Lấy danh sách cart
        binding.tvQuantityCart.setText(CartUtil.listCart.size() + "");
    }

    private void callApiBanner() {
        // handle call api banner product
    }

    private void setDataBanner(List<Banner> data) {
        ArrayList<SlideModel> list  = new ArrayList<>();
        List<String> tabTitles = new ArrayList<>();
        for (Banner banner: data) {
            list.add(new SlideModel(banner.getImage() , ScaleTypes.FIT));
            tabTitles.add(banner.getNote());
        }
        binding.sliderProduct.setImageList(list, ScaleTypes.FIT);
    }

    private void initController() {
        binding.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), CartActivity.class);
//                startActivity(intent);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    productAdapter.filterItem(s.toString());

                }else {
                    productAdapter.filterItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), FindProduct.class);
//                startActivity(intent);
            }
        });
    }
    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recycleProduct.setLayoutManager(layoutManager);
        listProdcut = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), listProdcut,this );
        productAdapter.setProductList(listProdcut);
        binding.recycleProduct.setAdapter(productAdapter);
    }

    @Override
    public void onclickObject(Object object) {
        Product product = (Product) object;
        String id = product.getId();
        String averageRate = String.valueOf(product.getAverageRate());
        String sellproduct = String.valueOf(product.getSoldQuantity());
        String reviewcount = String.valueOf(product.getReviewCount());
        Double minPrice = (product.getMinPrice());
//        Intent intent = new Intent(getActivity(), DetailProduct.class);
//        intent.putExtra("id_product", id);
//        intent.putExtra("sold_quantity",sellproduct);
//        intent.putExtra("rating_start",averageRate);
//        intent.putExtra("review_count",reviewcount);
//        intent.putExtra("minPrice",minPrice);
//        Log.d("rating_start", "onclickObject: "+averageRate);
//        getActivity().startActivity(intent);
    }

}