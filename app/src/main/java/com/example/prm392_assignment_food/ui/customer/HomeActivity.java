package com.example.prm392_assignment_food.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.MainActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.repository.FoodRepository;
import com.example.prm392_assignment_food.ui.cart.CartActivity;
import com.example.prm392_assignment_food.ui.location.AccessLocationActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeActivity - Trang chủ giống GrabFood
 * Hiển thị danh sách món ăn với search và filter theo category
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int PAGE_SIZE = 10;

    // UI Components
    private Toolbar toolbar;
    private RecyclerView recyclerFoodList;
    private FoodAdapter foodAdapter;
    private TextView tvTotalItems;
    private TextView tabAll, tabBreakfast, tabLunch, tabDinner;
    private ImageView btnCart;
    private FloatingActionButton fabMap;
    private ProgressBar progressBar;
    private Button btnGoToMain;

    // Data
    private List<MenuItemResponse> menuItems;
    private FoodRepository repository;
    private int currentPage = 0;
    private String currentCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // QUAN TRỌNG: Init ApiClient để attach token
        com.example.prm392_assignment_food.data.network.ApiClient.init(this);

        // DEBUG: Check token có tồn tại không
        com.example.prm392_assignment_food.utils.TokenManager tokenManager =
                new com.example.prm392_assignment_food.utils.TokenManager(this);
        String token = tokenManager.getToken();
        Log.d(TAG, "Token exists: " + (token != null && !token.isEmpty()));
        if (token != null) {
            Log.d(TAG, "Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");
        } else {
            Log.e(TAG, "NO TOKEN FOUND - User needs to login again!");
        }

        initViews();
        initData();
        setupToolbar();
        setupRecyclerView();
        setupTabListeners();
        setupClickListeners();

        // Load data từ API
        loadMenuItems();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerFoodList = findViewById(R.id.recycler_food_list);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tabAll = findViewById(R.id.tab_all);
        tabBreakfast = findViewById(R.id.tab_breakfast);
        tabLunch = findViewById(R.id.tab_lunch);
        tabDinner = findViewById(R.id.tab_dinner);
        btnCart = findViewById(R.id.btn_cart);
        fabMap = findViewById(R.id.fab_map);
        progressBar = findViewById(R.id.progress_bar);
        btnGoToMain = findViewById(R.id.btnGoToMain);
    }

    private void initData() {
        menuItems = new ArrayList<>();
        repository = new FoodRepository();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Yummy Go");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setupRecyclerView() {
        recyclerFoodList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(tabAll, null));
        tabBreakfast.setOnClickListener(v -> selectTab(tabBreakfast, "Breakfast"));
        tabLunch.setOnClickListener(v -> selectTab(tabLunch, "Lunch"));
        tabDinner.setOnClickListener(v -> selectTab(tabDinner, "Dinner"));
    }

    private void setupClickListeners() {
        // Cart button
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Map FAB
        fabMap.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AccessLocationActivity.class);
            startActivity(intent);
        });
        
        btnGoToMain.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void selectTab(TextView selectedTab, String category) {
        resetAllTabs();

        selectedTab.setBackgroundResource(R.drawable.tab_selected_background);
        selectedTab.setTextColor(getResources().getColor(R.color.white));

        // TODO: Map category name → ID (cần load categories trước)
        currentCategoryId = null; // Tạm thời set null
        currentPage = 0;
        loadMenuItems();
    }

    private void resetAllTabs() {
        TextView[] tabs = {tabAll, tabBreakfast, tabLunch, tabDinner};
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.tab_unselected_background);
            tab.setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }

    /**
     * Load menu items từ API
     */
    private void loadMenuItems() {
        showLoading();

        repository.getMenuItems(currentPage, PAGE_SIZE, "name", null, currentCategoryId,
                new FoodRepository.RepositoryCallback<PageResponse<MenuItemResponse>>() {
                    @Override
                    public void onSuccess(PageResponse<MenuItemResponse> data) {
                        Log.d(TAG, "Success: Loaded " + data.getContent().size() + " items");

                        menuItems.clear();
                        menuItems.addAll(data.getContent());

                        runOnUiThread(() -> {
                            hideLoading();
                            updateUI();
                            updateTotalItems((int) data.getTotalElements().longValue());
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error: " + errorMessage);

                        runOnUiThread(() -> {
                            hideLoading();
                            Toast.makeText(HomeActivity.this,
                                    "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerFoodList.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            recyclerFoodList.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI() {
        // Convert MenuItemResponse → Food để dùng adapter hiện tại
        List<Food> foodList = new ArrayList<>();
        for (MenuItemResponse item : menuItems) {
            Food food = new Food(
                    item.getId(),
                    item.getName() != null ? item.getName() : "Unknown",
                    item.getFormattedPrice(),
                    item.getCategoryName() != null ? item.getCategoryName() : "",
                    R.drawable.chicken_thai_biriyani,
                    "Location",
                    item.getDescription() != null ? item.getDescription() : ""
            );
            foodList.add(food);
        }

        foodAdapter = new FoodAdapter(this, foodList);
        recyclerFoodList.setAdapter(foodAdapter);
    }

    private void updateTotalItems(int count) {
        String totalText = String.format("Tổng %d món", count);
        tvTotalItems.setText(totalText);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle menu icon click if needed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
