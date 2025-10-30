package com.example.prm392_assignment_food.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// import com.example.prm392_assignment_food.data.model.ApiResponse; // Dòng này không cần thiết nữa
import com.example.prm392_assignment_food.data.model.VnPayCreateRequest;
import com.example.prm392_assignment_food.data.repository.PaymentRepository;
import com.example.prm392_assignment_food.data.model.VnPayCreateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel extends ViewModel {
    private final PaymentRepository paymentRepository;
    private final MutableLiveData<String> paymentUrl = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public PaymentViewModel() {
        this.paymentRepository = new PaymentRepository();
    }

    public LiveData<String> getPaymentUrl() {
        return paymentUrl;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void createVnPayPayment(String orderId, long amount, String orderDescription, String bankCode) {
        isLoading.postValue(true);
        VnPayCreateRequest request = new VnPayCreateRequest(orderId, amount, orderDescription, bankCode);

        // Sửa kiểu dữ liệu trong Callback để khớp với Repository và ApiService
        paymentRepository.createVnPayPayment(request).enqueue(new Callback<VnPayCreateResponse>() {
            @Override
            public void onResponse(Call<VnPayCreateResponse> call, Response<VnPayCreateResponse> response) {
                isLoading.postValue(false);

                // Đơn giản hóa toàn bộ logic xử lý response
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy URL trực tiếp từ body(), không cần qua apiResponse.getData()
                    String url = response.body().getPaymentUrl();
                    paymentUrl.postValue(url);
                } else {
                    errorMessage.postValue("Không thể tạo thanh toán. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VnPayCreateResponse> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }
}