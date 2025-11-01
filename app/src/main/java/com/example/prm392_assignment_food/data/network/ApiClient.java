package com.example.prm392_assignment_food.data.network;

import android.content.Context;
import android.util.Log;

import com.example.prm392_assignment_food.utils.TokenManager;

import java.util.concurrent.TimeUnit; // <<< THÊM IMPORT NÀY

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://prm392.nguyenhoangan.site/";
//     private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static Context appContext = null;

    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
            Log.d(TAG, "ApiClient initialized with context");

            // QUAN TRỌNG: Reset client để apply context mới

            resetClient();
        }
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    // --- THÊM CẤU HÌNH TIMEOUT --- 
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    // -----------------------------
                    .addInterceptor(loggingInterceptor);

            if (appContext != null) {
                clientBuilder.addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    TokenManager tokenManager = new TokenManager(appContext);
                    String token = tokenManager.getToken();
                    Request.Builder requestBuilder = originalRequest.newBuilder();

                    if (token != null && !token.isEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                        Log.d(TAG, "Token attached to request: " + token.substring(0, Math.min(20, token.length())) + "...");
                    } else {
                        Log.w(TAG, "No token available - request will be unauthorized");
                    }

                    return chain.proceed(requestBuilder.build());
                });
            } else {
                Log.w(TAG, "ApiClient not initialized with context - token will not be attached");
            }
            okHttpClient = clientBuilder.build();
        }
        return okHttpClient;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating new Retrofit instance");
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Log.d(TAG, "Retrofit instance created successfully");
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public static void resetClient() {
        retrofit = null;
        okHttpClient = null;
        Log.d(TAG, "Retrofit client reset");
    }

