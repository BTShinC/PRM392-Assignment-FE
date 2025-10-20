package com.example.prm392_assignment_food.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Manager class để quản lý JWT token và user session
 * Sử dụng SharedPreferences để persist data
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    

    private static final String PREF_NAME = "FoodAppPrefs";

    private static final String KEY_TOKEN  = "AUTH_TOKEN";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Token retrieved: " + (token != null ? "exists" : "null"));
        return token;
    }

}


