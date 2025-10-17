package com.example.prm392_assignment_food.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity {
    
    private ImageView imgFoodDetail, btnBack;
    private TextView tvFoodNameDetail, tvPriceDetail, tvCategoryDetail, tvDeliveryType;
    private TextView tvLocationDetail, tvRatingDetail, tvReviewsDetail, tvDescription;
    private TextView btnEdit;
    private LinearLayout ingredientsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        
        initViews();
        loadDataFromIntent();
        setupClickListeners();
        setupIngredients();
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
        ingredientsContainer = findViewById(R.id.ingredients_container);
    }
    
    private void loadDataFromIntent() {
        // Get data from intent extras
        String foodName = getIntent().getStringExtra("food_name");
        String foodPrice = getIntent().getStringExtra("food_price");
        String foodCategory = getIntent().getStringExtra("food_category");
        float foodRating = getIntent().getFloatExtra("food_rating", 0.0f);
        int foodReviews = getIntent().getIntExtra("food_reviews", 0);
        int foodImage = getIntent().getIntExtra("food_image", R.drawable.onboarding1);
        String foodLocation = getIntent().getStringExtra("food_location");
        String foodDescription = getIntent().getStringExtra("food_description");
        String deliveryType = getIntent().getStringExtra("food_delivery_type");
        
        // Set data to views
        if (foodName != null) tvFoodNameDetail.setText(foodName);
        if (foodPrice != null) tvPriceDetail.setText(foodPrice);
        if (foodCategory != null) tvCategoryDetail.setText(foodCategory);
        if (foodLocation != null) tvLocationDetail.setText(foodLocation);
        if (foodDescription != null) tvDescription.setText(foodDescription);
        if (deliveryType != null) tvDeliveryType.setText(deliveryType);
        
        tvRatingDetail.setText(String.valueOf(foodRating));
        tvReviewsDetail.setText("(" + foodReviews + " Reviews)");
        imgFoodDetail.setImageResource(foodImage);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnEdit.setOnClickListener(v -> {
            // Handle edit action - could open edit screen or show edit dialog
            // For now, just show a toast or log
        });
    }
    
    private void setupIngredients() {
        // Get ingredients from intent or use default
        List<String> ingredients = Arrays.asList("Salt", "Chicken", "Onion", "Garlic", "Pappers", "Ginger", "Broccoli", "Orange", "Walnut");
        List<String> allergyIngredients = Arrays.asList("Onion", "Pappers");
        
        // Clear existing ingredients if any
        if (ingredientsContainer != null) {
            ingredientsContainer.removeAllViews();
            
            // Create rows of ingredients (5 per row)
            LinearLayout currentRow = null;
            for (int i = 0; i < ingredients.size(); i++) {
                if (i % 5 == 0) {
                    // Create new row
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    ingredientsContainer.addView(currentRow);
                }
                
                // Create ingredient item
                createIngredientItem(currentRow, ingredients.get(i), allergyIngredients.contains(ingredients.get(i)));
            }
        }
    }
    
    private void createIngredientItem(LinearLayout parent, String ingredientName, boolean hasAllergy) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout ingredientItem = (LinearLayout) inflater.inflate(R.layout.item_ingredient, parent, false);
        
        ImageView imgIngredient = ingredientItem.findViewById(R.id.img_ingredient);
        TextView tvIngredientName = ingredientItem.findViewById(R.id.tv_ingredient_name);
        TextView tvIngredientAllergy = ingredientItem.findViewById(R.id.tv_ingredient_allergy);
        
        // Set ingredient data
        imgIngredient.setImageResource(IngredientHelper.getIngredientImage(ingredientName));
        tvIngredientName.setText(ingredientName);
        
        if (hasAllergy) {
            tvIngredientAllergy.setVisibility(TextView.VISIBLE);
        } else {
            tvIngredientAllergy.setVisibility(TextView.GONE);
        }
        
        parent.addView(ingredientItem);
    }
}
