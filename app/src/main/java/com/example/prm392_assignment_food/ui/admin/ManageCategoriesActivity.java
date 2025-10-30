package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryRequest;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCategoriesActivity extends AppCompatActivity {

    private static final String TAG = "ManageCategories";
    private static final int UPDATE_CATEGORY_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<MenuCategoryResponse> categoryList = new ArrayList<>();
    private ApiService apiService;
    private FloatingActionButton fabAddCategory;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        apiService = ApiClient.getApiService();
        recyclerView = findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        fabAddCategory = findViewById(R.id.fab_add_category);
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(int position) {
                MenuCategoryResponse category = categoryList.get(position);
                Intent intent = new Intent(ManageCategoriesActivity.this, UpdateCategoryActivity.class);
                intent.putExtra("categoryId", category.getCategoryId());
                intent.putExtra("name", category.getName());
                intent.putExtra("description", category.getDescription());
                startActivityForResult(intent, UPDATE_CATEGORY_REQUEST_CODE);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteCategory(position);
            }
        });

        fetchCategories();
    }

    private void fetchCategories() {
        apiService.getMenuCategories(0, 100, "name,asc", null).enqueue(new Callback<PageResponse<MenuCategoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuCategoryResponse>> call, Response<PageResponse<MenuCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getContent());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageCategoriesActivity.this, "Tải danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuCategoryResponse>> call, Throwable t) {
                Toast.makeText(ManageCategoriesActivity.this, "Lỗi khi tải danh mục: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(int position) {
        MenuCategoryResponse category = categoryList.get(position);
        apiService.deleteMenuCategory(category.getCategoryId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageCategoriesActivity.this, "Xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                    categoryList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(ManageCategoriesActivity.this, "Xóa danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageCategoriesActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        final EditText etCategoryName = dialogView.findViewById(R.id.et_dialog_category_name);
        final EditText etCategoryDescription = dialogView.findViewById(R.id.et_dialog_category_description);

        builder.setTitle("Thêm danh mục mới");
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etCategoryName.getText().toString().trim();
            String description = etCategoryDescription.getText().toString().trim();
            if (!name.isEmpty()) {
                addCategory(name, description);
            } else {
                Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addCategory(String name, String description) {
        MenuCategoryRequest request = new MenuCategoryRequest(name, description);
        apiService.addMenuCategory(request).enqueue(new Callback<MenuCategoryResponse>() {
            @Override
            public void onResponse(Call<MenuCategoryResponse> call, Response<MenuCategoryResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageCategoriesActivity.this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    fetchCategories(); // Refresh the list
                } else {
                    Toast.makeText(ManageCategoriesActivity.this, "Thêm danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MenuCategoryResponse> call, Throwable t) {
                Toast.makeText(ManageCategoriesActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchCategories(); // Refresh the list after an update
        }
    }
}
