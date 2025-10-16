package com.example.prm392_assignment_food.ui.Billing;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_assignment_food.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    private Button btnTrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setOnClickListener(v ->
                Toast.makeText(this, "Tracking order...", Toast.LENGTH_SHORT).show()
        );
    }
}

