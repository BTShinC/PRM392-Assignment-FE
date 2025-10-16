package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;

public class CardListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvCardName, tvCardHint, tvTotal;
    private Button btnAddNew, btnPayConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        btnBack = findViewById(R.id.btnBack);
        tvCardName = findViewById(R.id.tvCardName);
        tvCardHint = findViewById(R.id.tvCardHint);
        tvTotal = findViewById(R.id.tvTotal);
        btnAddNew = findViewById(R.id.btnAddNew);
        btnPayConfirm = findViewById(R.id.btnPayConfirm);

        tvTotal.setText("TOTAL: $96");

        String cardType = getIntent().getStringExtra("cardType");
        tvCardName.setText("No " + cardType + " card added");
        tvCardHint.setText("You can add a " + cardType + " card and save it for later");

        btnBack.setOnClickListener(v -> onBackPressed());
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCardActivity.class);
            intent.putExtra("cardType", cardType);
            startActivity(intent);
        });

        btnPayConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            startActivity(intent);
        });
    }
}
