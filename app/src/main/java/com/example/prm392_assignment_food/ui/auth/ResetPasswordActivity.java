package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ResetPasswordRequest;
import com.example.prm392_assignment_food.data.model.ResetPasswordResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etOtp, etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ImageButton btnBack;
    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Get email from ForgotPasswordActivity
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        btnBack.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPasswordReset();
            }
        });
    }

    private void performPasswordReset() {
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "An error occurred. Please try again from the beginning.", Toast.LENGTH_LONG).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(userEmail, newPassword, otp);
        Call<ResetPasswordResponse> call = apiService.resetPassword(request);

        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("200".equals(response.body().getStatus())) {
                        Toast.makeText(ResetPasswordActivity.this, "Password has been reset successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to reset password. Please check OTP.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
