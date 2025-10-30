package com.example.prm392_assignment_food.data.network;


import com.example.prm392_assignment_food.data.model.MenuCategoryRequest;
import com.example.prm392_assignment_food.data.model.MenuItemRequest;
import com.example.prm392_assignment_food.data.model.auth.ForgotPasswordRequest;
import com.example.prm392_assignment_food.data.model.auth.ForgotPasswordResponse;
import com.example.prm392_assignment_food.data.model.auth.LoginRequest;
import com.example.prm392_assignment_food.data.model.auth.LoginResponse;
import com.example.prm392_assignment_food.data.model.auth.RegisterRequest;
import com.example.prm392_assignment_food.data.model.auth.RegisterResponse;
import com.example.prm392_assignment_food.data.model.auth.ResetPasswordRequest;
import com.example.prm392_assignment_food.data.model.auth.ResetPasswordResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import com.example.prm392_assignment_food.data.model.CartItemRequest;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.model.CreateOrderRequest;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.model.ResponseDto;
import com.example.prm392_assignment_food.data.model.UpdateQuantityRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/v1/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/users/v1/register")
    Call<RegisterResponse> requestRegistrationOtp(@Body RegisterRequest registerRequest);

    @POST("otp/verify-register")
    Call<RegisterResponse> verifyAndRegister(@Body RegisterRequest registerRequest);

    @POST("api/users/v1/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("otp/verify-reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);


    @GET("api/users/v1/check-admin-exists")
    Call<Boolean> checkAdminExists();

    @GET("api/menu-items")
    Call<PageResponse<MenuItemResponse>> getMenuItems(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sort") String sort,
            @Query("search") String search,
            @Query("categoryId") String categoryId
    );

    @GET("api/menu-items/{id}")
    Call<MenuItemResponse> getMenuItemById(@Path("id") String id);

    @GET("api/menu-categories")
    Call<PageResponse<MenuCategoryResponse>> getMenuCategories(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sort") String sort,
            @Query("search") String search
    );

    @GET("api/carts/{userId}")
    Call<CartResponse> getCart(@Path("userId") String userId);

    @PUT("/{userId}/items/{menuItemId}")
    Call<CartResponse> updateItem(@Path("userId") String userId, @Path("menuItemId") String menuItemId, @Body UpdateQuantityRequest request);

    @DELETE("api/carts/{userId}/items/{menuItemId}")
    Call<Void> removeCartItem(
            @Path("userId") String userId,
            @Path("menuItemId") String menuItemId
    );

    @POST("api/carts/{userId}/items")
    Call<CartResponse> addItem(@Path("userId") String userId, @Body CartItemRequest request);

    @POST("/api/orders")
    ResponseDto<Object> createOrder(@Body CreateOrderRequest request);

    @GET("/api/orders/users/{userId}")
    ResponseDto<Object> getOrders(@Query("userId") String userId);

    @Multipart
    @POST("/api/admins/menu-items")
    Call<MenuItemResponse> addMenuItem(
            @Part("request") RequestBody request,
            @Part MultipartBody.Part file
    );

    @POST("/api/admins/menu-categories")
    Call<MenuCategoryResponse> addMenuCategory(@Body MenuCategoryRequest menuCategoryRequest);

    @PUT("/api/admins/menu-categories/{id}")
    Call<MenuCategoryResponse> updateMenuCategory(@Path("id") String id, @Body MenuCategoryRequest menuCategoryRequest);

    @DELETE("/api/admins/menu-categories/{id}")
    Call<Void> deleteMenuCategory(@Path("id") String id);

    @Multipart
    @PUT("/api/admins/menu-items/{id}")
    Call<MenuItemResponse> updateMenuItem(
            @Path("id") String id,
            @Part("request") RequestBody request,
            @Part MultipartBody.Part file
    );

    @DELETE("/api/admins/menu-items/{id}")
    Call<Void> deleteMenuItem(@Path("id") String id);
}
