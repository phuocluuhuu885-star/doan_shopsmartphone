package com.example.doan_shopsmartphone.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doan_shopsmartphone.view.profile_screen.history_screen.FragmentPageCancelled;
import com.example.doan_shopsmartphone.view.profile_screen.history_screen.FragmentPageDeliveried;
import com.example.doan_shopsmartphone.view.profile_screen.history_screen.FragmentPageDelivering;
import com.example.doan_shopsmartphone.view.profile_screen.history_screen.FragmentPageWaitConfirm;
import com.example.doan_shopsmartphone.view.profile_screen.history_screen.FragmentPageWaitingDelivery;

public class ViewPageOrderAdapter extends FragmentStateAdapter {
    public ViewPageOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentPageWaitConfirm();
            case 1:
                return new FragmentPageWaitingDelivery();
            case 2:
                return new FragmentPageDelivering();
            case 3:
                return new FragmentPageDeliveried();
            case 4:
                return new FragmentPageCancelled();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
