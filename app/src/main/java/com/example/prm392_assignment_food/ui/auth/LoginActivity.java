package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.ui.customer.HomeActivity;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.auth.LoginRequest;
import com.example.prm392_assignment_food.data.model.auth.LoginResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private ApiService apiService;
    private TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);

        if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
            // Token exists and is not expired. Go straight to HomeActivity.
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            startActivity(intent);
            finish(); // Finish LoginActivity so user can't go back to it
            return; // Return to prevent the rest of the onCreate from executing
        } else if (tokenManager.getToken() != null) {
            // Token exists but is expired. Clear it.
            tokenManager.clear();
        }

        // If token is null or expired, proceed to show the login screen
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        apiService = ApiClient.getApiService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (Integer.parseInt(loginResponse.getStatus()) == 200) { // More robust status check

                        tokenManager.saveToken(loginResponse.getData(), email);

                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
