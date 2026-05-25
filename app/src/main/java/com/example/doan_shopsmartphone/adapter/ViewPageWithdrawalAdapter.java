package com.example.doan_shopsmartphone.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doan_shopsmartphone.view.profile_screen.FragmentWithdrawalList;

public class ViewPageWithdrawalAdapter extends FragmentStateAdapter {

    public ViewPageWithdrawalAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return FragmentWithdrawalList.newInstance("pending");
        } else {
            return FragmentWithdrawalList.newInstance("processed");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
