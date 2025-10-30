package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.MenuItemRequest;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddItemActivity";

    private EditText etItemName, etPrice, etDescription;
    private ImageView ivMainImage;
    private Button btnSaveItem;
    private LinearLayout btnAddPhoto;
    private Spinner spinnerCategory;
    private SwitchCompat switchIsAvailable;

    private Uri imageUri;
    private ApiService apiService;
    private FirebaseStorage storage;
    private List<MenuCategoryResponse> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        storage = FirebaseStorage.getInstance();
        apiService = ApiClient.getApiService();

        // Initialize views
        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        ivMainImage = findViewById(R.id.iv_main_image);
        btnSaveItem = findViewById(R.id.btn_save_item);
        btnAddPhoto = findViewById(R.id.btn_add_photo);
        spinnerCategory = findViewById(R.id.spinner_category);
        switchIsAvailable = findViewById(R.id.switch_is_available);
        
        // Setup Spinner
        setupCategorySpinner();

        // Set listeners
        btnAddPhoto.setOnClickListener(v -> openFileChooser());
        btnSaveItem.setOnClickListener(v -> uploadImageAndSaveData());
        
        // Fetch categories from API
        fetchCategories();
    }

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }
    
    private void fetchCategories() {
        apiService.getMenuCategories(0, 100, "name,asc", null).enqueue(new Callback<PageResponse<MenuCategoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuCategoryResponse>> call, Response<PageResponse<MenuCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body().getContent();
                    List<String> categoryNames = new ArrayList<>();
                    for (MenuCategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }
                    categoryAdapter.clear();
                    categoryAdapter.addAll(categoryNames);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuCategoryResponse>> call, Throwable t) {
                Toast.makeText(AddItemActivity.this, "Error fetching categories: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivMainImage.setImageURI(imageUri);
        }
    }

    private void uploadImageAndSaveData() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = storage.getReference().child("product_images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Image upload successful: " + uri.toString());
                    saveDataToApi(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: ", e);
                    Toast.makeText(AddItemActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveDataToApi(String imageUrl) {
        // Validate inputs
        String itemName = etItemName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

        if (itemName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categoryList.size()) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get data from views
        String categoryId = categoryList.get(selectedCategoryPosition).getCategoryId();
        double price = Double.parseDouble(priceStr);
        boolean isAvailable = switchIsAvailable.isChecked();

        // *** THÊM LOG ĐỂ KIỂM TRA GIÁ TRỊ CỦA CATEGORYID ***
        Log.d(TAG, "Attempting to create request with Category ID: " + categoryId);

        // Create request object
        MenuItemRequest menuItemRequest = new MenuItemRequest(categoryId, itemName, description, price, imageUrl, isAvailable);

        // Make API call
        apiService.addMenuItem(menuItemRequest).enqueue(new Callback<MenuItemResponse>() {
            @Override
            public void onResponse(Call<MenuItemResponse> call, Response<MenuItemResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous activity
                } else {
                    String errorMsg = "Failed to add product. Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ", " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(AddItemActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API Error: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<MenuItemResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: ", t);
                Toast.makeText(AddItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
