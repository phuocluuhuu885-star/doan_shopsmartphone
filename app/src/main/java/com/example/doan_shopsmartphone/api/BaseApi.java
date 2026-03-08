package com.example.doan_shopsmartphone.api;

import com.example.doan_shopsmartphone.api.response.BannerReponse;
import com.example.doan_shopsmartphone.api.response.DetailUserReponse;
import com.example.doan_shopsmartphone.api.response.ProductResponse;
import com.example.doan_shopsmartphone.model.OrderResponse;
import com.example.doan_shopsmartphone.model.body.PurchaseBody;
import com.example.doan_shopsmartphone.model.response.CartReponse;
import com.example.doan_shopsmartphone.model.response.InfoResponse;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApi {
    Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    String LOCALHOT = "192.168.0.100";
    BaseApi API = new Retrofit.Builder()
            .baseUrl("http://192.168.0.100:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BaseApi.class);

    @GET("banner/get-list")
    Call<BannerReponse> getListBanner();

    @GET("products/all-product")
    Call<ProductResponse> getListAllProduct();
    @GET("user/detail-profile/{idUser}")
    Call<DetailUserReponse> detailProfile(@Header("Authorization") String authorization,
                                          @Path("idUser") String idUser);
    @GET("cart/all-cart-user")
    Call<CartReponse> allCartUser(@Header("Authorization") String authorization);
    @DELETE("cart/delete-cart-item/{idCart}")
    Call<ServerResponse> deleteCartItem(@Header("Authorization") String authorization,
                                        @Path("idCart") String idCart);
    @FormUrlEncoded
    @PUT("cart/update-quantity/{idCart}")
    Call<ServerResponse> updateQuantityCartItem(@Header("Authorization") String authorization,
                                                @Path("idCart") String idCart,
                                                @Field("quantity") int quantity);
    @GET("info")
    Call<InfoResponse> getInfo(@Header("Authorization") String authorization);

    @POST("order/create-order")
    Call<ServerResponse> createOrder(@Header("Authorization") String authorization,
                                     @Body PurchaseBody purchaseBody);
    @POST("order/create-order-by-zalo")
    Call<ServerResponse> createOrderByZalo(@Header("Authorization") String authorization,
                                           @Body PurchaseBody purchaseBody);
    @GET("order")
    Call<OrderResponse> getListOrder(@Header("Authorization") String authorization,
                                     @Query("status") String status);
    @FormUrlEncoded
    @PUT("order/update-order-status/{idOrder}")
    Call<ServerResponse> updateOrderStatus(@Header("Authorization") String authorization,
                                           @Path("idOrder") String idOrder,
                                           @Field("status") String status);


}
