package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.admin.FoodItem;
import java.util.ArrayList;
import java.util.List;

public class MyFoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_food_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_food_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("Chicken Thai Biriyani", "Breakfast", 4.9f, 10, 60.0, R.drawable.onboarding1));
        foodItems.add(new FoodItem("Chicken Bhuna", "Breakfast", 4.9f, 10, 30.0, R.drawable.onboarding1));
        foodItems.add(new FoodItem("Mazalichiken Halim", "Breakfast", 4.9f, 10, 25.0, R.drawable.onboarding1));

        MyFoodListAdapter adapter = new MyFoodListAdapter(foodItems);
        recyclerView.setAdapter(adapter);
    }
}
