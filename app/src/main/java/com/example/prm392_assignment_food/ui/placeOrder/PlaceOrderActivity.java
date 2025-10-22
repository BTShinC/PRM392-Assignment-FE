package com.example.prm392_assignment_food.ui.placeOrder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

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

    private ApiService apiService;
    private TokenManager tokenManager;

    private final BigDecimal DELIVERY_FEE = new BigDecimal("2.00"); // Phí ship tạm tính

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        initViews();
        initApi();
        setupRecyclerView();
        setupListeners();

        loadCartData();
    }

    private void initViews() {
        orderItemsRecyclerView = findViewById(R.id.cartRecyclerView);
        tvProductsTotal = findViewById(R.id.textViewProductTotal);
        tvDeliveryFee = findViewById(R.id.textViewDeliveryFee);
        tvTotalPayment = findViewById(R.id.textViewTotalPayment);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
    }

    private void initApi() {
        ApiClient.init(this);
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter MỘT LẦN DUY NHẤT với danh sách rỗng
        adapter = new PlaceOrderAdapter(new ArrayList<>());
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemsRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
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

    private void updateUiWithCartData(CartResponse cart) {
        // --- ĐÃ SỬA ---
        // Cập nhật danh sách sản phẩm thông qua phương thức của adapter
        adapter.updateItems(cart.getItems());
        // --- KẾT THÚC SỬA ---

        // Cập nhật thông tin thanh toán
        BigDecimal productsTotal = cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO;
        BigDecimal totalPayment = productsTotal.add(DELIVERY_FEE);

        tvProductsTotal.setText(String.format(Locale.US, "$%.2f", productsTotal));
        tvDeliveryFee.setText(String.format(Locale.US, "$%.2f", DELIVERY_FEE));
        tvTotalPayment.setText(String.format(Locale.US, "$%.2f", totalPayment));
    }
}
