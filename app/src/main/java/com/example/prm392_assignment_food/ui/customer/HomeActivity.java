package com.example.prm392_assignment_food.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
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
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
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
    private TextInputLayout tilSearch;
    private TextInputEditText etSearch;

    // Data
    private List<MenuItemResponse> menuItems;
    private FoodRepository repository;
    private int currentPage = 0;
    private String currentCategoryId = null;
    private String currentSearch = null;

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
        loadCategories();
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
        tilSearch = findViewById(R.id.til_search);
        etSearch = findViewById(R.id.et_search);
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
        tabBreakfast.setOnClickListener(v -> selectTab(tabBreakfast, null));
        tabLunch.setOnClickListener(v -> selectTab(tabLunch, null));
        tabDinner.setOnClickListener(v -> selectTab(tabDinner, null));
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
        // Search end icon click
        if (tilSearch != null) {
            tilSearch.setEndIconOnClickListener(v -> {
                currentSearch = etSearch != null && etSearch.getText() != null
                        ? etSearch.getText().toString().trim() : null;
                currentPage = 0;
                loadMenuItems();
            });
        }

        
        btnGoToMain.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void selectTab(TextView selectedTab, String categoryId) {
        resetAllTabs();

        selectedTab.setBackgroundResource(R.drawable.tab_selected_background);
        selectedTab.setTextColor(getResources().getColor(R.color.white));

        currentCategoryId = categoryId;
        currentPage = 0;
        loadMenuItems();
    }

    private void resetAllTabs() {
        // Reset các tab tĩnh
        TextView[] tabs = {tabAll, tabBreakfast, tabLunch, tabDinner};
        for (TextView tab : tabs) {
            if (tab == null) continue;
            tab.setBackgroundResource(R.drawable.tab_unselected_background);
            tab.setTextColor(getResources().getColor(R.color.medium_gray));
        }
        // Reset các tab được build động từ API
        android.widget.LinearLayout container = findViewById(R.id.layout_categories);
        if (container != null) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof TextView && child.getId() != R.id.tab_all) {
                    ((TextView) child).setBackgroundResource(R.drawable.tab_unselected_background);
                    ((TextView) child).setTextColor(getResources().getColor(R.color.medium_gray));
                }
            }
        }
    }

    /**
     * Load menu items từ API
     */
    private void loadMenuItems() {
        showLoading();

        repository.getMenuItems(currentPage, PAGE_SIZE, "name", currentSearch, currentCategoryId,
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

    private void loadCategories() {
        repository.getMenuCategories(0, 20, "name", null,
                new FoodRepository.RepositoryCallback<PageResponse<MenuCategoryResponse>>() {
                    @Override
                    public void onSuccess(PageResponse<MenuCategoryResponse> data) {
                        runOnUiThread(() -> buildCategoryTabs(data.getContent()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Load categories error: " + errorMessage);
                    }
                });
    }

    private void buildCategoryTabs(List<MenuCategoryResponse> categories) {
        android.widget.LinearLayout container = findViewById(R.id.layout_categories);
        if (container == null) return;

        // Keep the first tab (All), remove the rest
        while (container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        final float density = getResources().getDisplayMetrics().density;
        int padH = (int) (20 * density);
        int padV = (int) (8 * density);
        int marginEnd = (int) (8 * density);

        // Sắp xếp: Bữa Sáng -> Bữa Trưa -> Bữa Tối -> Others
        java.util.List<MenuCategoryResponse> ordered = new java.util.ArrayList<>();
        for (MenuCategoryResponse c : categories) {
            String n = c.getName() != null ? c.getName() : "";
            if (n.toLowerCase().contains("sáng")) ordered.add(c);
        }
        for (MenuCategoryResponse c : categories) {
            String n = c.getName() != null ? c.getName() : "";
            if (n.toLowerCase().contains("trưa")) ordered.add(c);
        }
        for (MenuCategoryResponse c : categories) {
            String n = c.getName() != null ? c.getName() : "";
            if (n.toLowerCase().contains("tối") || n.toLowerCase().contains("toi")) ordered.add(c);
        }
        for (MenuCategoryResponse c : categories) {
            if (!ordered.contains(c)) ordered.add(c);
        }

        for (MenuCategoryResponse cat : ordered) {
            TextView tab = new TextView(this);
            tab.setText(cat.getName());
            tab.setTextSize(14);
            tab.setTextColor(getResources().getColor(R.color.medium_gray));
            tab.setBackgroundResource(R.drawable.tab_unselected_background);
            tab.setPadding(padH, padV, padH, padV);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, marginEnd, 0);
            tab.setLayoutParams(lp);
            tab.setClickable(true);
            tab.setFocusable(true);
            final String id = cat.getCategoryId();
            tab.setOnClickListener(v -> selectTab(tab, id));
            container.addView(tab);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, com.example.prm392_assignment_food.ui.auth.ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
