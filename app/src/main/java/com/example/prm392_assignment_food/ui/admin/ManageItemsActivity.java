package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageItemsActivity extends AppCompatActivity {

    private static final int UPDATE_ITEM_REQUEST_CODE = 1;
    private static final int ADD_ITEM_REQUEST_CODE = 2;

    private RecyclerView recyclerView;
    private ItemManageAdapter adapter;
    private List<MenuItemResponse> itemList = new ArrayList<>();
    private ApiService apiService;
    private FloatingActionButton fabAddItem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        apiService = ApiClient.getApiService();
        recyclerView = findViewById(R.id.recycler_view_items);
        fabAddItem = findViewById(R.id.fab_add_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemManageAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(ManageItemsActivity.this, AddItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
        });

        adapter.setOnItemClickListener(new ItemManageAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(int position) {
                MenuItemResponse item = itemList.get(position);
                Intent intent = new Intent(ManageItemsActivity.this, UpdateItemActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, UPDATE_ITEM_REQUEST_CODE);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteItem(position);
            }
        });

        fetchItems();
    }

    private void fetchItems() {
        apiService.getMenuItems(0, 100, "name,asc", null, null).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList.clear();
                    itemList.addAll(response.body().getContent());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageItemsActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                Toast.makeText(ManageItemsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(int position) {
        MenuItemResponse item = itemList.get(position);
        apiService.deleteMenuItem(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageItemsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    itemList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(ManageItemsActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageItemsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == UPDATE_ITEM_REQUEST_CODE || requestCode == ADD_ITEM_REQUEST_CODE) && resultCode == RESULT_OK) {
            fetchItems(); // Refresh the list
        }
    }
}
