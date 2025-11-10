package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private ApiService apiService;
    private TextView tvRunningOrders;
    private TextView tvOrderRequest;
    private TextView tvTotalRevenue;
    private PieChart pieChart;
    private RecyclerView rvPopularItems;
    private PopularItemAdapter popularItemAdapter;
    private List<PopularItem> popularItemList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(RunningOrdersBottomSheetFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            updateOrderCounts();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();
        tvRunningOrders = view.findViewById(R.id.tvRunningOrders);
        tvOrderRequest = view.findViewById(R.id.tvOrderRequest);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        pieChart = view.findViewById(R.id.pieChart);
        rvPopularItems = view.findViewById(R.id.rvPopularItems);

        setupPopularItemsRecyclerView();
        updateOrderCounts();
        loadAndProcessChartData();

        view.findViewById(R.id.running_orders_card).setOnClickListener(v -> showOrdersBottomSheet("CONFIRMED"));
        view.findViewById(R.id.order_request_card).setOnClickListener(v -> showOrdersBottomSheet("PAID"));
        view.findViewById(R.id.revenue_card).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminAnalyticsActivity.class);
            startActivity(intent);
        });
    }

    private void setupPopularItemsRecyclerView() {
        popularItemAdapter = new PopularItemAdapter(getContext(), popularItemList);
        rvPopularItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularItems.setAdapter(popularItemAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOrderCounts();
        loadAndProcessChartData(); // This will refresh the data
    }

    private void showOrdersBottomSheet(String status) {
        RunningOrdersBottomSheetFragment bottomSheet = RunningOrdersBottomSheetFragment.newInstance(status);
        bottomSheet.show(getParentFragmentManager(), RunningOrdersBottomSheetFragment.TAG);
    }

    private void updateOrderCounts() {
        fetchCombinedOrderRequestCount();
        fetchCombinedRunningOrdersCount();
    }
    
    private void fetchCombinedOrderRequestCount() {
        final AtomicInteger totalCount = new AtomicInteger(0);
        final AtomicInteger callCounter = new AtomicInteger(2);

        Callback<ApiResponseDto<PageResponse<OrderResponse>>> callback = new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // CORRECTED TYPE CONVERSION
                    totalCount.addAndGet(response.body().getData().getTotalElements().intValue());
                }
                if (callCounter.decrementAndGet() == 0) {
                    tvOrderRequest.setText(String.valueOf(totalCount.get()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                if (callCounter.decrementAndGet() == 0) {
                    tvOrderRequest.setText(String.valueOf(totalCount.get()));
                }
            }
        };

        apiService.getOrders(0, 1, null, "PAID", null).enqueue(callback);
        apiService.getOrders(0, 1, null, "AWAITING_PAYMENT", null).enqueue(callback);
    }
    
    private void fetchCombinedRunningOrdersCount() {
         final AtomicInteger totalCount = new AtomicInteger(0);
        final AtomicInteger callCounter = new AtomicInteger(2);

        Callback<ApiResponseDto<PageResponse<OrderResponse>>> callback = new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // CORRECTED TYPE CONVERSION
                    totalCount.addAndGet(response.body().getData().getTotalElements().intValue());
                }
                if (callCounter.decrementAndGet() == 0) {
                    tvRunningOrders.setText(String.valueOf(totalCount.get()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                if (callCounter.decrementAndGet() == 0) {
                    tvRunningOrders.setText(String.valueOf(totalCount.get()));
                }
            }
        };

        apiService.getOrders(0, 1, null, "CONFIRMED", null).enqueue(callback);
        apiService.getOrders(0, 1, null, "SHIPPING", null).enqueue(callback);
    }


    private void loadAndProcessChartData() {
        apiService.getMenuItems(0, 1000, "name,asc", null, null).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, MenuItemResponse> menuItemMap = new HashMap<>();
                    for (MenuItemResponse item : response.body().getContent()) {
                        menuItemMap.put(item.getId(), item);
                    }
                    fetchCompletedOrdersAndCalculate(menuItemMap);
                } else {
                    Toast.makeText(getContext(), "Failed to load menu items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCompletedOrdersAndCalculate(Map<String, MenuItemResponse> menuItemMap) {
        apiService.getOrders(0, 1000, Collections.singletonList("createdAt,DESC"), "COMPLETED", null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> completedOrders = response.body().getData().getContent();
                    double totalRevenue = 0;

                    Map<String, Double> revenueByCategory = new HashMap<>();
                    Map<String, Integer> popularItemsCount = new HashMap<>();

                    for (OrderResponse order : completedOrders) {
                        totalRevenue += order.totalPrice;
                        if (order.orderItems == null || order.orderItems.isEmpty()) continue;

                        double itemRevenue = order.totalPrice / order.orderItems.size();

                        for (OrderResponse.OrderItemResponse item : order.orderItems) {
                            MenuItemResponse menuItem = menuItemMap.get(item.menuItemId);
                            if (menuItem != null) {
                                revenueByCategory.put(menuItem.getCategoryName(), revenueByCategory.getOrDefault(menuItem.getCategoryName(), 0.0) + itemRevenue);
                                popularItemsCount.put(item.menuItemId, popularItemsCount.getOrDefault(item.menuItemId, 0) + 1);
                            }
                        }
                    }

                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvTotalRevenue.setText(currencyFormatter.format(totalRevenue));
                    setupPieChart(revenueByCategory);
                    updatePopularItems(popularItemsCount, menuItemMap);

                } else {
                    Toast.makeText(getContext(), "Failed to load completed orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePopularItems(Map<String, Integer> popularItemsCount, Map<String, MenuItemResponse> menuItemMap) {
        List<PopularItem> sortedItems = popularItemsCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> new PopularItem(menuItemMap.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        popularItemList.clear();
        popularItemList.addAll(sortedItems);
        popularItemAdapter.notifyDataSetChanged();
    }

    private void setupPieChart(Map<String, Double> revenueByCategory) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (revenueByCategory.isEmpty()) {
            entries.add(new PieEntry(100f, "Chưa có dữ liệu"));
        } else {
            for (Map.Entry<String, Double> entry : revenueByCategory.entrySet()) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }
}
