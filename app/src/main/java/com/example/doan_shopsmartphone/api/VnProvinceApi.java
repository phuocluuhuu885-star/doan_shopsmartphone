package com.example.doan_shopsmartphone.api;

import com.example.doan_shopsmartphone.api.DTO.ProvinceDTO;
import com.example.doan_shopsmartphone.api.mapper.DistrictRawResponse;
import com.example.doan_shopsmartphone.api.mapper.ProvinceRawResponse;
import com.example.doan_shopsmartphone.api.mapper.WardRawResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VnProvinceApi {

    Gson gson = new GsonBuilder().create();

    VnProvinceApi API = new Retrofit.Builder()
            .baseUrl("https://provinces.open-api.vn/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(VnProvinceApi.class);

    // Lấy danh sách tỉnh
    @GET("p/")
    Call<List<ProvinceDTO>> getProvinces();

    // lấy huyện theo tỉnh
    @GET("p/{code}?depth=2")
    Call<ProvinceRawResponse> getDistricts(
            @Path("code") int provinceCode
    );

    // lấy xã theo huyện
    @GET("d/{code}?depth=2")
    Call<DistrictRawResponse> getWards(
            @Path("code") int districtCode
    );

}
