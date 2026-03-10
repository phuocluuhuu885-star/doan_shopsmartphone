package com.example.doan_shopsmartphone.api;

import com.example.doan_shopsmartphone.model.OrderResponse;
import com.example.doan_shopsmartphone.model.body.PurchaseBody;
import com.example.doan_shopsmartphone.model.body.YeuthichRequestBody;
import com.example.doan_shopsmartphone.model.response.BannerReponse;
import com.example.doan_shopsmartphone.model.response.CartReponse;
import com.example.doan_shopsmartphone.model.response.DetailProductResponse;
import com.example.doan_shopsmartphone.model.response.DetailUserReponse;
import com.example.doan_shopsmartphone.model.response.InfoResponse;
import com.example.doan_shopsmartphone.model.response.ListCommentResponse;
import com.example.doan_shopsmartphone.model.response.ProductByCategoryReponse;
import com.example.doan_shopsmartphone.model.response.ProductResponse;
import com.example.doan_shopsmartphone.model.response.ServerResponse;
import com.example.doan_shopsmartphone.model.response.BannerReponse;
import com.example.doan_shopsmartphone.api.response.LoginResponse;
import com.example.doan_shopsmartphone.model.response.ProductResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface BaseApi {
    Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    String LOCALHOT = "192.168.1.21";
    BaseApi API = new Retrofit.Builder()
            .baseUrl("http://192.168.1.21:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BaseApi.class);

    @GET("banner/get-list")
    Call<BannerReponse> getListBanner();

    @GET("products/all-product")
    Call<ProductResponse> getListAllProduct();

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("user/detail-profile/{idUser}")
    Call<DetailUserReponse> detailProfile(@Header("Authorization") String authorization,
                                          @Path("idUser") String idUser);

    @GET("cart/all-cart-user")
    Call<CartReponse> allCartUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("login-with-google")
    Call<LoginResponse> loginGoogle(@Field("idToken") String tokenGG);

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

    @FormUrlEncoded
    @POST("register")
    Call<ServerResponse> register(@Field("email") String email,
                                  @Field("password") String password);
    @GET("logout")
    Call<ServerResponse> logout(@Header("Authorization") String authorization);
    @GET("verify/{idCode}")
    Call<ServerResponse> verify(@Path("idCode") String idCode);

    @Multipart
    @POST("user/upload-avatar/{id}") // Thay đường dẫn đúng với Server của bạn
    Call<ServerResponse> uploadAvatar(
            @Header("Authorization") String token,
            @Path("id") String idUser,
            @Part MultipartBody.Part avatar);

    @FormUrlEncoded
    @PUT("user/edit-profile/{id}") // Thay đường dẫn đúng với Server của bạn
    Call<ServerResponse> editProfile(
            @Header("Authorization") String token,
            @Path("id") String idUser,
            @Field("username") String username,
            @Field("birthday") String birthday
    );

    @GET("products/detail-product/{idProduct}")
    Call<DetailProductResponse> getDetailProduct(@Path("idProduct") String idProduct);

    @GET("products/similar-product/{idProduct}")
    Call<ProductResponse> getDataSimilarlProduct(@Path("idProduct") String idProduct);

    @GET("review/{productId}")
    Call<ListCommentResponse> getListComment(
            @Header("Authorization") String authorization,
            @Path("productId") String productId);

    @GET("/api/yeuthich/checktheoiduser/{user_id}")
    Call<List<YeuthichRequestBody>> getFavorites(@Path("user_id") String userId);

    @POST("/api/yeuthich/themyeuthich")
    Call<Void> addFavorite(@Body YeuthichRequestBody requestBody);

    @POST("/api/yeuthich/deletefavorite")
    Call<Void> removeFavorite(@Body YeuthichRequestBody yeuthich);

    @FormUrlEncoded
    @POST("cart/create-cart-item")
    Call<ServerResponse> createCartItem(
            @Header("Authorization") String authorization,
            @Field("option_id") String optionId,
            @Field("quantity") int quantity);
    @GET("products/all-product-by-category")
    Call<ProductByCategoryReponse> getListProductByCategory(@Query("token") String token);
    @GET("products/all-product")
    Call<ProductResponse> getListAllProducts(@Query("isActive") boolean isActive, @Query("token") String token);
}
