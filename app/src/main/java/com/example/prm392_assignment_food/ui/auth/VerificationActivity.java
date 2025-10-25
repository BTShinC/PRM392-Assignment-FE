package com.example.prm392_assignment_food.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class VerificationActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;
    private Button btnVerify;
    private ImageButton btnBack;
    private TextView tvEmail, tvResend;
    private ApiService apiService;
    private RegisterRequest registerRequest;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        etCode5 = findViewById(R.id.etCode5);
        etCode6 = findViewById(R.id.etCode6);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
        tvEmail = findViewById(R.id.tvEmail);
        tvResend = findViewById(R.id.tvResend);

        apiService = ApiClient.getClient().create(ApiService.class);

        registerRequest = (RegisterRequest) getIntent().getSerializableExtra("register_request");

        if (registerRequest != null && registerRequest.getEmail() != null) {
            tvEmail.setText(registerRequest.getEmail());
        }

        setupOtpInputs();
        startResendTimer();

        btnVerify.setOnClickListener(v -> verifyOtpAndRegister());
        btnBack.setOnClickListener(v -> finish());
        tvResend.setOnClickListener(v -> {
            if (tvResend.getText().toString().equals("Resend")) {
                resendOtp();
            }
        });
    }

    private void startResendTimer() {
        tvResend.setClickable(false);
        countDownTimer = new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResend.setText("Resend in." + (millisUntilFinished / 1000) + "sec");
            }

            @Override
            public void onFinish() {
                tvResend.setText("Resend");
                tvResend.setClickable(true);
            }
        }.start();
    }

    private void resendOtp() {
        if (registerRequest == null) {
            Toast.makeText(this, "Registration data not found. Cannot resend OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the original, complete registerRequest object to resend the OTP
        Call<RegisterResponse> call = apiService.requestRegistrationOtp(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(VerificationActivity.this, "A new OTP has been sent.", Toast.LENGTH_SHORT).show();
                    startResendTimer(); // Restart the timer
                } else {
                    Toast.makeText(VerificationActivity.this, "Failed to resend OTP.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        etCode1.addTextChangedListener(new OtpTextWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new OtpTextWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new OtpTextWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new OtpTextWatcher(etCode4, etCode5));
        etCode5.addTextChangedListener(new OtpTextWatcher(etCode5, etCode6));
        etCode6.addTextChangedListener(new OtpTextWatcher(etCode6, null));
    }

    private void verifyOtpAndRegister() {
        if (registerRequest == null) {
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String otp1 = etCode1.getText().toString();
        String otp2 = etCode2.getText().toString();
        String otp3 = etCode3.getText().toString();
        String otp4 = etCode4.getText().toString();
        String otp5 = etCode5.getText().toString();
        String otp6 = etCode6.getText().toString();

        if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
            Toast.makeText(this, "Please enter the full 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        String otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
        registerRequest.setOtp(otp);

        Call<RegisterResponse> call = apiService.verifyAndRegister(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if ("200".equals(registerResponse.getStatus())) {
                        Toast.makeText(VerificationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerificationActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerificationActivity.this, "Verification failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class OtpTextWatcher implements TextWatcher {
        private View currentView;
        private View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
