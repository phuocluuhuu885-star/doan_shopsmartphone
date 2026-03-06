package com.example.doan_shopsmartphone.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doan_shopsmartphone.fragment.tab.FragmentPageDiscount;
import com.example.doan_shopsmartphone.fragment.tab.FragmentPageOutStanding;
import com.example.doan_shopsmartphone.fragment.tab.FragmentPageSelling;

public class ViewPageHomeAdapter extends FragmentStateAdapter {
    public ViewPageHomeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentPageSelling();
            case 1:
                return new FragmentPageDiscount();
            case 2:
                return new FragmentPageOutStanding();
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

