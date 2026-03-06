package com.example.doan_shopsmartphone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ViewPageHomeAdapter;
import com.example.doan_shopsmartphone.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setTab();
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
}