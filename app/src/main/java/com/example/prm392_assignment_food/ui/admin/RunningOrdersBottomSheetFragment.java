package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

// A dummy data model class. Replace with your actual data model.
class RunningOrder {
    private String category;
    private String name;
    private String id;
    private String price;

    public RunningOrder(String category, String name, String id, String price) {
        this.category = category;
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getId() { return id; }
    public String getPrice() { return price; }
}

public class RunningOrdersBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "RunningOrdersBottomSheetFragment";

    public static RunningOrdersBottomSheetFragment newInstance() {
        return new RunningOrdersBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_running_orders, container, false);

        RecyclerView rvRunningOrders = view.findViewById(R.id.rv_running_orders);
        rvRunningOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Populate with dummy data for demonstration
        List<RunningOrder> runningOrderList = new ArrayList<>();
        runningOrderList.add(new RunningOrder("#Breakfast", "Chicken Thai Biriyani", "ID: 32053", "$60"));
        runningOrderList.add(new RunningOrder("#Breakfast", "Chicken Bhuna", "ID: 15253", "$30"));
        runningOrderList.add(new RunningOrder("#Breakfast", "Vegetarian Poutine", "ID: 21200", "$35"));
        runningOrderList.add(new RunningOrder("#Breakfast", "Turkey Bacon Strips", "ID: 53241", "$45"));
        runningOrderList.add(new RunningOrder("#Breakfast", "Veggie Burrito.", "ID: 58464", "$25"));

        RunningOrderAdapter adapter = new RunningOrderAdapter(runningOrderList);
        rvRunningOrders.setAdapter(adapter);

        return view;
    }
}
