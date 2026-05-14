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
    
    private AttributeAdapter colorAdapter, storageAdapter, conditionAdapter, batteryAdapter, integrityAdapter, warrantyAdapter;
    
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
        List<String> colors = getUniqueValues(allOptions, "color");
        List<String> storages = getUniqueValues(allOptions, "storage");
        List<String> conditions = getUniqueValues(allOptions, "condition");
        List<String> batteries = getUniqueValues(allOptions, "battery");
        List<String> integrities = getUniqueValues(allOptions, "integrity");
        List<String> warranties = getUniqueValues(allOptions, "warranty");

        colorAdapter = createAdapter(colors);
        storageAdapter = createAdapter(storages);
        conditionAdapter = createAdapter(conditions);
        batteryAdapter = createAdapter(batteries);
        integrityAdapter = createAdapter(integrities);
        warrantyAdapter = createAdapter(warranties);

        binding.rvColors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvColors.setAdapter(colorAdapter);

        binding.rvStorage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvStorage.setAdapter(storageAdapter);

        binding.rvCondition.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCondition.setAdapter(conditionAdapter);

        binding.rvBattery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvBattery.setAdapter(batteryAdapter);

        binding.rvIntegrity.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvIntegrity.setAdapter(integrityAdapter);

        binding.rvWarranty.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvWarranty.setAdapter(warrantyAdapter);
        
        binding.btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    private List<String> getUniqueValues(List<OptionProduct> options, String type) {
        Set<String> set = new HashSet<>();
        for (OptionProduct op : options) {
            String val = "";
            switch (type) {
                case "color": val = op.getNameColor(); break;
                case "storage": val = op.getStorageCapacity(); break;
                case "condition": val = op.getConditionPercent(); break;
                case "battery": val = op.getBatteryHealth(); break;
                case "integrity": val = op.getIsOriginal(); break;
                case "warranty": val = op.getWarrantyTime(); break;
            }
            if (val != null && !val.isEmpty()) set.add(val);
        }
        return new ArrayList<>(set);
    }

    private AttributeAdapter createAdapter(List<String> values) {
        return new AttributeAdapter(values, value -> {
            evaluateStates();
            updateUI();
        });
    }

    private void evaluateStates() {
        String selColor = colorAdapter.getSelectedValue();
        String selStorage = storageAdapter.getSelectedValue();
        String selCondition = conditionAdapter.getSelectedValue();
        String selBattery = batteryAdapter.getSelectedValue();
        String selIntegrity = integrityAdapter.getSelectedValue();
        String selWarranty = warrantyAdapter.getSelectedValue();

        // Dynamic Filtering Logic: 
        // For each group, calculate which values are "viable" given the selections in OTHER groups.
        
        colorAdapter.updateStates(getViableValues("color", selStorage, selCondition, selBattery, selIntegrity, selWarranty));
        storageAdapter.updateStates(getViableValues("storage", selColor, selCondition, selBattery, selIntegrity, selWarranty));
        conditionAdapter.updateStates(getViableValues("condition", selColor, selStorage, selBattery, selIntegrity, selWarranty));
        batteryAdapter.updateStates(getViableValues("battery", selColor, selStorage, selCondition, selIntegrity, selWarranty));
        integrityAdapter.updateStates(getViableValues("integrity", selColor, selStorage, selCondition, selBattery, selWarranty));
        warrantyAdapter.updateStates(getViableValues("warranty", selColor, selStorage, selCondition, selBattery, selIntegrity));

        // Try to find a perfect match
        findMatch(selColor, selStorage, selCondition, selBattery, selIntegrity, selWarranty);
    }

    private List<String> getViableValues(String targetType, String... otherSelections) {
        // Logic: Filter allOptions where they match all non-null otherSelections
        return allOptions.stream()
                .filter(op -> matchesOther(op, targetType, otherSelections))
                .map(op -> {
                    switch (targetType) {
                        case "color": return op.getNameColor();
                        case "storage": return op.getStorageCapacity();
                        case "condition": return op.getConditionPercent();
                        case "battery": return op.getBatteryHealth();
                        case "integrity": return op.getIsOriginal();
                        case "warranty": return op.getWarrantyTime();
                        default: return "";
                    }
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean matchesOther(OptionProduct op, String targetType, String[] others) {
        // indices mapping: 0:color, 1:storage, 2:condition, 3:battery, 4:integrity, 5:warranty
        // But the input 'others' depends on which targetType we are evaluating.
        // This is a bit tricky, let's use the explicit selection variables.
        
        String selColor = colorAdapter.getSelectedValue();
        String selStorage = storageAdapter.getSelectedValue();
        String selCondition = conditionAdapter.getSelectedValue();
        String selBattery = batteryAdapter.getSelectedValue();
        String selIntegrity = integrityAdapter.getSelectedValue();
        String selWarranty = warrantyAdapter.getSelectedValue();

        if (!targetType.equals("color") && selColor != null && !selColor.equals(op.getNameColor())) return false;
        if (!targetType.equals("storage") && selStorage != null && !selStorage.equals(op.getStorageCapacity())) return false;
        if (!targetType.equals("condition") && selCondition != null && !selCondition.equals(op.getConditionPercent())) return false;
        if (!targetType.equals("battery") && selBattery != null && !selBattery.equals(op.getBatteryHealth())) return false;
        if (!targetType.equals("integrity") && selIntegrity != null && !selIntegrity.equals(op.getIsOriginal())) return false;
        if (!targetType.equals("warranty") && selWarranty != null && !selWarranty.equals(op.getWarrantyTime())) return false;
        
        return true;
    }

    private void findMatch(String color, String storage, String condition, String battery, String integrity, String warranty) {
        // Only match if all required fields are selected
        // Note: some products might not have all 6 attributes. 
        // We only care about matching what exists in the schema for that product.
        
        selectedOptionMatch = null;
        for (OptionProduct op : allOptions) {
            boolean mColor = (color == null && (op.getNameColor() == null || op.getNameColor().isEmpty())) || (color != null && color.equals(op.getNameColor()));
            boolean mStorage = (storage == null && (op.getStorageCapacity() == null || op.getStorageCapacity().isEmpty())) || (storage != null && storage.equals(op.getStorageCapacity()));
            boolean mCondition = (condition == null && (op.getConditionPercent() == null || op.getConditionPercent().isEmpty())) || (condition != null && condition.equals(op.getConditionPercent()));
            boolean mBattery = (battery == null && (op.getBatteryHealth() == null || op.getBatteryHealth().isEmpty())) || (battery != null && battery.equals(op.getBatteryHealth()));
            boolean mIntegrity = (integrity == null && (op.getIsOriginal() == null || op.getIsOriginal().isEmpty())) || (integrity != null && integrity.equals(op.getIsOriginal()));
            boolean mWarranty = (warranty == null && (op.getWarrantyTime() == null || op.getWarrantyTime().isEmpty())) || (warranty != null && warranty.equals(op.getWarrantyTime()));

            if (mColor && mStorage && mCondition && mBattery && mIntegrity && mWarranty) {
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
            binding.tvPrice.setText("Vui lòng chọn cấu hình");
            binding.tvStock.setText("Kho: --");
            binding.btnConfirm.setEnabled(false);
            binding.btnConfirm.setText("Xác nhận");
            
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
