package com.example.prm392_assignment_food.ui.Billing;

import com.example.prm392_assignment_food.data.model.ApiResponse;
import com.example.prm392_assignment_food.data.model.CreateOrderRequest;
import com.example.prm392_assignment_food.data.model.CreateOrderResponse;
import com.example.prm392_assignment_food.data.model.OrderItemRequest;
import com.example.prm392_assignment_food.utils.Constants;
import com.example.prm392_assignment_food.data.model.PaymentMethod;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;
import com.example.prm392_assignment_food.data.model.VnPayCreateRequest;
import com.example.prm392_assignment_food.data.model.VnPayCreateResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillingActivity extends AppCompatActivity {


    private LinearLayout btnCash, btnVnPay;
    private Button btnPayConfirm;
    private ProgressBar progressBar;
    private TextView tvTotal;
    private ImageView btnBack;

    private double totalAmountInVND = 0.0;
    private String realOrderId = "";
    private PaymentMethod selectedPaymentMethod = null;

    private List<CartItemResponse> cartItems;

    private TokenManager tokenManager;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        btnCash = findViewById(R.id.btnCash);
        btnVnPay = findViewById(R.id.btnVnPay);
        btnPayConfirm = findViewById(R.id.btnPayConfirm);
        progressBar = findViewById(R.id.progress_bar_billing);
        tvTotal = findViewById(R.id.tvTotal);
        btnBack = findViewById(R.id.btnBack);
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getApiService();


        getIntentData();
        setupListeners();
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            totalAmountInVND = extras.getDouble(Constants.EXTRA_TOTAL_AMOUNT, 0.0);
            realOrderId = extras.getString(Constants.EXTRA_ORDER_ID, "");
            Serializable items = extras.getSerializable(Constants.EXTRA_CART_ITEMS);
            if (items instanceof List<?>) {
                cartItems = (List<CartItemResponse>) items;
                Log.d("BillingActivity", "Nhận " + cartItems.size() + " món từ intent");
            }
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText("TỔNG CỘNG: " + currencyFormatter.format(totalAmountInVND));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCash.setOnClickListener(v -> selectPaymentMethod(PaymentMethod.COD));
        btnVnPay.setOnClickListener(v -> selectPaymentMethod(PaymentMethod.VNPAY));
        btnPayConfirm.setOnClickListener(v -> {
            if (selectedPaymentMethod == null) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod == PaymentMethod.COD) {
                createCashOrder();
            } else if (selectedPaymentMethod == PaymentMethod.VNPAY) {
                createVnPayPaymentDirectly();
            }

        });
    }

    private void selectPaymentMethod(PaymentMethod method) {
        selectedPaymentMethod = method;
        btnCash.setBackgroundResource(method == PaymentMethod.COD ? R.drawable.bg_payment_selected : R.drawable.bg_payment_unselected);
        btnVnPay.setBackgroundResource(method == PaymentMethod.VNPAY ? R.drawable.bg_payment_selected : R.drawable.bg_payment_unselected);
    }

    private void createCashOrder() {
        String token = tokenManager.getToken();
        String userIdStr = JwtUtils.getUserId(token);
        if (userIdStr == null) {
            Toast.makeText(this, "Không thể xác thực người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }
        UUID userId = UUID.fromString(userIdStr);

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào để thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🧱 Chuyển CartItemResponse → OrderItemRequest
        List<OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItemResponse item : cartItems) {
            if (item.getMenuItemId() != null) {
                orderItems.add(new OrderItemRequest(
                        item.getMenuItemId(),
                        item.getQuantity(),
                        item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO
                ));
            }
        }

        // 🧠 Tạo body request
        CreateOrderRequest request = new CreateOrderRequest(userId, PaymentMethod.COD, orderItems);



        progressBar.setVisibility(View.VISIBLE);
        btnPayConfirm.setEnabled(false);

        apiService.createOrder(request).enqueue(new Callback<ApiResponse<CreateOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CreateOrderResponse>> call, Response<ApiResponse<CreateOrderResponse>> response) {
                progressBar.setVisibility(View.GONE);
                btnPayConfirm.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CreateOrderResponse orderResponse = response.body().getData();
                    realOrderId = orderResponse.getOrderId();

                    Log.d("BillingActivity", "✅ Order Cash tạo thành công: " + realOrderId);

                    saveCartForLater(realOrderId, cartItems);

                    Intent intent = new Intent(BillingActivity.this, PaymentSuccessActivity.class);
                    intent.putExtra(Constants.EXTRA_ORDER_ID, realOrderId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e("BillingActivity", "❌ Tạo đơn cash thất bại. Code: " + response.code());
                    Toast.makeText(BillingActivity.this, "Không thể tạo đơn hàng tiền mặt.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CreateOrderResponse>> call, Throwable t) {  // ✅ ĐÃ SỬA KIỂU Ở ĐÂY
                progressBar.setVisibility(View.GONE);
                btnPayConfirm.setEnabled(true);
                Log.e("BillingActivity", "💥 Lỗi mạng khi tạo order cash", t);
                Toast.makeText(BillingActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createVnPayPaymentDirectly() {
        progressBar.setVisibility(View.VISIBLE);
        btnPayConfirm.setEnabled(false);

        ApiService apiService = ApiClient.getApiService();
        long amountToSend = (long) totalAmountInVND;
        String orderDescription = "Thanh toan don hang #" + realOrderId;
        VnPayCreateRequest request = new VnPayCreateRequest(realOrderId, amountToSend, orderDescription, "");

        apiService.createVnPayPayment(request).enqueue(new Callback<VnPayCreateResponse>() {
            @Override
            public void onResponse(Call<VnPayCreateResponse> call, Response<VnPayCreateResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnPayConfirm.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().getPaymentUrl();
                    if (url != null && !url.isEmpty()) {
                        saveCartForLater(realOrderId, cartItems);
                        Log.d("BillingActivity_Direct", "SUCCESS! URL is: " + url);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } else {
                        Log.e("BillingActivity_Direct", "API Success but URL is null or empty.");
                        Toast.makeText(BillingActivity.this, "Không nhận được URL thanh toán.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("BillingActivity_Direct", "API call failed. Code: " + response.code());
                    Toast.makeText(BillingActivity.this, "Không thể tạo thanh toán. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VnPayCreateResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnPayConfirm.setEnabled(true);
                Log.e("BillingActivity_Direct", "Network failure: ", t);
                Toast.makeText(BillingActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void goToSuccess() {
        // 🔹 1. Lưu danh sách món ăn vào SharedPreferences trước
        saveCartForLater(realOrderId, cartItems);
        Log.d("BillingActivity", "Đã lưu giỏ hàng (cash) cho orderId: " + realOrderId);

        // 🔹 2. Điều hướng sang trang PaymentSuccessActivity
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra(Constants.EXTRA_ORDER_ID, realOrderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveCartForLater(String orderId, List<CartItemResponse> itemsToSave) {
        if (orderId == null || orderId.isEmpty() || itemsToSave == null || itemsToSave.isEmpty()) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String cartJson = gson.toJson(itemsToSave);
        editor.putString(orderId, cartJson);
        editor.apply();
        Log.d("BillingActivity", "Đã lưu giỏ hàng cho orderId: " + orderId);
    }

}