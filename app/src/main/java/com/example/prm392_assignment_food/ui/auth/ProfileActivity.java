package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CircleImageView profileImage;
    private EditText etFullName, etEmail, etPhoneNumber, etBio;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivBack = findViewById(R.id.ivBack);
        profileImage = findViewById(R.id.profile_image);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBio = findViewById(R.id.etBio);
        btnSave = findViewById(R.id.btnSave);

        SharedPreferences prefs = getSharedPreferences("FoodAppPrefs", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "");

        etEmail.setText(email);

        ivBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            // TODO: Handle saving profile information
        });
    }
}
