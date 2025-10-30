package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.admin.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class MyFoodListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_food_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_food_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("Chicken Thai Biriyani", "Breakfast", 4.9f, 10, 60.0, R.drawable.onboarding1));
        foodItems.add(new FoodItem("Chicken Bhuna", "Breakfast", 4.9f, 10, 30.0, R.drawable.onboarding1));
        foodItems.add(new FoodItem("Mazalichiken Halim", "Breakfast", 4.9f, 10, 25.0, R.drawable.onboarding1));

        MyFoodListAdapter adapter = new MyFoodListAdapter(foodItems);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
