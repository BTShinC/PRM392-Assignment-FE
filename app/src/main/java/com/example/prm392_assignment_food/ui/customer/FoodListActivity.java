package com.example.prm392_assignment_food.ui.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerFoodList;
    private FoodAdapter foodAdapter;
    private List<Food> foodList;
    private TextView tvTotalItems;
    private TextView tabAll, tabBreakfast, tabLunch, tabDinner;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        
        initViews();
        setupSampleData();
        setupRecyclerView();
        setupTabListeners();
        setupBackButton();
        updateTotalItems();
    }
    
    private void initViews() {
        recyclerFoodList = findViewById(R.id.recycler_food_list);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tabAll = findViewById(R.id.tab_all);
        tabBreakfast = findViewById(R.id.tab_breakfast);
        tabLunch = findViewById(R.id.tab_lunch);
        tabDinner = findViewById(R.id.tab_dinner);
        btnBack = findViewById(R.id.btn_back);
    }
    
    private void setupSampleData() {
        foodList = new ArrayList<>();
        
        // Sample ingredients lists
        List<String> biriyaniIngredients = Arrays.asList("Salt", "Chicken", "Onion", "Garlic", "Pappers", "Ginger", "Broccoli", "Orange", "Walnut");
        List<String> bhunaIngredients = Arrays.asList("Salt", "Chicken", "Onion", "Garlic", "Ginger");
        List<String> halimIngredients = Arrays.asList("Salt", "Chicken", "Onion", "Garlic", "Pappers");
        
        // Add more ingredients for variety
        List<String> lunchIngredients = Arrays.asList("Salt", "Chicken", "Broccoli", "Orange");
        List<String> dinnerIngredients = Arrays.asList("Salt", "Chicken", "Garlic", "Walnut");
        
        // Add sample food items with real food images
        foodList.add(new Food(
            "Chicken Thai Biriyani",
            "$60",
            "Breakfast",
            4.9f,
            10,
            R.drawable.chicken_thai_biriyani,
            "Kentucky 39495",
            biriyaniIngredients,
            "Lorem ipsum dolor sit amet, consetdur Maton adipiscing elit. Bibendum in vel, mattis et amet dui mauris turpis.",
            "Delivery"
        ));
        
        foodList.add(new Food(
            "Chicken Bhuna",
            "$30",
            "Breakfast",
            4.9f,
            10,
            R.drawable.chicken_bhuna,
            "Kentucky 39495",
            bhunaIngredients,
            "Lorem ipsum dolor sit amet, consetdur Maton adipiscing elit. Bibendum in vel, mattis et amet dui mauris turpis.",
            "Pick UP"
        ));
        
        foodList.add(new Food(
            "Mazalichiken Halim",
            "$25",
            "Breakfast",
            4.9f,
            10,
            R.drawable.halim,
            "Kentucky 39495",
            halimIngredients,
            "Lorem ipsum dolor sit amet, consetdur Maton adipiscing elit. Bibendum in vel, mattis et amet dui mauris turpis.",
            "Pick UP"
        ));
        
        // Add some lunch items
        foodList.add(new Food(
            "Grilled Chicken Salad",
            "$35",
            "Lunch",
            4.7f,
            8,
            R.drawable.chicken_thai_biriyani, // Reuse image for demo
            "Kentucky 39495",
            lunchIngredients,
            "Fresh grilled chicken with mixed vegetables and special sauce.",
            "Delivery"
        ));
        
        // Add some dinner items  
        foodList.add(new Food(
            "Chicken Curry Special",
            "$45",
            "Dinner",
            4.8f,
            12,
            R.drawable.chicken_bhuna, // Reuse image for demo
            "Kentucky 39495",
            dinnerIngredients,
            "Traditional chicken curry with aromatic spices and herbs.",
            "Pick UP"
        ));
    }
    
    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(this, foodList);
        recyclerFoodList.setLayoutManager(new LinearLayoutManager(this));
        recyclerFoodList.setAdapter(foodAdapter);
    }
    
    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(tabAll, "All"));
        tabBreakfast.setOnClickListener(v -> selectTab(tabBreakfast, "Breakfast"));
        tabLunch.setOnClickListener(v -> selectTab(tabLunch, "Lunch"));
        tabDinner.setOnClickListener(v -> selectTab(tabDinner, "Dinner"));
    }
    
    private void selectTab(TextView selectedTab, String category) {
        // Reset all tabs
        resetAllTabs();
        
        // Set selected tab style
        selectedTab.setBackgroundColor(getResources().getColor(R.color.orange));
        selectedTab.setTextColor(getResources().getColor(R.color.white));
        
        // Filter food list based on category
        filterFoodList(category);
    }
    
    private void resetAllTabs() {
        TextView[] tabs = {tabAll, tabBreakfast, tabLunch, tabDinner};
        for (TextView tab : tabs) {
            tab.setBackgroundColor(getResources().getColor(R.color.light_gray));
            tab.setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }
    
    private void filterFoodList(String category) {
        List<Food> filteredList = new ArrayList<>();
        
        if (category.equals("All")) {
            filteredList.addAll(foodList);
        } else {
            for (Food food : foodList) {
                if (food.getCategory().equals(category)) {
                    filteredList.add(food);
                }
            }
        }
        
        foodAdapter = new FoodAdapter(this, filteredList);
        recyclerFoodList.setAdapter(foodAdapter);
        updateTotalItems(filteredList.size());
    }
    
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void updateTotalItems() {
        updateTotalItems(foodList.size());
    }
    
    private void updateTotalItems(int count) {
        String totalText = String.format("Total %02d items", count);
        tvTotalItems.setText(totalText);
    }
}
