package com.example.prm392_assignment_food.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.ui.auth.ProfileFragment;
import com.example.prm392_assignment_food.ui.chat.InboxActivity;
import com.example.prm392_assignment_food.ui.location.LocationFragment;
import com.example.prm392_assignment_food.ui.order.OrderFragment;

public class CustomerMainActivity extends AppCompatActivity {

    // Navigation Components
    private View dashboardContainer, listContainer, homeContainer, notificationContainer, profileContainer;
    private ImageView ivDashboard, ivList, ivHome, ivNotification, ivProfile;
    private View lastSelectedNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        initNavViews();
        setupNavClickListeners();

        // Check for navigation intent first
        if (getIntent().hasExtra("NAVIGATE_TO")) {
            String destination = getIntent().getStringExtra("NAVIGATE_TO");
            if ("ORDER_FRAGMENT".equals(destination)) {
                loadFragment(new OrderFragment());
                lastSelectedNavView = dashboardContainer; // dashboardContainer corresponds to OrderFragment
                updateIconTints(dashboardContainer);
            } else {
                loadHomeFragmentByDefault(); // fallback to home
            }
        } else if (savedInstanceState == null) {
            loadHomeFragmentByDefault();
        }
    }

    private void loadHomeFragmentByDefault() {
        loadFragment(new HomeFragment());
        lastSelectedNavView = homeContainer;
        updateIconTints(homeContainer);
    }

    private void initNavViews() {
        dashboardContainer = findViewById(R.id.dashboard_container);
        listContainer = findViewById(R.id.list_container);
        homeContainer = findViewById(R.id.fab_add_container);
        notificationContainer = findViewById(R.id.notification_container);
        profileContainer = findViewById(R.id.profile_container);
        ivDashboard = findViewById(R.id.iv_dashboard);
        ivList = findViewById(R.id.iv_list);
        ivHome = findViewById(R.id.iv_home);
        ivNotification = findViewById(R.id.iv_notification);
        ivProfile = findViewById(R.id.iv_profile);
    }

    private void setupNavClickListeners() {
        dashboardContainer.setOnClickListener(v -> handleNavigation(v, new OrderFragment()));
        listContainer.setOnClickListener(v -> handleNavigation(v, new LocationFragment()));
        homeContainer.setOnClickListener(v -> handleNavigation(v, new HomeFragment()));

        notificationContainer.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            Intent intent = new Intent(CustomerMainActivity.this, InboxActivity.class);
            startActivity(intent);
        });

        profileContainer.setOnClickListener(v -> handleNavigation(v, new ProfileFragment()));
    }

    private void handleNavigation(View targetView, Fragment fragment) {
        if (lastSelectedNavView != null && lastSelectedNavView.getId() == targetView.getId()) {
            return; // Do nothing if the user taps on the current tab
        }

        targetView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        lastSelectedNavView = targetView;
        updateIconTints(targetView);
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void updateIconTints(View selectedView) {
        // Reset all icons to default color
        ivDashboard.setColorFilter(getColor(R.color.dark_gray));
        ivList.setColorFilter(getColor(R.color.dark_gray));
        ivNotification.setColorFilter(getColor(R.color.dark_gray));
        ivProfile.setColorFilter(getColor(R.color.dark_gray));

        // Always keep home icon orange
        ivHome.setColorFilter(getColor(R.color.orange));

        // Set the selected icon color to orange
        ImageView selectedIcon = null;
        int viewId = selectedView.getId();

        if (viewId == R.id.dashboard_container) selectedIcon = ivDashboard;
        else if (viewId == R.id.list_container) selectedIcon = ivList;
        else if (viewId == R.id.notification_container) selectedIcon = ivNotification;
        else if (viewId == R.id.profile_container) selectedIcon = ivProfile;

        if (selectedIcon != null) {
            selectedIcon.setColorFilter(getColor(R.color.orange));
        }
    }
}
