package com.example.prm392_assignment_food.data.network;

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
import retrofit2.http.POST;

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
}
