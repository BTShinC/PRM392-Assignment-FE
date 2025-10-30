package com.example.prm392_assignment_food.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm392_assignment_food.data.model.ApiResponse;
import com.example.prm392_assignment_food.data.model.CreateOrderRequest;
import com.example.prm392_assignment_food.data.model.CreateOrderResponse;
import com.example.prm392_assignment_food.data.repository.OrderRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {
    private final OrderRepository orderRepository = new OrderRepository();
    private final MutableLiveData<ApiResponse<CreateOrderResponse>> createOrderResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<ApiResponse<CreateOrderResponse>> getCreateOrderResult() {
        return createOrderResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void createOrder(CreateOrderRequest request) {
        isLoading.setValue(true);
        orderRepository.createOrder(request).enqueue(new Callback<ApiResponse<CreateOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CreateOrderResponse>> call, Response<ApiResponse<CreateOrderResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    createOrderResult.setValue(response.body());
                } else {
                    ApiResponse<CreateOrderResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setStatus(response.code());
                    String errorMessage = "Lỗi " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) { /* Bỏ qua */ }
                    errorResponse.setMessage(errorMessage);
                    createOrderResult.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CreateOrderResponse>> call, Throwable t) {
                isLoading.setValue(false);
                ApiResponse<CreateOrderResponse> failureResponse = new ApiResponse<>();
                failureResponse.setStatus(500);
                failureResponse.setMessage("Lỗi kết nối mạng: " + t.getMessage());
                createOrderResult.setValue(failureResponse);
            }
        });
    }

}
