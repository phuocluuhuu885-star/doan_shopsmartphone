package com.example.doan_shopsmartphone.fragment;

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

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ViewPageHomeAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentHomeBinding;
import com.example.doan_shopsmartphone.model.Banner;
import com.example.doan_shopsmartphone.model.response.BannerReponse;
import com.example.doan_shopsmartphone.ultil.ApiUtil;
import com.example.doan_shopsmartphone.ultil.TAG;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPageHomeAdapter viewPagerHomeAdapter;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();

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
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setTab();
        initController();
        callApiBanner();
        ApiUtil.setTitleQuantityCart(getActivity(),binding.tvQuantityCart);
    }

    private void setTab() {
        viewPagerHomeAdapter = new ViewPageHomeAdapter(getActivity());
        binding.viewPagerHome.setAdapter(viewPagerHomeAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabHome, binding.viewPagerHome, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Sản phẩm");
                        break;
                    case 1:
                        tab.setText("Giảm giá");
                        break;
                    case 2:
                        tab.setText("Bán chạy");
                        break;
                    default:
                        tab.setText("Liên quan");
                }
            }
        });
        mediator.attach();
    }

    private void callApiBanner() {
        BaseApi.API.getListBanner().enqueue(new Callback<BannerReponse>() {
            @Override
            public void onResponse(Call<BannerReponse> call, Response<BannerReponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    BannerReponse reponse = response.body();
                    Log.d(TAG.toString, "onResponse-getListBanner: " + reponse.toString());
                    if(reponse.getCode() == 200) {
                        setDataBanner(reponse.getData());
                    }
                } else { // nhận các đầu status #200
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-getListBanner: " + errorMessage);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerReponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataBanner(List<Banner> data) {
        ArrayList<SlideModel> list  = new ArrayList<>();

        for (Banner banner: data) {
            list.add(new SlideModel(banner.getImage() , ScaleTypes.FIT));

        }
        binding.sliderHome.setImageList(list, ScaleTypes.FIT);
    }

    private void initController() {
        binding.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.findPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        binding.chatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}