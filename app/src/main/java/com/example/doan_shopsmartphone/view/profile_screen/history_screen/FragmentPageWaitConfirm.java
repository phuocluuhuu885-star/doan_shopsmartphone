package com.example.doan_shopsmartphone.view.profile_screen.history_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.doan_shopsmartphone.adapter.OrderAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentWaitConfirmBinding;
import com.example.doan_shopsmartphone.model.OptionAndQuantity;
import com.example.doan_shopsmartphone.model.Order;
import com.example.doan_shopsmartphone.model.OrderResponse;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ObjectUtil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPageWaitConfirm extends Fragment implements ObjectUtil {

    private FragmentWaitConfirmBinding binding;
    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    private ProgressLoadingDialog loadingDialog;

    public FragmentPageWaitConfirm() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentPageWaitConfirm newInstance() {
        FragmentPageWaitConfirm fragment = new FragmentPageWaitConfirm();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitConfirmBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        urlListOrder();
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(getActivity());
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), orderList, this, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvOrder.setLayoutManager(layoutManager);
        binding.rcvOrder.setAdapter(orderAdapter);
    }

    private void urlListOrder() {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(requireContext());

        binding.progressBar.setVisibility(View.VISIBLE);
        BaseApi.API.getListOrder(token, TAG.WAIT_CONFIRM).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    OrderResponse orderResponse = response.body();
                    assert orderResponse != null;
                    Log.d(TAG.toString, "onResponse-getListOrder: " + orderResponse);
                    if(orderResponse.getCode() == 200 || orderResponse.getCode() == 201) {
                        orderList = orderResponse.getResult();
                        for(Order order: orderList){
                            List<OptionAndQuantity> listoption = order.getProductsOrder();
                            for(OptionAndQuantity list: listoption){
                                Log.d("kiem tra discount", "discount: "+list.getDiscount_value());
                            }
                        }
                        orderAdapter.setListOrder(orderList);
                        if(orderList.size() == 0) {
                            binding.layoutDrum.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutDrum.setVisibility(View.GONE);
                        }
                    }
                } else { // nhận các đầu status #200
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-getListOrder: " + errorMessage);
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
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-getListOrder: " + t.toString());
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onclickObject(Object object) {
        Order order = (Order) object;
        
        udpateStatusOrder(order);
    }

    private void udpateStatusOrder(Order order) {
        String token = AccountUltil.BEARER + AccountUltil.getToken(requireContext());
        loadingDialog.show();
        BaseApi.API.updateOrderStatus(token, order.getId(), TAG.CANCELLED).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if(response.isSuccessful()){ // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    assert serverResponse != null;
                    Log.d(TAG.toString, "onResponse-updateOrderStatus: " + serverResponse.toString());
                    if(serverResponse.getCode() == 200) {
                        orderList.remove(order);
                        orderAdapter.setListOrder(orderList);
                    }
                } else { // nhận các đầu status #200
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-updateOrderStatus: " + errorMessage);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-updateOrderStatus: " + t.toString());
                loadingDialog.dismiss();
            }
        });
    }
}