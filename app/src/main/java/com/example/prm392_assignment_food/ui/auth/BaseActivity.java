package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.utils.TokenManager;

public class BaseActivity extends AppCompatActivity {

    private static final long SESSION_TIMEOUT = 15 * 60 * 1000; // 15 minutes in milliseconds
    private static final String PREF_NAME = "FoodAppPrefs";
    private static final String KEY_LOGIN_TIMESTAMP = "KEY_LOGIN_TIMESTAMP";

    private SharedPreferences sharedPreferences;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        tokenManager = new TokenManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSession();
    }

    private void checkSession() {
        long loginTimestamp = sharedPreferences.getLong(KEY_LOGIN_TIMESTAMP, 0);
        if (loginTimestamp == 0) {
            // Not logged in, or timestamp not set
            return;
        }

        long currentTime = System.currentTimeMillis();
        if ((currentTime - loginTimestamp) > SESSION_TIMEOUT) {
            // Session has expired
            logoutUser();
        }
    }

    private void logoutUser() {
        tokenManager.clear();
        
        // Clear the timestamp
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGIN_TIMESTAMP);
        editor.apply();

        Toast.makeText(this, "Phiên đăng nhập đã hết hạn.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
