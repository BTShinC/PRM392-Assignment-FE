package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.RegisterRequest;
import com.example.prm392_assignment_food.data.model.RegisterResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etRetypePassword, etPhone, etAddress, etRole;
    private Button btnSignUp;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etRole = findViewById(R.id.etRole);
        btnSignUp = findViewById(R.id.btnSignUp);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOtp();
            }
        });
    }

    private void requestOtp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String retypePassword = etRetypePassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(retypePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // The OTP field is null here as we are requesting it
        RegisterRequest registerRequest = new RegisterRequest(email, password, name, phone, address, role, null);
        Call<RegisterResponse> call = apiService.requestRegistrationOtp(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    // Assuming "200" means OTP was sent successfully
                    if ("200".equals(registerResponse.getStatus())) { 
                        Toast.makeText(RegisterActivity.this, "OTP sent to your email!", Toast.LENGTH_SHORT).show();
                        
                        // Start VerificationActivity and pass the registration data
                        Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                        intent.putExtra("register_request", registerRequest);
                        startActivity(intent);

                    } else {
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to send OTP!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
