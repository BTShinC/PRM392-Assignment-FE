// File: com/example/prm392_assignment_food/ui/Billing/PaymentSuccessActivity.java

package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.content.SharedPreferences; // <<<--- THÊM IMPORT
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // <<<--- THÊM IMPORT
import androidx.recyclerview.widget.RecyclerView; // <<<--- THÊM IMPORT

import com.example.prm392_assignment_food.MainActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse; // <<<--- THÊM IMPORT
import com.example.prm392_assignment_food.ui.location.TrackOrderActivity;
import com.example.prm392_assignment_food.ui.placeOrder.PlaceOrderAdapter; // <<<--- SỬ DỤNG ADAPTER TỪ MÀN HÌNH TRƯỚC
import com.example.prm392_assignment_food.utils.Constants;
import com.google.gson.Gson; // <<<--- THÊM IMPORT
import com.google.gson.reflect.TypeToken; // <<<--- THÊM IMPORT

import java.lang.reflect.Type; // <<<--- THÊM IMPORT
import java.util.ArrayList; // <<<--- THÊM IMPORT
import java.util.List; // <<<--- THÊM IMPORT

public class PaymentSuccessActivity extends AppCompatActivity {

    private Button btnTrackOrder, btnBackToHome;
    private TextView tvPaymentStatus, tvOrderId;
    private RecyclerView recyclerViewOrders; // <<<--- THÊM BIẾN NÀY
    private String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Ánh xạ View (dựa trên ID chính xác trong file XML của bạn)
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvOrderId = findViewById(R.id.tvOrderId);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders); // <<<--- ÁNH XẠ RECYCLERVIEW

        // Xử lý Intent khi Activity được tạo
        handleIntent(getIntent());

        btnTrackOrder.setOnClickListener(v -> {
            if (orderId != null && !orderId.isEmpty()) {
                Intent trackIntent = new Intent(this, TrackOrderActivity.class);
                trackIntent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                startActivity(trackIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy mã đơn hàng.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();

        // Trường hợp 1: Mở bằng Deep Link
        if (data != null && "prm392food".equals(data.getScheme())) {
            String status = data.getQueryParameter("status");
            this.orderId = data.getQueryParameter("orderId");

            if ("success".equals(status)) {
                updateUiForSuccess();
            } else {
                updateUiForFailure();
            }
        }
        // Trường hợp 2: Mở bình thường (thanh toán tiền mặt)
        else {
            this.orderId = intent.getStringExtra(Constants.EXTRA_ORDER_ID);
            updateUiForSuccess();
        }
    }

    private void updateUiForSuccess() {
        // Cập nhật text dựa trên layout của bạn
        tvPaymentStatus.setText("Congratulations!");
        tvOrderId.setText("You successfully made a payment for order #" + this.orderId);
        btnTrackOrder.setVisibility(View.VISIBLE);

        // <<<--- THÊM PHẦN NÀY VÀO CUỐI HÀM --- >>>
        SharedPreferences prefs = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String json = prefs.getString(orderId, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItemResponse>>(){}.getType();
            List<CartItemResponse> items = gson.fromJson(json, type);

            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewOrders.setAdapter(new PlaceOrderAdapter(items));

            Log.d("PaymentSuccess", "Đọc được " + items.size() + " món cho orderId: " + orderId);

            // Xóa cache sau khi hiển thị xong để tránh lưu cũ
            prefs.edit().remove(orderId).apply();
        } else {
            Log.e("PaymentSuccess", "Không tìm thấy dữ liệu trong SharedPreferences cho orderId=" + orderId);
        }
    }


    private void updateUiForFailure() {
        tvPaymentStatus.setText("Payment Failed!");
        tvOrderId.setText("There was an issue with your order #" + this.orderId);
        btnTrackOrder.setVisibility(View.GONE);
    }

    // <<<--- HÀM MỚI ĐƯỢC THÊM VÀO ĐỂ ĐỌC VÀ HIỂN THỊ DỮ LIỆU --- >>>
    private void displayOrderItemsFromCache(String orderId) {
        if (orderId == null || orderId.isEmpty()) return;

        SharedPreferences sharedPreferences = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString(orderId, null);

        if (cartJson != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CartItemResponse>>(){}.getType();
            List<CartItemResponse> cartItems = gson.fromJson(cartJson, listType);

            if (cartItems != null && !cartItems.isEmpty()) {
                // Tái sử dụng PlaceOrderAdapter để hiển thị
                PlaceOrderAdapter adapter = new PlaceOrderAdapter(cartItems);
                recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewOrders.setAdapter(adapter);
            }

            // Xóa dữ liệu tạm sau khi đã dùng để tránh làm đầy bộ nhớ
            sharedPreferences.edit().remove(orderId).apply();
        }
    }
}