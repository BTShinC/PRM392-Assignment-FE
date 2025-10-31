package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrdersActivity extends AppCompatActivity {

    private static final String TAG = "AdminOrdersActivity";
    private ApiService apiService;
    private RecyclerView recyclerView;
    private RunningOrderAdapter adapter;
    private List<RunningOrder> runningOrderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        apiService = ApiClient.getApiService();
        recyclerView = findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass context to adapter for Glide
        adapter = new RunningOrderAdapter(this, runningOrderList);
        recyclerView.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        apiService.getOrders(0, 20, null, null, null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<OrderResponse> pageResponse = response.body().getData();
                    if (pageResponse != null && pageResponse.getContent() != null) {
                        List<OrderResponse> orderResponses = pageResponse.getContent();
                        runningOrderList.clear();
                        for (OrderResponse orderResponse : orderResponses) {
                            if (orderResponse != null && orderResponse.orderItems != null && !orderResponse.orderItems.isEmpty()) {
                                // For this activity, we don't need to fetch full item details yet.
                                // We can enhance this later if needed.
                                String name = orderResponse.orderItems.get(0).menuItemId; // Placeholder
                                runningOrderList.add(new RunningOrder(
                                        orderResponse.orderId,
                                        "Category", // Placeholder
                                        name,
                                        "$" + orderResponse.totalPrice,
                                        null // No image URL needed here for now
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "PageResponse or its content is null");
                        Toast.makeText(AdminOrdersActivity.this, "No orders found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to fetch orders, code: " + response.code());
                    Toast.makeText(AdminOrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                Log.e(TAG, "Error fetching orders", t);
                Toast.makeText(AdminOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}