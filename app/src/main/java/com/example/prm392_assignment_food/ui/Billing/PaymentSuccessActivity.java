// File: com/example/prm392_assignment_food/ui/Billing/PaymentSuccessActivity.java

package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.MainActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;
import com.example.prm392_assignment_food.ui.location.TrackOrderActivity;
import com.example.prm392_assignment_food.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PaymentSuccessActivity extends AppCompatActivity {

    private Button btnTrackOrder, btnBackToHome;
    private TextView tvPaymentStatus, tvOrderId;
    private RecyclerView recyclerViewOrders;
    private String orderId = "";

    private ImageView ivStatusIcon;
    private LinearLayout llHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Ánh xạ View
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvOrderId = findViewById(R.id.tvOrderId);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);

        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        llHeader = findViewById(R.id.llHeader);

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
        // Cập nhật UI
        ivStatusIcon.setImageResource(R.drawable.ic_payment_success);
        llHeader.setBackgroundResource(R.drawable.bg_success_header);

        tvPaymentStatus.setText("Congratulations!");
        tvOrderId.setText("You successfully made a payment,\nenjoy our service!");
        btnTrackOrder.setVisibility(View.VISIBLE);

        // <<<--- SỬ DỤNG PaymentSuccessAdapter MỚI --- >>>
        SharedPreferences prefs = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String json = prefs.getString(orderId, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItemResponse>>(){}.getType();
            List<CartItemResponse> items = gson.fromJson(json, type);

            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            // SỬ DỤNG ADAPTER MỚI ĐẸP HƠN
            recyclerViewOrders.setAdapter(new PaymentSuccessAdapter(items));

            Log.d("PaymentSuccess", "Đọc được " + items.size() + " món cho orderId: " + orderId);

            // Xóa cache sau khi hiển thị xong
            prefs.edit().remove(orderId).apply();
        } else {
            Log.e("PaymentSuccess", "Không tìm thấy dữ liệu trong SharedPreferences cho orderId=" + orderId);
        }
    }

    private void updateUiForFailure() {
        ivStatusIcon.setImageResource(R.drawable.ic_payment_failure);
        llHeader.setBackgroundResource(R.drawable.bg_success_header);

        tvPaymentStatus.setText("Payment Failed!");
        tvOrderId.setText("There was an issue with your payment.\nPlease try again.");

        btnTrackOrder.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnBackToHome.getLayoutParams();
        // Đặt trọng số của nó bằng tổng trọng số để nó chiếm toàn bộ không gian
        params.weight = 2.0f;
        // Bỏ margin cuối vì không còn button nào bên cạnh
        params.setMarginEnd(0);
        // Áp dụng lại các tham số mới
        btnBackToHome.setLayoutParams(params);


        // Hiển thị order details nếu có (phần này giữ nguyên)
        SharedPreferences prefs = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String json = prefs.getString(orderId, null);

        if (json != null) {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItemResponse>>(){}.getType();
            List<CartItemResponse> items = gson.fromJson(json, type);

            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            // Giả sử bạn có PaymentSuccessAdapter
            recyclerViewOrders.setAdapter(new PaymentSuccessAdapter(items));
        } else {
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }
}