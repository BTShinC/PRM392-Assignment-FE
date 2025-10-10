package com.example.prm392_assignment_food.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;

public class VerificationActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4;
    private Button btnVerify;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);

        btnVerify.setOnClickListener(v -> {
            String code = etCode1.getText().toString() + etCode2.getText().toString() + etCode3.getText().toString() + etCode4.getText().toString();

            if (code.length() == 4) {
                // Mock verification
                Toast.makeText(VerificationActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(VerificationActivity.this, "Please enter the full code", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
