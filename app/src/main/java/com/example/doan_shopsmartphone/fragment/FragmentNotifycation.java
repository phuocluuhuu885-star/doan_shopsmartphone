package com.example.doan_shopsmartphone.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doan_shopsmartphone.R;


public class FragmentNotifycation extends Fragment {



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
        return inflater.inflate(R.layout.fragment_notifycation, container, false);
    }
}