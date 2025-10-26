package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.prm392_assignment_food.R;

public class AdminActivity extends AppCompatActivity {

    private ImageView ivDashboard;
    private ImageView ivList;
    // fabAdd ImageView is inside a FrameLayout, but we might not need a direct reference if it's just for display

    private final Fragment dashboardFragment = new AdminDashboardFragment();
    private final Fragment foodListFragment = new MyFoodListFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Still need references to ImageViews for color filtering
        ivDashboard = findViewById(R.id.iv_dashboard);
        ivList = findViewById(R.id.iv_list);

        setupBottomNavigation();

        // Initialize fragments
        fm.beginTransaction().add(R.id.fragment_container, foodListFragment, "2").hide(foodListFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, dashboardFragment, "1").commit();

        updateIconColors();
    }

    private void setupBottomNavigation() {
        // Get references to the containers for click listeners
        FrameLayout dashboardContainer = findViewById(R.id.dashboard_container);
        FrameLayout listContainer = findViewById(R.id.list_container);
        FrameLayout fabAddContainer = findViewById(R.id.fab_add_container);

        dashboardContainer.setOnClickListener(v -> {
            if (active != dashboardFragment) {
                fm.beginTransaction().hide(active).show(dashboardFragment).commit();
                active = dashboardFragment;
                updateIconColors();
            }
        });

        listContainer.setOnClickListener(v -> {
            if (active != foodListFragment) {
                fm.beginTransaction().hide(active).show(foodListFragment).commit();
                active = foodListFragment;
                updateIconColors();
            }
        });

        fabAddContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    private void updateIconColors() {
        int defaultColor = ContextCompat.getColor(this, R.color.medium_gray);
        ivDashboard.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        ivList.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);

        int activeColor = ContextCompat.getColor(this, R.color.orange);
        if (active == dashboardFragment) {
            ivDashboard.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        } else if (active == foodListFragment) {
            ivList.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        }
    }
}
