package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryRequest;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCategoryActivity extends AppCompatActivity {

    private static final String TAG = "UpdateCategoryActivity";

    private EditText etCategoryName, etCategoryDescription;
    private Button btnSaveCategory;
    private ApiService apiService;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        apiService = ApiClient.getApiService();

        etCategoryName = findViewById(R.id.et_category_name_update);
        etCategoryDescription = findViewById(R.id.et_category_description_update);
        btnSaveCategory = findViewById(R.id.btn_save_category_update);

        // Get data from intent
        categoryId = getIntent().getStringExtra("categoryId");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");

        // Set initial values
        etCategoryName.setText(name);
        etCategoryDescription.setText(description);

        btnSaveCategory.setOnClickListener(v -> updateCategory());
    }

    private void updateCategory() {
        String name = etCategoryName.getText().toString().trim();
        String description = etCategoryDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        MenuCategoryRequest menuCategoryRequest = new MenuCategoryRequest(name, description);

        apiService.updateMenuCategory(categoryId, menuCategoryRequest).enqueue(new Callback<MenuCategoryResponse>() {
            @Override
            public void onResponse(Call<MenuCategoryResponse> call, Response<MenuCategoryResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateCategoryActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(UpdateCategoryActivity.this, "Update failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MenuCategoryResponse> call, Throwable t) {
                Toast.makeText(UpdateCategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
