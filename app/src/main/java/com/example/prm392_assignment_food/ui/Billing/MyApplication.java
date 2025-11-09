package com.example.prm392_assignment_food.ui.Billing;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Gọi hàm tạo kênh thông báo khi ứng dụng được khởi chạy
        NotificationHelper.createNotificationChannel(this);
    }
}

