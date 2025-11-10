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

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;
import com.example.prm392_assignment_food.ui.customer.CustomerMainActivity;
import com.example.prm392_assignment_food.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.example.prm392_assignment_food.ui.chat.NotificationType;
import com.example.prm392_assignment_food.ui.Billing.NotificationHelper;

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
            Intent trackOrderIntent = new Intent(this, CustomerMainActivity.class);
            trackOrderIntent.putExtra("NAVIGATE_TO", "ORDER_FRAGMENT");
            trackOrderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(trackOrderIntent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, CustomerMainActivity.class);
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

        String notificationTitle = "";
        String notificationMessage = "";
        NotificationType notificationType = null;

        if (data != null && "prm392food".equals(data.getScheme())) {
            String status = data.getQueryParameter("status");
            this.orderId = data.getQueryParameter("orderId");

            if ("success".equals(status)) {
                updateUiForSuccess();
                notificationTitle = "Thanh toán thành công!";
                notificationMessage = "Đã nhận thanh toán VNPAY cho đơn #" + this.orderId;
                notificationType = NotificationType.ORDER_PAID;
            } else {
                updateUiForFailure();
                notificationTitle = "Thanh toán thất bại";
                notificationMessage = "Thanh toán VNPAY cho đơn #" + this.orderId + " đã thất bại.";
                notificationType = NotificationType.ORDER_PAYMENT_FAILED;
            }

            if(notificationType != null) {
                NotificationHelper.showNotification(
                        getApplicationContext(),
                        notificationType,
                        notificationTitle,
                        notificationMessage
                );
            }
        } else {
            this.orderId = intent.getStringExtra(Constants.EXTRA_ORDER_ID);
            updateUiForSuccess();
        }
    }

    private void updateUiForSuccess() {
        ivStatusIcon.setImageResource(R.drawable.ic_payment_success);
        llHeader.setBackgroundResource(R.drawable.bg_success_header);
        tvPaymentStatus.setText("Thanh Toán Thành Công");
        tvOrderId.setText("Bạn đã thanh toán thành công,\nhãy tận hưởng dịch vụ của chúng tôi!");
        btnTrackOrder.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String json = prefs.getString(orderId, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItemResponse>>(){}.getType();
            List<CartItemResponse> items = gson.fromJson(json, type);
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewOrders.setAdapter(new PaymentSuccessAdapter(items));
            prefs.edit().remove(orderId).apply();
        } else {
            Log.e("PaymentSuccess", "Không tìm thấy dữ liệu trong SharedPreferences cho orderId=" + orderId);
        }
    }

    private void updateUiForFailure() {
        ivStatusIcon.setImageResource(R.drawable.ic_payment_failure);
        llHeader.setBackgroundResource(R.drawable.bg_success_header); // Maybe a different color for failure?
        tvPaymentStatus.setText("Thanh toán thất bại!");
        tvOrderId.setText("Đã có lỗi xảy ra với thanh toán của bạn.\nVui lòng thử lại.");
        btnTrackOrder.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnBackToHome.getLayoutParams();
        params.weight = 2.0f;
        params.setMarginEnd(0);
        btnBackToHome.setLayoutParams(params);

        SharedPreferences prefs = getSharedPreferences("PendingOrders", MODE_PRIVATE);
        String json = prefs.getString(orderId, null);
        if (json != null) {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItemResponse>>(){}.getType();
            List<CartItemResponse> items = gson.fromJson(json, type);
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewOrders.setAdapter(new PaymentSuccessAdapter(items));
        } else {
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }
}
