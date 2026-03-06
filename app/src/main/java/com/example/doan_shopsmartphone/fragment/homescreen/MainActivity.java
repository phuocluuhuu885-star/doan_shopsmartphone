package com.example.doan_shopsmartphone.fragment.homescreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

//import com.etebarian.NafisBottomNavigation.NafisBottomNavigation;

import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.databinding.ActivityMainBinding;
import com.example.doan_shopsmartphone.fragment.FragmentHome;
import com.example.doan_shopsmartphone.fragment.FragmentProduct;
import com.example.doan_shopsmartphone.fragment.FragmentProfile;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClickBottomNav();
    }

    private void onClickBottomNav() {
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(1,R.drawable.homepage));
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(2,R.drawable.product_24));
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(3,R.drawable.thongbao));
        binding.bottomNavigation.add(new NafisBottomNavigation.Model(4,R.drawable.profile));
        binding.bottomNavigation.show(1,true);
        loadFragment(FragmentHome.newInstance());

        binding.bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        loadFragment(FragmentHome.newInstance());
                        break;
                    case 2:
                        loadFragment(FragmentProduct.newInstance());
                        break;
                    case 3:
                        loadFragment(FragmentNotification.newInstance());
                        break;
                    case 4:
                        loadFragment(FragmentProfile.newInstance());
                        break;
                }
                return null;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,fragment);
        transaction.commit();
    }
}
