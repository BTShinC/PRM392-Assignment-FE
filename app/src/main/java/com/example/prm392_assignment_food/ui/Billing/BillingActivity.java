package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;

public class BillingActivity extends AppCompatActivity {

    private LinearLayout btnCash, btnVisa, btnMastercard, btnPaypal;
    private TextView tvTotal;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        btnCash = findViewById(R.id.btnCash);
        btnVisa = findViewById(R.id.btnVisa);
        btnMastercard = findViewById(R.id.btnMastercard);
        btnPaypal = findViewById(R.id.btnPaypal);
        tvTotal = findViewById(R.id.tvTotal);
        btnBack = findViewById(R.id.btnBack);

        tvTotal.setText("TOTAL: $96");

        btnBack.setOnClickListener(v -> onBackPressed());

        // chọn phương thức thanh toán
        btnCash.setOnClickListener(v -> goToSuccess());
        btnPaypal.setOnClickListener(v -> goToSuccess());

        btnVisa.setOnClickListener(v -> openCardList("Visa"));
        btnMastercard.setOnClickListener(v -> openCardList("Mastercard"));
    }

    private void openCardList(String cardType) {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("cardType", cardType);
        startActivity(intent);
    }

    private void goToSuccess() {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }
}
