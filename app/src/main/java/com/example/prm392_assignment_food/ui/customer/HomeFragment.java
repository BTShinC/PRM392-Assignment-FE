package com.example.prm392_assignment_food.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.repository.FoodRepository;
import com.example.prm392_assignment_food.ui.cart.CartActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int PAGE_SIZE = 10;

    // UI Components
    private RecyclerView recyclerFoodList;
    private FoodAdapter foodAdapter;
    private TextView tvTotalItems;
    private TextView tabAll, tabBreakfast, tabLunch, tabDinner;
    private ImageView btnCart;
    private ProgressBar progressBar;
    private TextInputLayout tilSearch;
    private TextInputEditText etSearch;

    // Data
    private List<MenuItemResponse> menuItems;
    private FoodRepository repository;
    private int currentPage = 0;
    private String currentCategoryId = null;
    private String currentSearch = null;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.prm392_assignment_food.data.network.ApiClient.init(requireActivity());

        initViews();
        initData();
        setupRecyclerView();
        setupTabListeners();
        loadCategories();
        setupClickListeners();

        loadMenuItems();
    }

    private void initViews() {
        recyclerFoodList = rootView.findViewById(R.id.recycler_food_list);
        tvTotalItems = rootView.findViewById(R.id.tv_total_items);
        tabAll = rootView.findViewById(R.id.tab_all);
        tabBreakfast = rootView.findViewById(R.id.tab_breakfast);
        tabLunch = rootView.findViewById(R.id.tab_lunch);
        tabDinner = rootView.findViewById(R.id.tab_dinner);
        btnCart = rootView.findViewById(R.id.btn_cart);
        progressBar = rootView.findViewById(R.id.progress_bar);
        tilSearch = rootView.findViewById(R.id.til_search);
        etSearch = rootView.findViewById(R.id.et_search);
    }

    private void initData() {
        menuItems = new ArrayList<>();
        repository = new FoodRepository();
    }

    private void setupRecyclerView() {
        recyclerFoodList.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(tabAll, null));
        tabBreakfast.setOnClickListener(v -> selectTab(tabBreakfast, null)); // You might want to use actual category IDs here
        tabLunch.setOnClickListener(v -> selectTab(tabLunch, null));
        tabDinner.setOnClickListener(v -> selectTab(tabDinner, null));
    }

    private void setupClickListeners() {
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CartActivity.class);
            startActivity(intent);
        });

        if (tilSearch != null) {
            tilSearch.setEndIconOnClickListener(v -> {
                currentSearch = etSearch != null && etSearch.getText() != null
                        ? etSearch.getText().toString().trim() : null;
                currentPage = 0;
                loadMenuItems();
            });
        }
    }

    private void selectTab(TextView selectedTab, String categoryId) {
        resetAllTabs();

        selectedTab.setBackgroundResource(R.drawable.tab_selected_background);
        selectedTab.setTextColor(getResources().getColor(R.color.white, null));

        currentCategoryId = categoryId;
        currentPage = 0;
        loadMenuItems();
    }

    private void resetAllTabs() {
        TextView[] tabs = {tabAll, tabBreakfast, tabLunch, tabDinner};
        for (TextView tab : tabs) {
            if (tab == null) continue;
            tab.setBackgroundResource(R.drawable.tab_unselected_background);
            tab.setTextColor(getResources().getColor(R.color.medium_gray, null));
        }
        LinearLayout container = rootView.findViewById(R.id.layout_categories);
        if (container != null) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof TextView && child.getId() != R.id.tab_all) {
                    ((TextView) child).setBackgroundResource(R.drawable.tab_unselected_background);
                    ((TextView) child).setTextColor(getResources().getColor(R.color.medium_gray, null));
                }
            }
        }
    }

    private void loadMenuItems() {
        showLoading();

        repository.getMenuItems(currentPage, PAGE_SIZE, "name", currentSearch, currentCategoryId,
                new FoodRepository.RepositoryCallback<PageResponse<MenuItemResponse>>() {
                    @Override
                    public void onSuccess(PageResponse<MenuItemResponse> data) {
                        if (isAdded()) { // Check if Fragment is still attached
                            requireActivity().runOnUiThread(() -> {
                                hideLoading();
                                menuItems.clear();
                                menuItems.addAll(data.getContent());
                                updateUI();
                                updateTotalItems(countAvailableItems());
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                hideLoading();
                                Toast.makeText(requireContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                            });
                        }
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
        List<Food> foodList = new ArrayList<>();
        for (MenuItemResponse item : menuItems) {
            if (Boolean.TRUE.equals(item.getAvailable())) {
                Food food = new Food(
                        item.getId(),
                        item.getName() != null ? item.getName() : "Unknown",
                        item.getFormattedPrice(),
                        item.getCategoryName() != null ? item.getCategoryName() : "",
                        item.getImageUrl(),
                        item.getDescription() != null ? item.getDescription() : ""
                );
                foodList.add(food);
            }
        }

        foodAdapter = new FoodAdapter(requireContext(), foodList);
        recyclerFoodList.setAdapter(foodAdapter);
    }

    private int countAvailableItems() {
        int count = 0;
        for (MenuItemResponse item : menuItems) {
            if (Boolean.TRUE.equals(item.getAvailable())) {
                count++;
            }
        }
        return count;
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
                        if (isAdded()) {
                           requireActivity().runOnUiThread(() -> buildCategoryTabs(data.getContent()));
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Load categories error: " + errorMessage);
                    }
                });
    }

    private void buildCategoryTabs(List<MenuCategoryResponse> categories) {
        LinearLayout container = rootView.findViewById(R.id.layout_categories);
        if (container == null) return;

        while (container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        final float density = getResources().getDisplayMetrics().density;
        int padH = (int) (20 * density);
        int padV = (int) (8 * density);
        int marginEnd = (int) (8 * density);

        List<MenuCategoryResponse> ordered = new ArrayList<>();
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
            TextView tab = new TextView(requireContext());
            tab.setText(cat.getName());
            tab.setTextSize(14);
            tab.setTextColor(getResources().getColor(R.color.medium_gray, null));
            tab.setBackgroundResource(R.drawable.tab_unselected_background);
            tab.setPadding(padH, padV, padH, padV);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, marginEnd, 0);
            tab.setLayoutParams(lp);
            tab.setClickable(true);
            tab.setFocusable(true);
            final String id = cat.getCategoryId();
            tab.setOnClickListener(v -> selectTab(tab, id));
            container.addView(tab);
        }
    }
}
