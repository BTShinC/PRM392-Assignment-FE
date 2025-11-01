package com.example.prm392_assignment_food.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.admin.DashboardResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAnalyticsActivity extends AppCompatActivity {

    private LineChart lineChart;
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

        lineChart = findViewById(R.id.lineChart);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        apiService = ApiClient.getApiService();

        loadDashboardData();
    }

    private void loadDashboardData() {
        // Mock data for total users
        int totalUsers = 250;
        tvTotalUsers.setText("Tổng số người dùng: " + totalUsers);

        List<Entry> entries = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(12);

        for (int i = 1; i <= 12; i++) {
            final int month = i;
            apiService.getDashboardByMonth(month).enqueue(new Callback<DashboardResponse>() {
                @Override
                public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        entries.add(new Entry(month, (float) response.body().data.total));
                    } else {
                        entries.add(new Entry(month, 0));
                    }
                    if (counter.decrementAndGet() == 0) {
                        setupChart(entries);
                    }
                }

                @Override
                public void onFailure(Call<DashboardResponse> call, Throwable t) {
                    entries.add(new Entry(month, 0));
                    if (counter.decrementAndGet() == 0) {
                        setupChart(entries);
                    }
                }
            });
        }
    }

    private void setupChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu theo tháng");
        dataSet.setColor(Color.parseColor("#FB6D3A"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.parseColor("#FB6D3A"));
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }
}
