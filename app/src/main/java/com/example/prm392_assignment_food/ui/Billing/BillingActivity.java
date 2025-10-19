package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;

public class BillingActivity extends AppCompatActivity {

    private LinearLayout btnCash, btnVnPay;
    private TextView tvTotal;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        btnCash = findViewById(R.id.btnCash);
        btnVnPay = findViewById(R.id.btnVnPay);
        tvTotal = findViewById(R.id.tvTotal);
        btnBack = findViewById(R.id.btnBack);

        tvTotal.setText("TOTAL: $96");

        btnBack.setOnClickListener(v -> onBackPressed());

        // 👉 Khi chọn thanh toán tiền mặt → đi thẳng đến trang thành công
        btnCash.setOnClickListener(v -> goToSuccess());

        // 👉 Khi chọn VNPay → chuyển qua danh sách thẻ
        btnVnPay.setOnClickListener(v -> openCardList("VNPay"));
    }

    /** Mở danh sách thẻ (CardListActivity) */
    private void openCardList(String cardType) {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("cardType", cardType);
        startActivity(intent);
    }

    /** Đi đến màn hình thanh toán thành công */
    private void goToSuccess() {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }
}
