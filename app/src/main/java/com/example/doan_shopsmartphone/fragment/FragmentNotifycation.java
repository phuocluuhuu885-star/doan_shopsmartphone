package com.example.doan_shopsmartphone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.NotificationAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentNotifycationBinding;
import com.example.doan_shopsmartphone.model.Notifi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class FragmentNotifycation extends Fragment {
    private FragmentNotifycationBinding binding;
    private NotificationAdapter notificationAdapter;
    private List<Notifi> notifiList;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    public FragmentNotifycation() {
        // Required empty public constructor
    }


    public static FragmentNotifycation newInstance() {
        FragmentNotifycation fragment = new FragmentNotifycation();
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
        // Inflate the layout for this fragment
        binding = FragmentNotifycationBinding.inflate(getLayoutInflater());
        initView();
        initController();
        setNumberCart();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                initController();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initController() {

        binding.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    private void setNumberCart() {
        //set number cart
    }
    public  void initView(){
        Log.d("cccccccccccccccc", "initView: +vao day");
        notifiList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rcvNofity.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(getActivity(), notifiList);
        binding.rcvNofity.setAdapter(notificationAdapter);
    }
}