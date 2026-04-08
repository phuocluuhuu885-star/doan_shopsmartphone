package com.example.doan_shopsmartphone.fragment;

import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.NotificationAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentNotificationBinding;
import com.example.doan_shopsmartphone.model.Notifi;
import com.example.doan_shopsmartphone.model.response.ListNotifiReponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.CartUtil;
import com.example.doan_shopsmartphone.ultil.NotificationUtil;
import com.example.doan_shopsmartphone.ultil.SocketManager;
import com.example.doan_shopsmartphone.view.Cart.CartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentNotification extends Fragment {

    private FragmentNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private List<Notifi> notifiList;
    private Socket mSocket;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    public FragmentNotification() {
        // Required empty public constructor
    }

    public static FragmentNotification newInstance() {
        FragmentNotification fragment = new FragmentNotification();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket = SocketManager.getInstance().getSocket();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// Chỉ khởi tạo View và Adapter 1 lần ở đây
        initView();
        initController();
        Log.e( "idPro: ",CartUtil.listCartCheck.toString() );
        // Gọi dữ liệu lần đầu
        getListNotify();
        setNumberCart();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListNotify();
                setNumberCart();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getListNotify() {
        Log.e("APIR", "Token is missing!");
        String rawToken = AccountUltil.getToken(requireContext());

        String token = AccountUltil.BEARER + rawToken;
        binding.progressBar.setVisibility(View.VISIBLE);
        Log.d("poas", "getListNotify: "+ AccountUltil.USER.getId());
        BaseApi.API.getNotifiList(token, AccountUltil.USER.getId()).enqueue(new Callback<ListNotifiReponse>() {
            @Override
            public void onResponse(Call<ListNotifiReponse> call, Response<ListNotifiReponse> response) {
                binding.progressBar.setVisibility(View.GONE); // Luôn ẩn khi có phản hồi

                if(response.isSuccessful() && response.body() != null){
                    ListNotifiReponse res = response.body();
                    if(res.getCode() == 200 || res.getCode() == 201) {
                        notifiList.clear();
                        notifiList.addAll(res.getResult());
                        notificationAdapter.notifyDataSetChanged();

                    }
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ListNotifiReponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });
    }
    private void initController() {

        binding.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }
    private void setNumberCart() {
        // Lấy danh sách cart
        binding.tvQuantityCart.setText(CartUtil.listCart.size() + "");
    }
    public  void initView(){
        notifiList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rcvNofity.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(getActivity(), notifiList);
        binding.rcvNofity.setAdapter(notificationAdapter);
    }



}