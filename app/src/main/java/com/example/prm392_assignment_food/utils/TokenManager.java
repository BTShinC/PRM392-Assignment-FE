package com.example.prm392_assignment_food.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Manager class để quản lý JWT token và user session
 * Sử dụng SharedPreferences để persist data
 */
public class TokenManager {
    // Tag cho logging
    private static final String TAG = "TokenManager";
    
    // SharedPreferences name
    private static final String PREF_NAME = "FoodAppPrefs";
    
    // Keys
    private static final String KEY_TOKEN  = "AUTH_TOKEN";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    
    // SharedPreferences instance
    private final SharedPreferences prefs;

    /**
     * Constructor - Khởi tạo TokenManager với context
     * @param context Application hoặc Activity context
     */
    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu access token vào SharedPreferences
     * @param token JWT token từ backend
     */
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
        Log.d(TAG, "Token saved successfully");
    }

    /**
     * Lấy access token từ SharedPreferences
     * @return JWT token hoặc null nếu chưa có
     */
    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Token retrieved: " + (token != null ? "exists" : "null"));
        return token;
    }

    /**
     * Xóa token (dùng khi logout)
     */
    public void clearToken() {
        prefs.edit().clear().apply();
        Log.d(TAG, "Token cleared");
    }

    /**
     * Kiểm tra có token không
     * @return true nếu có token, false nếu chưa login
     */
    public boolean hasToken() {
        return getToken() != null && !getToken().isEmpty();
    }

    /**
     * Lưu email của user
     * @param email User email
     */
    public void saveUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
        Log.d(TAG, "User email saved: " + email);
    }

    /**
     * Lấy email của user
     * @return User email hoặc null
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
}


