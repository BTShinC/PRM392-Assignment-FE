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

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.admin.DashboardResponse;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private ApiService apiService;
    private TextView tvRunningOrders;
    private TextView tvOrderRequest;
    private TextView tvTotalRevenue;
    private LineChart lineChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Listen for results from the bottom sheet
        getParentFragmentManager().setFragmentResultListener(RunningOrdersBottomSheetFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            // The bottom sheet was dismissed, so we refresh the counts
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
        lineChart = view.findViewById(R.id.lineChart);

        setupLineChart();
        updateOrderCounts();
        updateDashboard();

        view.findViewById(R.id.running_orders_card).setOnClickListener(v -> {
            showOrdersBottomSheet("CONFIRMED");
        });

        view.findViewById(R.id.order_request_card).setOnClickListener(v -> {
            showOrdersBottomSheet("PAID");
        });

        view.findViewById(R.id.revenue_card).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminAnalyticsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOrderCounts();
        updateDashboard();
    }
    
    private void showOrdersBottomSheet(String status) {
        RunningOrdersBottomSheetFragment bottomSheet = RunningOrdersBottomSheetFragment.newInstance(status);
        bottomSheet.show(getParentFragmentManager(), RunningOrdersBottomSheetFragment.TAG);
    }

    private void updateOrderCounts() {
        fetchOrderCountByStatus("PAID", tvOrderRequest);
        fetchOrderCountByStatus("CONFIRMED", tvRunningOrders);
    }
    
    private void updateDashboard() {
        apiService.getDashboardAll().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvTotalRevenue.setText(String.format("$%.2f", response.body().data.total));
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load dashboard data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOrderCountByStatus(String status, TextView textView) {
        apiService.getOrders(0, 1, null, status, null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<OrderResponse> pageResponse = response.body().getData();
                    if (pageResponse != null) {
                        textView.setText(String.valueOf(pageResponse.getTotalElements()));
                    } else {
                        textView.setText("0");
                    }
                } else {
                    textView.setText("0");
}
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                textView.setText("0");
            }
        });
    }

    private void setupLineChart() {
        // Chart setup remains the same
        ArrayList<Entry> entries = new ArrayList<>();
        // Add some dummy data for now, we will replace it later
        entries.add(new Entry(0, 0));
       

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        dataSet.setColor(Color.parseColor("#FB6D3A"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.parseColor("#FB6D3A"));
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.invalidate(); 
    }
}