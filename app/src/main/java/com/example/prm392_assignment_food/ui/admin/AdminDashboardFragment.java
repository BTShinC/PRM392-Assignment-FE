package com.example.prm392_assignment_food.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    private LineChart lineChart;
    private Spinner spinnerLocation, spinnerTimeRange;
    private RecyclerView rvPopularItems;
    private MaterialCardView runningOrdersCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        spinnerTimeRange = view.findViewById(R.id.spinnerTimeRange);
        rvPopularItems = view.findViewById(R.id.rvPopularItems);
        runningOrdersCard = view.findViewById(R.id.running_orders_card);

        setupSpinners();
        setupLineChart();
        setupRecyclerView();

        runningOrdersCard.setOnClickListener(v -> {
            RunningOrdersBottomSheetFragment bottomSheet = RunningOrdersBottomSheetFragment.newInstance();
            bottomSheet.show(getParentFragmentManager(), RunningOrdersBottomSheetFragment.TAG);
        });

        return view;
    }

    private void setupSpinners() {
        // Location Spinner
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.locations, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        // Time Range Spinner
        ArrayAdapter<CharSequence> timeRangeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_range, android.R.layout.simple_spinner_item);
        timeRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeRange.setAdapter(timeRangeAdapter);
    }

    private void setupLineChart() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 6));
        entries.add(new Entry(3, 2));
        entries.add(new Entry(4, 5));
        entries.add(new Entry(5, 4));
        entries.add(new Entry(6, 7));


        LineDataSet dataSet = new LineDataSet(entries, "Total Revenue");
        dataSet.setColor(Color.parseColor("#FFA500"));
        dataSet.setCircleColor(Color.parseColor("#FFA500"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        final String[] quarters = new String[]{"10AM", "11AM", "12PM", "01PM", "02PM", "03PM", "04PM"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return quarters[(int) value];
            }
        });

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.invalidate(); // refresh
    }

    private void setupRecyclerView() {
        rvPopularItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularItems.setAdapter(new PopularItemsAdapter());
    }

    private class PopularItemsAdapter extends RecyclerView.Adapter<PopularItemsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_food, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Bind data here
        }

        @Override
        public int getItemCount() {
            return 5; // Sample count
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
