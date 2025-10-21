package com.example.prm392_assignment_food.ui.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemRequest;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.data.repository.FoodRepository;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    private static final String TAG = "FoodDetailActivity";

    // Views cũ
    private ImageView imgFoodDetail, btnBack;
    private TextView tvFoodNameDetail, tvPriceDetail, tvCategoryDetail, tvDeliveryType;
    private TextView tvLocationDetail, tvRatingDetail, tvReviewsDetail, tvDescription;
    private TextView btnEdit;
    private LinearLayout ingredientsContainer;

    // Thêm vào: Views cho chức năng giỏ hàng
    private Button btnAddToCart;
    private ImageView btnIncrease, btnDecrease;
    private TextView tvQuantity;

    // Data & Repo cũ
    private FoodRepository foodRepository;

    // Thêm vào: Các thành phần cho API và quản lý token
    private ApiService apiService;
    private TokenManager tokenManager;

    // Thêm vào: Biến để lưu trạng thái
    private String currentMenuItemId;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodRepository = new FoodRepository();

        // Thêm vào: Khởi tạo các thành phần API
        ApiClient.init(this);
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

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
        ingredientsContainer = findViewById(R.id.ingredients_container);

        // Thêm vào: Ánh xạ các view của chức năng giỏ hàng
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnIncrease = findViewById(R.id.btn_increase);
        btnDecrease = findViewById(R.id.btn_decrease);
        tvQuantity = findViewById(R.id.tv_quantity);

        // Thêm vào: Set số lượng ban đầu
        tvQuantity.setText(String.valueOf(quantity));
    }

    private void loadDataFromIntent() {
        // Thêm vào: Lưu menuItemId vào biến thành viên
        this.currentMenuItemId = getIntent().getStringExtra("menu_item_id");

        if (currentMenuItemId != null && !currentMenuItemId.isEmpty()) {
            loadMenuItemFromApi(currentMenuItemId);
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
                    if (menuItem.getPrice() != null) tvPriceDetail.setText("$" + menuItem.getPrice());
                    if (menuItem.getCategoryId() != null) tvCategoryDetail.setText(menuItem.getCategoryName());
                    if (menuItem.getDescription() != null) tvDescription.setText(menuItem.getDescription());

                    tvLocationDetail.setText("Restaurant Location");
                    tvDeliveryType.setText("Delivery Available");
                    tvRatingDetail.setText("4.5");
                    tvReviewsDetail.setText("(120 Reviews)");
                    imgFoodDetail.setImageResource(R.drawable.onboarding1);

                    setupIngredients();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FoodDetailActivity.this, "Error loading food details: " + errorMessage, Toast.LENGTH_SHORT).show();
                    loadDataFromIntentExtras();
                });
            }
        });
    }

    private void loadDataFromIntentExtras() {
        String foodName = getIntent().getStringExtra("food_name");
        String foodPrice = getIntent().getStringExtra("food_price");
        String foodCategory = getIntent().getStringExtra("food_category");
        float foodRating = getIntent().getFloatExtra("food_rating", 0.0f);
        int foodReviews = getIntent().getIntExtra("food_reviews", 0);
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

        tvRatingDetail.setText(String.valueOf(foodRating));
        tvReviewsDetail.setText("(" + foodReviews + " Reviews)");
        imgFoodDetail.setImageResource(foodImage);

        setupIngredients();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {});

        // Thêm vào: Các listener cho chức năng giỏ hàng
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> addItemToCart());
    }

    // Thêm vào: Phương thức mới để gọi API thêm vào giỏ hàng
    private void addItemToCart() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = JwtUtils.getUserId(token);
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Lỗi: userId không hợp lệ hoặc chưa đăng nhập. userId=" + userId);
            Toast.makeText(this, "Vui lòng đăng nhập trước khi thêm sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentMenuItemId == null || currentMenuItemId.isEmpty()) {
            Log.e(TAG, "Lỗi: menuItemId không hợp lệ. menuItemId=" + currentMenuItemId);
            Toast.makeText(this, "Lỗi: Không xác định được sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đang thêm vào giỏ hàng...", Toast.LENGTH_SHORT).show();

        CartItemRequest request = new CartItemRequest(UUID.fromString(currentMenuItemId), quantity);

        apiService.addItem(userId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FoodDetailActivity.this, "Đã thêm sản phẩm vào giỏ hàng!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Thêm thất bại. Code: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "addItem failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "addItem failure: ", t);
            }
        });
    }

    private void setupIngredients() {
        List<String> ingredients = Arrays.asList("Salt", "Chicken", "Onion", "Garlic", "Pappers", "Ginger", "Broccoli", "Orange", "Walnut");
        List<String> allergyIngredients = Arrays.asList("Onion", "Pappers");

        if (ingredientsContainer != null) {
            ingredientsContainer.removeAllViews();

            LinearLayout currentRow = null;
            for (int i = 0; i < ingredients.size(); i++) {
                if (i % 5 == 0) {
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    ingredientsContainer.addView(currentRow);
                }
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
