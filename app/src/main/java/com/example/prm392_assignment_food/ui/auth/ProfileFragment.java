package com.example.prm392_assignment_food.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.auth.User;
import com.example.prm392_assignment_food.data.model.auth.UserProfileResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView ivBack;
    private CircleImageView profileImage;
    private EditText etFullName, etEmail, etPhoneNumber, etBio;
    private View rootView;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();

        ivBack = rootView.findViewById(R.id.ivBack);
        profileImage = rootView.findViewById(R.id.profile_image);
        etFullName = rootView.findViewById(R.id.etFullName);
        etEmail = rootView.findViewById(R.id.etEmail);
        etPhoneNumber = rootView.findViewById(R.id.etPhoneNumber);
        etBio = rootView.findViewById(R.id.etBio);

        // Hide back button as navigation is handled by the main activity
        ivBack.setVisibility(View.GONE);

        loadProfile();
    }

    private void loadProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("FoodAppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("AUTH_TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);
            String userId = jwt.getClaim("userId").asString();

            apiService.getUserProfile(userId).enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        User user = response.body().getData();
                        etFullName.setText(user.getFullName());
                        etEmail.setText(user.getEmail());
                        etPhoneNumber.setText(user.getPhone());
                        etBio.setText(user.getAddress());

                        etFullName.setEnabled(false);
                        etEmail.setEnabled(false);
                        etPhoneNumber.setEnabled(false);
                        etBio.setEnabled(false);
                    } else {
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Log.e(TAG, "Profile load error: " + t.getMessage());
                    Toast.makeText(requireContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "JWT Decode Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Failed to read user information.", Toast.LENGTH_SHORT).show();
        }
    }
}
