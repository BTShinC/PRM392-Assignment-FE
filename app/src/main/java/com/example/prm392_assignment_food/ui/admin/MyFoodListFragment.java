package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFoodListFragment extends Fragment {

    private static final String TAG = "MyFoodListFragment";

    private RecyclerView recyclerView;
    private MyFoodListAdapter adapter;
    private List<MenuItemResponse> foodItems = new ArrayList<>();
    private ApiService apiService;
    private TabLayout tabLayout;
    private TextView tvTotalItems;
    private final Map<String, String> categoryNameToIdMap = new HashMap<>();
    private boolean isTabListenerSetup = false; // Flag to prevent setting listener multiple times

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_food_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_food_list);
        tabLayout = view.findViewById(R.id.tab_layout);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        apiService = ApiClient.getApiService();

        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetch fresh data every time the fragment becomes visible.
        fetchCategoriesAndRefreshData();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyFoodListAdapter(foodItems);
        recyclerView.setAdapter(adapter);
    }

    private void fetchCategoriesAndRefreshData() {
        apiService.getMenuCategories(0, 100, "name,asc", null).enqueue(new Callback<PageResponse<MenuCategoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuCategoryResponse>> call, Response<PageResponse<MenuCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryNameToIdMap.clear();
                    for (MenuCategoryResponse category : response.body().getContent()) {
                        categoryNameToIdMap.put(category.getName().toLowerCase(), category.getCategoryId());
                    }
                    Log.d(TAG, "Categories loaded: " + categoryNameToIdMap.keySet());

                    if (!isTabListenerSetup) {
                        setupTabListener();
                        isTabListenerSetup = true;
                    }
                    // After categories are loaded, refresh the list based on the currently selected tab.
                    refreshListBasedOnCurrentTab();
                } else {
                    Toast.makeText(getContext(), "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuCategoryResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading categories", t);
                Toast.makeText(getContext(), "Error loading categories: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshListBasedOnCurrentTab() {
        TabLayout.Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
        if (selectedTab != null) {
            handleTabSelection(selectedTab);
        } else {
            // Default to fetching all items if no tab is selected for some reason
            fetchMenuItems(null);
        }
    }

    private void handleTabSelection(TabLayout.Tab tab) {
        String selectedTabText = tab.getText().toString();
        Log.d(TAG, "Handling selection for tab: " + selectedTabText);

        if ("Tất cả".equalsIgnoreCase(selectedTabText)) {
            fetchMenuItems(null);
        } else {
            String categoryId = categoryNameToIdMap.get(selectedTabText.toLowerCase());
            if (categoryId != null) {
                fetchMenuItems(categoryId);
            } else {
                Log.w(TAG, "No category ID found for tab: " + selectedTabText);
                Toast.makeText(getContext(), "Không tìm thấy danh mục: " + selectedTabText, Toast.LENGTH_SHORT).show();
                adapter.updateData(new ArrayList<>());
                tvTotalItems.setText("Tổng 00 món");
            }
        }
    }

    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabSelection(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchMenuItems(String categoryId) {
        Log.d(TAG, "Fetching menu items for categoryId: " + categoryId);
        apiService.getMenuItems(0, 50, "name,asc", null, categoryId).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItemResponse> items = response.body().getContent();
                    adapter.updateData(items);
                    tvTotalItems.setText(String.format(Locale.getDefault(), "Tổng %02d món", items.size()));
                    Log.d(TAG, "Successfully loaded " + items.size() + " items.");
                } else {
                    Toast.makeText(getContext(), "Failed to load menu items.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load menu items. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                Log.e(TAG, "An error occurred while fetching menu items", t);
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
