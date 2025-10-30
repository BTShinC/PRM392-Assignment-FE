package com.example.prm392_assignment_food.data.repository;

import com.example.prm392_assignment_food.data.model.ApiResponse;
import com.example.prm392_assignment_food.data.model.VnPayCreateRequest;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.data.model.VnPayCreateResponse;

import retrofit2.Call;

public class PaymentRepository {
    private ApiService apiService;

    public PaymentRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public Call<VnPayCreateResponse> createVnPayPayment(VnPayCreateRequest request) { return apiService.createVnPayPayment(request); }
}