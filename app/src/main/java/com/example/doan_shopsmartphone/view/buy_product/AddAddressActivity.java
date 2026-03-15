package com.example.doan_shopsmartphone.view.buy_product;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.CityAdapter;
import com.example.doan_shopsmartphone.adapter.DistrictAdapter;
import com.example.doan_shopsmartphone.adapter.WardAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.api.DTO.DistrictDTO;
import com.example.doan_shopsmartphone.api.DTO.ProvinceDTO;
import com.example.doan_shopsmartphone.api.DTO.WardDTO;
import com.example.doan_shopsmartphone.api.VnProvinceApi;
import com.example.doan_shopsmartphone.api.mapper.DistrictRawResponse;
import com.example.doan_shopsmartphone.api.mapper.ProvinceRawResponse;
import com.example.doan_shopsmartphone.databinding.ActivityAddAddressBinding;
import com.example.doan_shopsmartphone.model.City;
import com.example.doan_shopsmartphone.model.District;
import com.example.doan_shopsmartphone.model.Ward;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.ProgressLoadingDialog;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {
    private ActivityAddAddressBinding binding;

    private ProgressLoadingDialog loadingDialog;
    private Boolean checked = false;
    private List<City> cityList;
    private List<District> districtList;
    private List<Ward> wardList;
    private CityAdapter cityAdapter;
    private DistrictAdapter districtAdapter;
    private WardAdapter wardAdapter;
    private String strCity = "";
    private String strDistrict = "";
    private String strWard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initController();
        initView();
        getListCity();
    }

    // -------------------------- spinner -----------------------------------

    private void getListCity() {

        binding.spnCity.setEnabled(false);

        VnProvinceApi.API.getProvinces().enqueue(new Callback<List<ProvinceDTO>>() {

            @Override
            public void onResponse(Call<List<ProvinceDTO>> call,
                                   Response<List<ProvinceDTO>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "size: " + response.body().size());
                    cityList.clear();
                    cityList.add(new City("--Chọn Tỉnh/Thành phố--"));

                    for (ProvinceDTO p : response.body()) {
                        cityList.add(new City(p.getCode(), p.getName()));
                    }

                    cityAdapter.notifyDataSetChanged();
                    binding.spnCity.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<ProvinceDTO>> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this,"Lỗi tải tỉnh",Toast.LENGTH_SHORT).show();
                binding.spnCity.setEnabled(true);
            }
        });
    }


    private void spinnerCityListener() {

        binding.spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                City city = cityAdapter.getItem(position);
                if (city == null) return;

                if (city.getProvinceId() != 0) {

                    strCity = city.getProvinceName();

                    resetDistrictAndWard();

                    getListDistrict(city.getProvinceId());

                } else {

                    strCity = "";
                    resetDistrictAndWard();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void resetDistrictAndWard() {
        districtList.clear();
        districtList.add(new District("--Chọn Quận/Huyện--"));
        districtAdapter.notifyDataSetChanged();

        wardList.clear();
        wardList.add(new Ward("--Chọn Phường/Xã--"));
        wardAdapter.notifyDataSetChanged();
    }

    private void getListDistrict(int provinceCode) {

        binding.spnDistrict.setEnabled(false);
        binding.spnWard.setEnabled(false);

        VnProvinceApi.API.getDistricts(provinceCode)
                .enqueue(new Callback<ProvinceRawResponse>() {

                    @Override
                    public void onResponse(Call<ProvinceRawResponse> call,
                                           Response<ProvinceRawResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            districtList.clear();
                            districtList.add(new District("--Chọn Quận/Huyện--"));

                            for (DistrictDTO d : response.body().getDistricts()) {
                                districtList.add(new District(d.getCode(), d.getName()));
                            }

                            districtAdapter.notifyDataSetChanged();
                            binding.spnDistrict.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProvinceRawResponse> call, Throwable t) {

                        Toast.makeText(AddAddressActivity.this,"Lỗi tải quận huyện",Toast.LENGTH_SHORT).show();
                        binding.spnDistrict.setEnabled(true);
                    }
                });
    }


    private void spinnerDistrictListener() {

        binding.spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                District district = districtAdapter.getItem(position);
                if (district == null) return;

                if (district.getDistrictId() != 0) {

                    strDistrict = district.getDistrictName();

                    wardList.clear();
                    wardList.add(new Ward("--Chọn Phường/Xã--"));
                    wardAdapter.notifyDataSetChanged();

                    getListWard(district.getDistrictId());

                } else {

                    strDistrict = "";

                    wardList.clear();
                    wardList.add(new Ward("--Chọn Phường/Xã--"));
                    wardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void getListWard(int districtCode) {

        binding.spnWard.setEnabled(false);

        VnProvinceApi.API.getWards(districtCode)
                .enqueue(new Callback<DistrictRawResponse>() {

                    @Override
                    public void onResponse(Call<DistrictRawResponse> call,
                                           Response<DistrictRawResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            wardList.clear();
                            wardList.add(new Ward("--Chọn Phường/Xã--"));

                            for (WardDTO w : response.body().getWards()) {
                                wardList.add(new Ward(w.getCode(), w.getName()));
                            }

                            wardAdapter.notifyDataSetChanged();
                            binding.spnWard.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<DistrictRawResponse> call, Throwable t) {

                        Toast.makeText(AddAddressActivity.this,"Lỗi tải phường xã",Toast.LENGTH_SHORT).show();
                        binding.spnWard.setEnabled(true);
                    }
                });
    }

    private void spinnerWardListener() {

        binding.spnWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Ward ward = wardAdapter.getItem(position);
                if (ward == null) return;

                if (ward.getWardId() != 0) {

                    strWard = ward.getWardName();

                    binding.edtAddress.setText(
                            strWard + ", " + strDistrict + ", " + strCity
                    );

                } else {

                    strWard = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void initController() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = binding.edtAddress.getText().toString().trim();
                String phoneNumber = binding.edtPhoneNumber.getText().toString().trim();
                String name = binding.edtUsername.getText().toString().trim();
                if (validateInfo(address, phoneNumber, name)) {
                    urlAddInfo(address, phoneNumber, name);
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

    }

    private void urlAddInfo(String address, String phoneNumber, String name) {
//        String token = AccountUltil.BEARER + AccountUltil.TOKEN;
        String token = AccountUltil.BEARER + AccountUltil.getToken(AddAddressActivity.this);

        Log.d("checkToken", "urlAddInfo: token ");
        loadingDialog.show();
        checked = binding.chkChooseDefault.isChecked();
        BaseApi.API.addInfo(token, name, address, phoneNumber, checked).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) { // chỉ nhận đầu status 200
                    ServerResponse serverResponse = response.body();
                    Log.d(TAG.toString, "onResponse-addInfo: " + serverResponse.toString());
                    if (serverResponse.getCode() == 200 || serverResponse.getCode() == 201) {
                        Intent intent = new Intent(AddAddressActivity.this, AddAddressActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }
                } else { // nhận các đầu status #200
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.getString("message");
                        Log.d(TAG.toString, "onResponse-addInfo: " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG.toString, "onFailure-addInfo: " + t.toString());
                loadingDialog.dismiss();
            }
        });
    }

    private boolean validateInfo(String address, String phoneNumber, String name) {
        if (strWard.length() == 0) {
            Toast.makeText(this, "Nhập dữ liệu địa chỉ đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Nhập dữ liệu số điện thoại đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nhập dữ liệu họ và tên đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initView() {
        loadingDialog = new ProgressLoadingDialog(this);


        cityList = new ArrayList<>();
        cityList.add(new City("--Chọn Tỉnh/Thành phố--"));
        cityAdapter = new CityAdapter(this, R.layout.layout_item_spinner_selected, cityList);
        cityAdapter.setDropDownViewResource(
                R.layout.layout_item_spinner_dropdown
        );
        binding.spnCity.setAdapter(cityAdapter);

        districtList = new ArrayList<>();
        districtList.add(new District("--Chọn Quận/Huyện--"));
        districtAdapter = new DistrictAdapter(this, R.layout.layout_item_spinner_selected, districtList);
        binding.spnDistrict.setAdapter(districtAdapter);

        wardList = new ArrayList<>();
        wardList.add(new Ward("--Chọn Phường/Xã--"));
        wardAdapter = new WardAdapter(this, R.layout.layout_item_spinner_selected, wardList);
        binding.spnWard.setAdapter(wardAdapter);

        spinnerCityListener();
        spinnerDistrictListener();
        spinnerWardListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
