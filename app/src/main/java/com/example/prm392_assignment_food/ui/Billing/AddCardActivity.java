package com.example.prm392_assignment_food.ui.Billing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.MainActivity;
import com.example.prm392_assignment_food.R;

public class AddCardActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etCardHolderName, etCardNumber, etExpireDate, etCVC;
    private Button btnAddCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        btnBack = findViewById(R.id.btnBack);
        etCardHolderName = findViewById(R.id.etCardHolderName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpireDate = findViewById(R.id.etExpireDate);
        etCVC = findViewById(R.id.etCVC);
        btnAddCard = findViewById(R.id.btnAddCard);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnAddCard.setOnClickListener(v -> {
            if (etCardHolderName.getText().toString().isEmpty() ||
                    etCardNumber.getText().toString().isEmpty() ||
                    etExpireDate.getText().toString().isEmpty() ||
                    etCVC.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
