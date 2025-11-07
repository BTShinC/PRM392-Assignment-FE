package com.example.prm392_assignment_food.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.prm392_assignment_food.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

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

        // Disable save button as per request
        btnSave.setVisibility(View.GONE);
        ivBack.setOnClickListener(v -> finish());

        loadProfileFromToken();
    }

    private void loadProfileFromToken() {
        SharedPreferences prefs = getSharedPreferences("FoodAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("AUTH_TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);

            // Get claims from token based on the logcat output
            String name = jwt.getClaim("name").asString();
            String email = jwt.getClaim("username").asString(); // Email is in 'username' claim

            // Phone and address are not in the token, so we leave them empty
            String phoneNumber = "";
            String address = "";

            // Populate the UI
            etFullName.setText(name);
            etEmail.setText(email);
            etPhoneNumber.setText(phoneNumber);
            etBio.setText(address);

            // Fields should not be editable
            etFullName.setEnabled(false);
            etEmail.setEnabled(false);
            etPhoneNumber.setEnabled(false);
            etBio.setEnabled(false);

        } catch (Exception e){
            Log.e(TAG, "JWT Decode Error: " + e.getMessage());
            Toast.makeText(this, "Failed to read user information.", Toast.LENGTH_SHORT).show();
        }
    }
}
