package com.example.doan_shopsmartphone.api;

import com.example.doan_shopsmartphone.api.response.BannerReponse;
import com.example.doan_shopsmartphone.api.response.ProductResponse;
import com.example.doan_shopsmartphone.api.response.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.FormUrlEncoded;


public interface BaseApi {
    Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    String LOCALHOT = "192.168.100.116";
    BaseApi API = new Retrofit.Builder()
            .baseUrl("http://192.168.100.116:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BaseApi.class);

    @GET("banner/get-list")
    Call<BannerReponse> getListBanner();

    @GET("products/all-product")
    Call<ProductResponse> getListAllProduct();

    @FormUrlEncoded
    @POST("register")
    Call<ServerResponse> register(@Field("email") String email,
                                  @Field("password") String password);

    @GET("verify/{idCode}")
    Call<ServerResponse> verify(@Path("idCode") String idCode);
}
