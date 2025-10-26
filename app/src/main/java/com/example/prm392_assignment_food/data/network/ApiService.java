package com.example.prm392_assignment_food.data.network;

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
