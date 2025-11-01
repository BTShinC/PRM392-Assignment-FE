package com.example.prm392_assignment_food.ui.order;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ResponseDto;
import com.example.prm392_assignment_food.data.model.order.OrderDto;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// --- BƯỚC 1: IMPLEMENT INTERFACE ---
public class OrderActivity extends AppCompatActivity implements View.OnClickListener, OrderAdapter.OrderInteractionListener {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<OrderDto> orderList = new ArrayList<>();
    private ApiService apiService;
    private TokenManager tokenManager;
    private String userId;
    private ProgressBar progressBar;

    private TextView tvAwaitingPayment, tvPaid, tvPaymentFailed, tvConfirmed, tvShipping, tvDelivered, tvCompleted, tvCancelled;
    private TextView currentStatusView;
    private String currentStatusString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(v -> finish());

        ApiClient.init(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        this.userId = JwtUtils.getUserId(token);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        progressBar = findViewById(R.id.progressBar);
        rvOrders = findViewById(R.id.rv_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // --- BƯỚC 2: CẬP NHẬT CONSTRUCTOR ---
        orderAdapter = new OrderAdapter(this, orderList, this);
        // -------------------------------------
        rvOrders.setAdapter(orderAdapter);

        initStatusTabs();

        loadOrdersByStatus("AWAITING_PAYMENT", tvAwaitingPayment);
    }

    private void initStatusTabs() {
        tvAwaitingPayment = findViewById(R.id.AWAITING_PAYMENT);
        tvPaid = findViewById(R.id.PAID);
        tvPaymentFailed = findViewById(R.id.PAYMENT_FAILED);
        tvConfirmed = findViewById(R.id.CONFIRMED);
        tvShipping = findViewById(R.id.SHIPPING);
        tvDelivered = findViewById(R.id.DELIVERED);
        tvCompleted = findViewById(R.id.COMPLETED);
        tvCancelled = findViewById(R.id.CANCELLED);

        tvAwaitingPayment.setOnClickListener(this);
        tvPaid.setOnClickListener(this);
        tvPaymentFailed.setOnClickListener(this);
        tvConfirmed.setOnClickListener(this);
        tvShipping.setOnClickListener(this);
        tvDelivered.setOnClickListener(this);
        tvCompleted.setOnClickListener(this);
        tvCancelled.setOnClickListener(this);
    }

    private void loadOrdersByStatus(String status, TextView statusView) {
        if (currentStatusView != null) {
            currentStatusView.setBackgroundResource(R.drawable.tab_background_unselected);
            currentStatusView.setTypeface(null, Typeface.NORMAL);
        }
        statusView.setBackgroundResource(R.drawable.tab_background_selected);
        statusView.setTypeface(null, Typeface.BOLD);
        currentStatusView = statusView;
        currentStatusString = status; // Lưu lại trạng thái hiện tại

        progressBar.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);

        apiService.getOrdersByStatus(userId, status).enqueue(new Callback<ResponseDto<List<OrderDto>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<OrderDto>>> call, Response<ResponseDto<List<OrderDto>>> response) {
                progressBar.setVisibility(View.GONE);
                rvOrders.setVisibility(View.VISIBLE);
                orderList.clear();

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess() && response.body().getData() != null) {
                        orderList.addAll(response.body().getData());
                    }
                } else {
                    Toast.makeText(OrderActivity.this, "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseDto<List<OrderDto>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                rvOrders.setVisibility(View.VISIBLE);
                orderList.clear();
                orderAdapter.notifyDataSetChanged();
                Toast.makeText(OrderActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API call failed", t);
            }
        });
    }

    // --- BƯỚC 3: TẠO PHƯƠNG THỨC onCancelOrder ---
    @Override
    public void onCancelOrder(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.updateOrderStatus(orderId, "CANCELLED").enqueue(new Callback<ResponseDto>() {
            @Override
            public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(OrderActivity.this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                    // Tải lại danh sách đơn hàng ở trạng thái hiện tại
                    loadOrdersByStatus(currentStatusString, currentStatusView);
                } else {
                    String errorMsg = "Có lỗi xảy ra, vui lòng thử lại.";
                    if(response.body() != null && response.body().getMessage() != null){
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(OrderActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ------------------------------------------------

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.AWAITING_PAYMENT) {
            loadOrdersByStatus("AWAITING_PAYMENT", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.PAID) {
            loadOrdersByStatus("PAID", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.PAYMENT_FAILED) {
            loadOrdersByStatus("PAYMENT_FAILED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.CONFIRMED) {
            loadOrdersByStatus("CONFIRMED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.SHIPPING) {
            loadOrdersByStatus("SHIPPING", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.DELIVERED) {
            loadOrdersByStatus("DELIVERED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.COMPLETED) {
            loadOrdersByStatus("COMPLETED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.CANCELLED) {
            loadOrdersByStatus("CANCELLED", (TextView) v);
            scrollTabToCenter((TextView) v);
        }
    }

    private void scrollTabToCenter(TextView selectedTab) {
        HorizontalScrollView scrollView = findViewById(R.id.tab_scroll);
        int scrollX = selectedTab.getLeft() + selectedTab.getWidth() / 2 - scrollView.getWidth() / 2;
        scrollView.smoothScrollTo(scrollX, 0);
    }
}
