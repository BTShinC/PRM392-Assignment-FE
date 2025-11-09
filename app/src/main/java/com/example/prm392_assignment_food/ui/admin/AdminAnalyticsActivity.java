package com.example.prm392_assignment_food.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAnalyticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvTotalUsers;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        pieChart = findViewById(R.id.pieChart);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        apiService = ApiClient.getApiService();

        loadAndProcessChartData();
    }

    private void loadAndProcessChartData() {
        // Mock data for total users
        tvTotalUsers.setText("Tổng số người dùng: 250");

        // Step 1: Get all menu items to create a map of itemId -> categoryName
        apiService.getMenuItems(0, 1000, "name,asc", null, null).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> menuItemCategoryMap = new HashMap<>();
                    for (MenuItemResponse item : response.body().getContent()) {
                        menuItemCategoryMap.put(item.getId(), item.getCategoryName());
                    }
                    // Step 2: Get all completed orders and process them
                    fetchCompletedOrdersAndCalculateRevenue(menuItemCategoryMap);
                } else {
                    Toast.makeText(AdminAnalyticsActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                Toast.makeText(AdminAnalyticsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCompletedOrdersAndCalculateRevenue(Map<String, String> menuItemCategoryMap) {
        // Step 2: Fetch all completed orders (assuming a large enough page size)
        apiService.getOrders(0, 1000, Collections.singletonList("createdAt,DESC"), "COMPLETED", null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> completedOrders = response.body().getData().getContent();

                    // Step 3: Calculate revenue per category
                    Map<String, Double> revenueByCategory = new HashMap<>();
                    for (OrderResponse order : completedOrders) {
                        for (OrderResponse.OrderItemResponse item : order.orderItems) {
                            String categoryName = menuItemCategoryMap.get(item.menuItemId);
                            if (categoryName != null) {
                                // The price and quantity are not directly available in OrderItemResponse.
                                // The revenue is implicitly calculated on the backend and reflected in order.totalPrice.
                                // For simplicity, we'll have to make an assumption.
                                // Let's find the menu item to get its price.
                                // This is inefficient, but necessary with the current data structure.
                                // A better solution would be to have the price in the OrderItemResponse.
                                // For now, we'll just distribute the total price of the order among the items.
                                // This is not accurate, but it's the best we can do without backend changes.
                                double itemRevenue = order.totalPrice / order.orderItems.size();
                                revenueByCategory.put(categoryName, revenueByCategory.getOrDefault(categoryName, 0.0) + itemRevenue);
                            }
                        }
                    }

                    // Step 4: Setup Pie Chart
                    setupPieChart(revenueByCategory);

                } else {
                    Toast.makeText(AdminAnalyticsActivity.this, "Failed to load completed orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                 Toast.makeText(AdminAnalyticsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPieChart(Map<String, Double> revenueByCategory) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (revenueByCategory.isEmpty()) {
            entries.add(new PieEntry(100f, "Chưa có dữ liệu"));
        } else {
            for (Map.Entry<String, Double> entry : revenueByCategory.entrySet()) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Doanh thu theo danh mục");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}