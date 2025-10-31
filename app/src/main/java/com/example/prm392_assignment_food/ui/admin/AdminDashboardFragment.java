package com.example.prm392_assignment_food.ui.admin;

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
    private LineChart lineChart;

    public AdminDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();
        tvRunningOrders = view.findViewById(R.id.tvRunningOrders);
        lineChart = view.findViewById(R.id.lineChart);

        setupLineChart();
        fetchRunningOrdersCount();

        // Find the running orders card
        View runningOrdersCard = view.findViewById(R.id.running_orders_card);
        runningOrdersCard.setOnClickListener(v -> {
            // Show the bottom sheet
            RunningOrdersBottomSheetFragment bottomSheet = RunningOrdersBottomSheetFragment.newInstance();
            bottomSheet.show(getParentFragmentManager(), RunningOrdersBottomSheetFragment.TAG);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the count when the fragment is resumed
        fetchRunningOrdersCount();
    }

    private void fetchRunningOrdersCount() {
        // Fetch orders with PAID status to get the count of "running" orders
        apiService.getOrders(0, 1, null, "PAID", null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<OrderResponse> pageResponse = response.body().getData();
                    if (pageResponse != null) {
                        // totalElements gives the total count of items matching the query
                        tvRunningOrders.setText(String.valueOf(pageResponse.getTotalElements()));
                    } else {
                        tvRunningOrders.setText("0");
                    }
                } else {
                    // Handle API error gracefully
                    tvRunningOrders.setText("0");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                // Handle network failure gracefully
                tvRunningOrders.setText("0");
                Toast.makeText(getContext(), "Error fetching running orders count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 6));
        entries.add(new Entry(3, 2));
        entries.add(new Entry(4, 7));
        entries.add(new Entry(5, 8));
        entries.add(new Entry(6, 5));

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
        lineChart.invalidate(); // refresh
    }
}