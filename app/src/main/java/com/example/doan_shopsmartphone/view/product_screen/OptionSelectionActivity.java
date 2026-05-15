package com.example.doan_shopsmartphone.view.product_screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.AttributeAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.ActivityOptionSelectionBinding;
import com.example.doan_shopsmartphone.model.OptionProduct;
import com.example.doan_shopsmartphone.model.Product;
import com.example.doan_shopsmartphone.model.ProductDetail;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionSelectionActivity extends AppCompatActivity {

    private ActivityOptionSelectionBinding binding;
    private ProductDetail product;
    private List<OptionProduct> allOptions;
    
    private AttributeAdapter colorAdapter, ramAdapter, storageAdapter, conditionAdapter, integrityAdapter;
    
    private OptionProduct selectedOptionMatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        setupToolbar();
        setupAdapters();
        updateUI();
    }

    private void initData() {
        String productJson = getIntent().getStringExtra("product_json");
        if (productJson != null) {
            product = new Gson().fromJson(productJson, ProductDetail.class);
            allOptions = product.getOption();
        } else {
            finish();
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAdapters() {
        // Extract unique values
        List<String> colors = getUniqueValues("color");
        List<String> rams = getUniqueValues("ram");
        List<String> storages = getUniqueValues("storage");
        List<String> conditions = getUniqueValues("condition");
        List<String> integrities = getUniqueValues("integrity");

        colorAdapter = createAdapter(colors);
        ramAdapter = createAdapter(rams);
        storageAdapter = createAdapter(storages);
        conditionAdapter = createAdapter(conditions);
        integrityAdapter = createAdapter(integrities);

        binding.rvColors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvColors.setAdapter(colorAdapter);

        binding.rvRam.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRam.setAdapter(ramAdapter);

        binding.rvStorage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvStorage.setAdapter(storageAdapter);

        binding.rvCondition.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCondition.setAdapter(conditionAdapter);

        binding.rvIntegrity.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvIntegrity.setAdapter(integrityAdapter);
        
        binding.btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    private List<String> getUniqueValues(String type) {
        return allOptions.stream()
            .map(op -> {
                switch (type) {
                    case "color": return op.getNameColor();
                    case "ram": return op.getRam();
                    case "storage": return op.getStorageCapacity();
                    case "condition": return op.getConditionPercent();
                    case "integrity": return op.getIsOriginal();
                    default: return "";
                }
            })
            .filter(val -> val != null && !val.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    private AttributeAdapter createAdapter(List<String> values) {
        return new AttributeAdapter(values, selectedValue -> {
            updateOptionsAvailability();
            findMatch(
                    colorAdapter.getSelectedValue(),
                    ramAdapter.getSelectedValue(),
                    storageAdapter.getSelectedValue(),
                    conditionAdapter.getSelectedValue(),
                    integrityAdapter.getSelectedValue()
            );
            updateUI();
        });
    }

    private void updateOptionsAvailability() {
        colorAdapter.updateStates(getViableValues("color"));
        ramAdapter.updateStates(getViableValues("ram"));
        storageAdapter.updateStates(getViableValues("storage"));
        conditionAdapter.updateStates(getViableValues("condition"));
        integrityAdapter.updateStates(getViableValues("integrity"));
    }

    private List<String> getViableValues(String targetType) {
        return allOptions.stream()
                .filter(op -> matchesOther(op, targetType))
                .map(op -> {
                    switch (targetType) {
                        case "color": return op.getNameColor();
                        case "ram": return op.getRam();
                        case "storage": return op.getStorageCapacity();
                        case "condition": return op.getConditionPercent();
                        case "integrity": return op.getIsOriginal();
                        default: return "";
                    }
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean matchesOther(OptionProduct op, String targetType) {
        String selColor = colorAdapter.getSelectedValue();
        String selRam = ramAdapter.getSelectedValue();
        String selStorage = storageAdapter.getSelectedValue();
        String selCondition = conditionAdapter.getSelectedValue();
        String selIntegrity = integrityAdapter.getSelectedValue();

        if (!targetType.equals("color") && selColor != null && !selColor.equals(op.getNameColor())) return false;
        if (!targetType.equals("ram") && selRam != null && !selRam.equals(op.getRam())) return false;
        if (!targetType.equals("storage") && selStorage != null && !selStorage.equals(op.getStorageCapacity())) return false;
        if (!targetType.equals("condition") && selCondition != null && !selCondition.equals(op.getConditionPercent())) return false;
        if (!targetType.equals("integrity") && selIntegrity != null && !selIntegrity.equals(op.getIsOriginal())) return false;
        
        return true;
    }

    private void findMatch(String color, String ram, String storage, String condition, String integrity) {
        selectedOptionMatch = null;
        for (OptionProduct op : allOptions) {
            boolean mColor = (color == null) || color.equals(op.getNameColor());
            boolean mRam = (ram == null) || ram.equals(op.getRam());
            boolean mStorage = (storage == null) || storage.equals(op.getStorageCapacity());
            boolean mCondition = (condition == null) || condition.equals(op.getConditionPercent());
            boolean mIntegrity = (integrity == null) || integrity.equals(op.getIsOriginal());

            if (mColor && mRam && mStorage && mCondition && mIntegrity) {
                selectedOptionMatch = op;
                break;
            }
        }
    }

    private void updateUI() {
        if (selectedOptionMatch != null) {
            DecimalFormat df = new DecimalFormat("###,###,###");
            double discount = (double) (100 - selectedOptionMatch.getDiscountValue()) / 100;
            int price = (int) (selectedOptionMatch.getPrice() * discount);
            
            binding.tvPrice.setText(df.format(price) + " đ");
            binding.tvStock.setText("Kho: " + selectedOptionMatch.getQuantity());
            
            Glide.with(this).load(selectedOptionMatch.getImage()).placeholder(R.drawable.loading).into(binding.imgOption);
            
            binding.btnConfirm.setEnabled(selectedOptionMatch.getQuantity() > 0);
            if (selectedOptionMatch.getQuantity() <= 0) {
                binding.btnConfirm.setText("Hết hàng");
            } else {
                binding.btnConfirm.setText("Xác nhận");
            }
        } else {
            double calculatedMinPrice = Double.MAX_VALUE;
            int totalQuantity = 0;
            if (allOptions != null && allOptions.size() > 0) {
                for (OptionProduct op : allOptions) {
                    double discount = (double) (100 - op.getDiscountValue()) / 100;
                    double actualPrice = op.getPrice() * discount;
                    if (actualPrice < calculatedMinPrice) {
                        calculatedMinPrice = actualPrice;
                    }
                    totalQuantity += op.getQuantity();
                }
            }
            if (calculatedMinPrice == Double.MAX_VALUE) calculatedMinPrice = 0;
            
            DecimalFormat df = new DecimalFormat("###,###,###");
            binding.tvPrice.setText(df.format(calculatedMinPrice) + " đ");
            binding.tvStock.setText("Kho: " + totalQuantity);
            binding.btnConfirm.setEnabled(false);
            binding.btnConfirm.setText("Chọn cấu hình");
            
            if (allOptions.size() > 0) {
                Glide.with(this).load(allOptions.get(0).getImage()).into(binding.imgOption);
            }
        }
    }

    private void handleConfirm() {
        if (selectedOptionMatch == null) return;
        
        String token = AccountUltil.BEARER + AccountUltil.getToken(this);
        String optionId = selectedOptionMatch.getId();
        int quantity = 1; // Default to 1 for this flow, or add a counter if needed
        
        BaseApi.API.createCartItem(token, optionId, quantity).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OptionSelectionActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OptionSelectionActivity.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(OptionSelectionActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
