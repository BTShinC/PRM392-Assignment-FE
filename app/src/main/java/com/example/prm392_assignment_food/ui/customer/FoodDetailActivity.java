package com.example.prm392_assignment_food.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.repository.FoodRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity {
    
    private ImageView imgFoodDetail, btnBack;
    private TextView tvFoodNameDetail, tvPriceDetail, tvCategoryDetail, tvDeliveryType;
    private TextView tvLocationDetail, tvRatingDetail, tvReviewsDetail, tvDescription;
    private TextView btnEdit;
    private LinearLayout ingredientsContainer;
    
    private FoodRepository foodRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        
        foodRepository = new FoodRepository();
        initViews();
        loadDataFromIntent();
        setupClickListeners();
    }
    
    private void initViews() {
        imgFoodDetail = findViewById(R.id.img_food_detail);
        btnBack = findViewById(R.id.btn_back);
        tvFoodNameDetail = findViewById(R.id.tv_food_name_detail);
        tvPriceDetail = findViewById(R.id.tv_price_detail);
        tvCategoryDetail = findViewById(R.id.tv_category_detail);
        tvDeliveryType = findViewById(R.id.tv_delivery_type);
        tvLocationDetail = findViewById(R.id.tv_location_detail);
        tvRatingDetail = findViewById(R.id.tv_rating_detail);
        tvReviewsDetail = findViewById(R.id.tv_reviews_detail);
        tvDescription = findViewById(R.id.tv_description);
        btnEdit = findViewById(R.id.btn_edit);
    }
    
    private void loadDataFromIntent() {
        // Get menu item ID from intent
        String menuItemId = getIntent().getStringExtra("menu_item_id");
        
        if (menuItemId != null && !menuItemId.isEmpty()) {
            loadMenuItemFromApi(menuItemId);
        } else {
            loadDataFromIntentExtras();
        }
    }
    
    private void loadMenuItemFromApi(String menuItemId) {
        foodRepository.getMenuItemById(menuItemId, new FoodRepository.RepositoryCallback<MenuItemResponse>() {
            @Override
            public void onSuccess(MenuItemResponse menuItem) {
                runOnUiThread(() -> {
                    if (menuItem.getName() != null) tvFoodNameDetail.setText(menuItem.getName());
                    if (menuItem.getPrice() != null) tvPriceDetail.setText(menuItem.getFormattedPrice());
                    if (menuItem.getCategoryId() != null) tvCategoryDetail.setText(menuItem.getCategoryName());
                    if (menuItem.getDescription() != null) tvDescription.setText(menuItem.getDescription());
                    

                    tvLocationDetail.setText("Restaurant Location");
                    tvDeliveryType.setText("Delivery Available");
                    tvRatingDetail.setText("");
                    tvReviewsDetail.setText("(120 Reviews)");
                    imgFoodDetail.setImageResource(R.drawable.onboarding1);
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FoodDetailActivity.this, "Error loading food details: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadDataFromIntentExtras() {
        String foodName = getIntent().getStringExtra("food_name");
        String foodPrice = getIntent().getStringExtra("food_price");
        String foodCategory = getIntent().getStringExtra("food_category");
        int foodImage = getIntent().getIntExtra("food_image", R.drawable.onboarding1);
        String foodLocation = getIntent().getStringExtra("food_location");
        String foodDescription = getIntent().getStringExtra("food_description");
        String deliveryType = getIntent().getStringExtra("food_delivery_type");
        
        if (foodName != null) tvFoodNameDetail.setText(foodName);
        if (foodPrice != null) tvPriceDetail.setText(foodPrice);
        if (foodCategory != null) tvCategoryDetail.setText(foodCategory);
        if (foodLocation != null) tvLocationDetail.setText(foodLocation);
        if (foodDescription != null) tvDescription.setText(foodDescription);
        if (deliveryType != null) tvDeliveryType.setText(deliveryType);

        imgFoodDetail.setImageResource(foodImage);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnEdit.setOnClickListener(v -> {
        });
    }
}
