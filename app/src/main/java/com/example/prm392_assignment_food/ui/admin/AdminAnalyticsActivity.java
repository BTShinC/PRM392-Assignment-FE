package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_assignment_food.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AdminAnalyticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvTotalUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        barChart = findViewById(R.id.barChart);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);

        loadDashboardData();
    }

    private void loadDashboardData() {
        // Mock data
        int totalUsers = 250;
        tvTotalUsers.setText("Tổng số người dùng: " + totalUsers);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 120f)); // Month 1: 120 users
        entries.add(new BarEntry(2, 180f)); // Month 2
        entries.add(new BarEntry(3, 220f)); // Month 3

        BarDataSet dataSet = new BarDataSet(entries, "Lượng người dùng theo tháng");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
