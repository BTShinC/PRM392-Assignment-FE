package com.example.prm392_assignment_food.data.repository;

import com.example.prm392_assignment_food.data.model.ApiResponse;
import com.example.prm392_assignment_food.data.model.CreateOrderRequest;
import com.example.prm392_assignment_food.data.model.CreateOrderResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import retrofit2.Call;

public class OrderRepository {
    private ApiService apiService;

    public OrderRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public Call<ApiResponse<CreateOrderResponse>> createOrder(CreateOrderRequest request) {
        return apiService.createOrder(request);
    }

}
