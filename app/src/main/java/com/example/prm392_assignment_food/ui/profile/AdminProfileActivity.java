package com.example.prm392_assignment_food.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.ui.auth.LoginActivity;

public class AdminProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Setup Menu Items
        setupMenuItem(R.id.item_personal_info, R.drawable.ic_personal_info, "Personal Info", null);
        setupMenuItem(R.id.item_settings, R.drawable.ic_settings, "Settings", null);
        setupMenuItem(R.id.item_withdrawal_history, R.drawable.ic_withdrawal_history, "Withdrawal History", null);
        setupMenuItem(R.id.item_number_of_orders, R.drawable.ic_number_of_orders, "Number of Orders", "29K");
        setupMenuItem(R.id.item_user_reviews, R.drawable.ic_user_reviews, "User Reviews", null);

        LinearLayout logoutItem = findViewById(R.id.item_logout);
        setupMenuItem(logoutItem, R.drawable.ic_logout, "Log Out", null);

        logoutItem.setOnClickListener(v -> logout());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupMenuItem(int viewId, int iconResId, String title, String value) {
        View menuItem = findViewById(viewId);
        setupMenuItem(menuItem, iconResId, title, value);
    }

    private void setupMenuItem(View menuItem, int iconResId, String title, String value) {
        ImageView icon = menuItem.findViewById(R.id.iv_icon);
        TextView tvTitle = menuItem.findViewById(R.id.tv_title);
        TextView tvValue = menuItem.findViewById(R.id.tv_value);
        ImageView arrow = menuItem.findViewById(R.id.iv_arrow);

        icon.setImageResource(iconResId);
        tvTitle.setText(title);

        if (value != null) {
            tvValue.setText(value);
            tvValue.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.GONE);
        } else {
            tvValue.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
        }

        // Since all new icons have their own colors, remove any tint to display them correctly.
        icon.setImageTintList(null);

        if (title.equals("Log Out")) {
            // Special case for Log Out: set text color to red.
            tvTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("FoodAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("AUTH_TOKEN");
        editor.remove("USER_EMAIL");
        editor.remove("LOGIN_TIMESTAMP");
        editor.apply();

        Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
