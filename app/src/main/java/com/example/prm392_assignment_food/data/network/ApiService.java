package com.example.prm392_assignment_food.data.network;

import com.example.prm392_assignment_food.data.model.CartItemRequest;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.model.ForgotPasswordRequest;
import com.example.prm392_assignment_food.data.model.ForgotPasswordResponse;
import com.example.prm392_assignment_food.data.model.LoginRequest;
import com.example.prm392_assignment_food.data.model.LoginResponse;
import com.example.prm392_assignment_food.data.model.RegisterRequest;
import com.example.prm392_assignment_food.data.model.RegisterResponse;
import com.example.prm392_assignment_food.data.model.ResetPasswordRequest;
import com.example.prm392_assignment_food.data.model.ResetPasswordResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("api/carts/{userId}/items")
    Call<CartResponse> addItem(
            @Path("userId") String userId,
            @Body CartItemRequest cartItemRequest
    );

    @GET("api/carts/{userId}")
    Call<CartResponse> getCart(@Path("userId") String userId);


}
