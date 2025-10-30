package com.example.prm392_assignment_food.ui.placeOrder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.model.CreateOrderRequest;
import com.example.prm392_assignment_food.data.model.OrderItemRequest;
import com.example.prm392_assignment_food.data.model.PaymentMethod;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.ui.Billing.BillingActivity;
import com.example.prm392_assignment_food.utils.Constants;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;
import com.example.prm392_assignment_food.viewmodel.OrderViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID; // Sử dụng đúng java.util.UUID
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends AppCompatActivity {

    private static final String TAG = "PlaceOrderActivity";


    private RecyclerView orderItemsRecyclerView;
    private PlaceOrderAdapter adapter;
    private TextView tvProductsTotal, tvDeliveryFee, tvTotalPayment;
    private ProgressBar progressBar;
    private ImageView backButton;
    private AppCompatButton buttonPlaceOrder;

    private OrderViewModel orderViewModel;
    private ApiService apiService;
    private TokenManager tokenManager;

    private List<CartItemResponse> currentCartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        initApi();
        setupRecyclerView();
        setupListeners();
        setupObservers();
        loadCartData();
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        buttonPlaceOrder.setOnClickListener(v -> {
            // Bước 1: Lấy User ID từ token
            String token = tokenManager.getToken();
            String userIdStr = JwtUtils.getUserId(token);
            if (userIdStr == null) {
                Toast.makeText(this, "Không thể xác thực người dùng.", Toast.LENGTH_SHORT).show();
                return;
            }
            UUID userId = UUID.fromString(userIdStr);

            // Bước 2: Thiết lập phương thức thanh toán
            PaymentMethod paymentMethod = PaymentMethod.VNPAY;

            // Bước 3: Chuyển đổi danh sách sản phẩm từ giỏ hàng
            if (currentCartItems == null || currentCartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống.", Toast.LENGTH_SHORT).show();
                return;
            }
            List<OrderItemRequest> orderItems = new ArrayList<>();
            for (CartItemResponse cartItem : currentCartItems) {
                // >>> SỬA LỖI 'fromString': Lấy thẳng UUID, không cần ép kiểu <<<
                // Giả sử getMenuItemId() trả về một đối tượng UUID
                UUID menuItemId = cartItem.getMenuItemId();

                if (menuItemId != null) {
                    // Lỗi 'Expected 3 arguments...' được giải quyết bằng cách sửa file OrderItemRequest.java
                    BigDecimal price = cartItem.getUnitPrice();
                    orderItems.add(new OrderItemRequest(menuItemId, cartItem.getQuantity(), price));
                } else {
                    Log.w(TAG, "Một sản phẩm trong giỏ hàng có menuItemId là null.");
                }
            }

            if (orderItems.isEmpty()) {
                Toast.makeText(this, "Không có sản phẩm hợp lệ trong giỏ hàng.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bước 4: Tạo đối tượng request hoàn chỉnh
            CreateOrderRequest request = new CreateOrderRequest(userId, paymentMethod, orderItems);

            // Bước 5: Gọi ViewModel
            orderViewModel.createOrder(request);
        });
    }

    private void updateUiWithCartData(CartResponse cart) {
        if (cart.getItems() != null) {
            this.currentCartItems = cart.getItems();
        } else {
            this.currentCartItems.clear();
        }

        adapter.updateItems(this.currentCartItems);

        BigDecimal productsTotal = cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO;
        BigDecimal deliveryFeeVND = new BigDecimal("20000");
        BigDecimal totalPayment = productsTotal.add(deliveryFeeVND);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvProductsTotal.setText(currencyFormatter.format(productsTotal));
        tvDeliveryFee.setText(currencyFormatter.format(deliveryFeeVND));
        tvTotalPayment.setText(currencyFormatter.format(totalPayment));
    }

    private void setupObservers() {
        orderViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            buttonPlaceOrder.setEnabled(!isLoading);
        });

        orderViewModel.getCreateOrderResult().observe(this, apiResponse -> {
            if (apiResponse == null) {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_LONG).show();
                return;
            }
            if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                String realOrderId = apiResponse.getData().getOrderId();
                double realTotalPrice = apiResponse.getData().getTotalPrice().doubleValue();

                // <<<--- CHỈ GIỮ LẠI MỘT LỜI GỌI DUY NHẤT NÀY --- >>>
                navigateToBilling(realOrderId, realTotalPrice, currentCartItems);

            } else {
                String message = "Không thể tạo đơn hàng.";
                if (apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                    message = apiResponse.getMessage();
                } else {
                    message += " Mã lỗi: " + apiResponse.getStatus();
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToBilling(String orderId, double totalAmount, List<CartItemResponse> cartItems) {
        Intent intent = new Intent(PlaceOrderActivity.this, BillingActivity.class);
        intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        intent.putExtra(Constants.EXTRA_TOTAL_AMOUNT, totalAmount);
        // Gửi kèm danh sách các món ăn
        intent.putExtra(Constants.EXTRA_CART_ITEMS, (Serializable) cartItems);
        startActivity(intent);
    }

    private void initViews() {
        orderItemsRecyclerView = findViewById(R.id.cartRecyclerView);
        tvProductsTotal = findViewById(R.id.textViewProductTotal);
        tvDeliveryFee = findViewById(R.id.textViewDeliveryFee);
        tvTotalPayment = findViewById(R.id.textViewTotalPayment);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
    }

    private void initApi() {
        ApiClient.init(this);
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getApiService();
    }

    private void setupRecyclerView() {
        adapter = new PlaceOrderAdapter(new ArrayList<>());
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemsRecyclerView.setAdapter(adapter);
    }

    private void loadCartData() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String userId = JwtUtils.getUserId(token);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateUiWithCartData(response.body());
                } else {
                    Toast.makeText(PlaceOrderActivity.this, "Lấy thông tin giỏ hàng thất bại. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API call failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PlaceOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed on failure: ", t);
            }
        });
    }
}
