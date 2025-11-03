package com.example.prm392_assignment_food.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.ui.auth.LoginActivity;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProfileActivity extends AppCompatActivity {

    private static final String TAG = "AdminProfileActivity";

    private TextView tvBalanceAmount;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        tvBalanceAmount = findViewById(R.id.tv_balance_amount);
        apiService = ApiClient.getApiService();

        // Setup Menu Items
        setupMenuItem(R.id.item_personal_info, R.drawable.ic_personal_info, "Thông tin cá nhân", null);
        setupMenuItem(R.id.item_settings, R.drawable.ic_settings, "Cài đặt", null);
        setupMenuItem(R.id.item_number_of_orders, R.drawable.ic_number_of_orders, "Số lượng đơn hàng", "...");
        setupMenuItem(R.id.item_user_reviews, R.drawable.ic_user_reviews, "Đánh giá của người dùng", null);

        LinearLayout logoutItem = findViewById(R.id.item_logout);
        setupMenuItem(logoutItem, R.drawable.ic_logout, "Đăng xuất", null);

        logoutItem.setOnClickListener(v -> logout());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAdminWalletBalance();
        fetchTotalOrderCount();
    }

    private void fetchAdminWalletBalance() {
        apiService.getAdminWallet().enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double balance = response.body();
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvBalanceAmount.setText(currencyFormatter.format(balance));
                } else {
                    Log.e(TAG, "Failed to fetch wallet balance. Code: " + response.code());
                    Toast.makeText(AdminProfileActivity.this, "Không thể tải số dư ví.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Log.e(TAG, "Error fetching wallet balance", t);
                Toast.makeText(AdminProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTotalOrderCount() {
        // We only need the total count, so we can request a single item to be efficient.
        apiService.getOrders(0, 1, Collections.singletonList("createdAt,DESC"), null, null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    long totalOrders = response.body().getData().getTotalElements();
                    View orderItemView = findViewById(R.id.item_number_of_orders);
                    TextView tvValue = orderItemView.findViewById(R.id.tv_value);
                    tvValue.setText(String.valueOf(totalOrders));
                } else {
                    Log.e(TAG, "Failed to fetch order count. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                Log.e(TAG, "Error fetching order count", t);
            }
        });
    }


    private void setupMenuItem(int viewId, int iconResId, String title, String value) {
        View menuItem = findViewById(viewId);
        setupMenuItem(menuItem, iconResId, title, value);
    }

    private void setupMenuItem(View menuItem, int iconResId, String title, String value) {
        ImageView icon = menuItem.findViewById(R.id.iv_icon);
        TextView tvTitle = menuItem.findViewById(R.id.tv_title);
        TextView tvValue = menuItem.findViewById(R.id.tv_value);
        ImageView arrow = menuItem.findViewById(R.id.iv_arrow);

        icon.setImageResource(iconResId);
        tvTitle.setText(title);

        if (value != null) {
            tvValue.setText(value);
            tvValue.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.GONE);
        } else {
            tvValue.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
        }

        icon.setImageTintList(null);

        if (title.equals("Đăng xuất")) {
            tvTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("FoodAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("AUTH_TOKEN");
        editor.remove("USER_EMAIL");
        editor.remove("LOGIN_TIMESTAMP");
        editor.apply();

        Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
