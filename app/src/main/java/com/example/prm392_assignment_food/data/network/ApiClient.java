package com.example.prm392_assignment_food.data.network;

import android.content.Context;
import android.util.Log;

import com.example.prm392_assignment_food.utils.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit API Client với JWT token authentication support
 */
public class ApiClient {

    private static final String TAG = "ApiClient";
    

    private static final String BASE_URL = "https://prm392.nguyenhoangan.site/";

    private static Retrofit retrofit = null;
    

    private static Context appContext = null;


        public static void init(Context context) {
            if (context != null) {
                appContext = context.getApplicationContext();
                Log.d(TAG, "ApiClient initialized with context");
                // QUAN TRỌNG: Reset client để apply context mới
                resetClient();
            }
        }

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating new Retrofit instance");
            
            // Create logging interceptor để debug requests/responses
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create OkHttpClient builder
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor);
            
            // Add authentication interceptor nếu có context
            if (appContext != null) {
                clientBuilder.addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    
                    // Get token từ TokenManager
                    TokenManager tokenManager = new TokenManager(appContext);
                    String token = tokenManager.getToken();
                    
                    // Build new request
                    Request.Builder requestBuilder = originalRequest.newBuilder();
                    
                        // Attach Bearer token nếu có
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
            
            OkHttpClient client = clientBuilder.build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                    
            Log.d(TAG, "Retrofit instance created successfully");
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
        Log.d(TAG, "Retrofit client reset");
    }
}

