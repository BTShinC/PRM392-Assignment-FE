package com.example.prm392_assignment_food;

import android.app.Application;

import com.example.prm392_assignment_food.data.network.ApiClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize ApiClient with application context
        ApiClient.init(this);
    }
}
