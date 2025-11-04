package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryRequest;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.ui.auth.BaseActivity;
import com.example.prm392_assignment_food.ui.profile.AdminProfileActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends BaseActivity { // Kế thừa từ BaseActivity

    private ImageView ivDashboard;
    private ImageView ivList;
    private ImageView ivProfile;

    private final Fragment dashboardFragment = new AdminDashboardFragment();
    private final Fragment foodListFragment = new MyFoodListFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = dashboardFragment;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Gọi super.onCreate()
        setContentView(R.layout.activity_admin);

        apiService = ApiClient.getApiService();

        ivDashboard = findViewById(R.id.iv_dashboard);
        ivList = findViewById(R.id.iv_list);
        ivProfile = findViewById(R.id.iv_profile);

        setupBottomNavigation();

        fm.beginTransaction().add(R.id.fragment_container, foodListFragment, "2").hide(foodListFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, dashboardFragment, "1").commit();

        updateIconColors();
    }

    @Override
    protected void onResume() {
        super.onResume(); // Gọi super.onResume()
        updateIconColors();
    }

    private void setupBottomNavigation() {
        FrameLayout dashboardContainer = findViewById(R.id.dashboard_container);
        FrameLayout listContainer = findViewById(R.id.list_container);
        FrameLayout fabAddContainer = findViewById(R.id.fab_add_container);
        FrameLayout profileContainer = findViewById(R.id.profile_container);

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

        fabAddContainer.setOnClickListener(v -> showAddOptionsDialog());

        profileContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showAddOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_options, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        LinearLayout optionManageItems = dialogView.findViewById(R.id.option_manage_items);
        LinearLayout optionManageCategories = dialogView.findViewById(R.id.option_manage_categories);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_dialog);

        optionManageItems.setOnClickListener(v -> {
             Intent intent = new Intent(this, ManageItemsActivity.class);
             startActivity(intent);
            dialog.dismiss();
        });

        optionManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategoriesActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        final EditText etCategoryName = dialogView.findViewById(R.id.et_dialog_category_name);
        final EditText etCategoryDescription = dialogView.findViewById(R.id.et_dialog_category_description);

        builder.setTitle("Add New Category");
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etCategoryName.getText().toString().trim();
            String description = etCategoryDescription.getText().toString().trim();
            if (!name.isEmpty()) {
                addCategory(name, description);
            } else {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addCategory(String name, String description) {
        MenuCategoryRequest request = new MenuCategoryRequest(name, description);
        apiService.addMenuCategory(request).enqueue(new Callback<MenuCategoryResponse>() {
            @Override

            public void onResponse(Call<MenuCategoryResponse> call, Response<MenuCategoryResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MenuCategoryResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateIconColors() {
        int defaultColor = ContextCompat.getColor(this, R.color.medium_gray);
        ivDashboard.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        ivList.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        ivProfile.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);

        int activeColor = ContextCompat.getColor(this, R.color.deep_orange);
        if (active == dashboardFragment) {
            ivDashboard.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        } else if (active == foodListFragment) {
            ivList.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        }
    }
}
