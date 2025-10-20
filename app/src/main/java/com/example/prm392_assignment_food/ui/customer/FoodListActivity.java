package com.example.prm392_assignment_food.ui.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.repository.FoodRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity hiển thị danh sách food với API thật
 */
public class FoodListActivity extends AppCompatActivity {
    private static final String TAG = "FoodListActivity";
    private static final int PAGE_SIZE = 10;
    
    // UI
    private RecyclerView recyclerFoodList;
    private FoodAdapter foodAdapter;
    private TextView tvTotalItems;
    private TextView tabAll, tabBreakfast, tabLunch, tabDinner;
    private ImageView btnBack;
    private ProgressBar progressBar;
    
    // Data
    private List<MenuItemResponse> menuItems;
    private FoodRepository repository;
    private int currentPage = 0;
    private String currentCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        
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
        setupRecyclerView();
        setupTabListeners();
        setupBackButton();
        
        // Load data từ API
        loadMenuItems();
    }
    
    private void initViews() {
        recyclerFoodList = findViewById(R.id.recycler_food_list);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tabAll = findViewById(R.id.tab_all);
        tabBreakfast = findViewById(R.id.tab_breakfast);
        tabLunch = findViewById(R.id.tab_lunch);
        tabDinner = findViewById(R.id.tab_dinner);
        btnBack = findViewById(R.id.btn_back);
        // progressBar = findViewById(R.id.progress_bar); // Chưa có trong layout
    }
    
    private void initData() {
        menuItems = new ArrayList<>();
        repository = new FoodRepository();
    }
    
    /**
     * Load menu items từ API
     */
    private void loadMenuItems() {
        Log.d(TAG, "Loading menu items from API...");
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
                        Toast.makeText(FoodListActivity.this,
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
                item.getName() != null ? item.getName() : "Unknown",
                item.getFormattedPrice(),
                item.getCategoryName() != null ? item.getCategoryName() : "",
                R.drawable.chicken_thai_biriyani, // Placeholder image
                "Location",
                new ArrayList<>(),
                item.getDescription() != null ? item.getDescription() : "",
                "Delivery"
            );
            foodList.add(food);
        }
        
        foodAdapter = new FoodAdapter(this, foodList);
        recyclerFoodList.setAdapter(foodAdapter);
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
    
    private void selectTab(TextView selectedTab, String category) {
        resetAllTabs();
        
        selectedTab.setBackgroundColor(getResources().getColor(R.color.orange));
        selectedTab.setTextColor(getResources().getColor(R.color.white));
        
        // TODO: Map category name → ID (cần load categories trước)
        currentCategoryId = null; // Tạm thời set null
        currentPage = 0;
        loadMenuItems();
    }
    
    private void resetAllTabs() {
        TextView[] tabs = {tabAll, tabBreakfast, tabLunch, tabDinner};
        for (TextView tab : tabs) {
            tab.setBackgroundColor(getResources().getColor(R.color.light_gray));
            tab.setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }
    
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void updateTotalItems(int count) {
        String totalText = String.format("Total %02d items", count);
        tvTotalItems.setText(totalText);
    }
}
