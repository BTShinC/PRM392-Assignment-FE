package com.example.prm392_assignment_food.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Manager class to manage JWT token and user session
 * Uses SharedPreferences to persist data
 */
public class TokenManager {
    private static final String TAG = "TokenManager";


    private static final String PREF_NAME = "FoodAppPrefs";

    private static final String KEY_TOKEN = "AUTH_TOKEN";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_LOGIN_TIMESTAMP = "LOGIN_TIMESTAMP";


    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;


    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    public void saveToken(String token, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
        Log.d(TAG, "Token and user email saved");
        // Log the full token for debugging purposes
        Log.d(TAG, "FULL TOKEN: " + token);
    }

    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Token retrieved: " + (token != null ? "exists" : "null"));
        return token;
    }

    public long getLoginTimestamp() {
        return prefs.getLong(KEY_LOGIN_TIMESTAMP, 0);
    }

    public boolean isTokenExpired() {
        long loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();
        long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes for expiration
        return (currentTime - loginTimestamp) > fiveMinutesInMillis;
    }

    public void clear() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_LOGIN_TIMESTAMP);
        editor.apply();
        Log.d(TAG, "TokenManager cleared");
    }
}
