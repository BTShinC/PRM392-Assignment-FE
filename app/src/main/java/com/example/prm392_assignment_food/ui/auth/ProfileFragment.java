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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView ivBack;
    private CircleImageView profileImage;
    private EditText etFullName, etEmail, etPhoneNumber, etBio;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivBack = rootView.findViewById(R.id.ivBack);
        profileImage = rootView.findViewById(R.id.profile_image);
        etFullName = rootView.findViewById(R.id.etFullName);
        etEmail = rootView.findViewById(R.id.etEmail);
        etPhoneNumber = rootView.findViewById(R.id.etPhoneNumber);
        etBio = rootView.findViewById(R.id.etBio);

        // Hide back button as navigation is handled by the main activity
        ivBack.setVisibility(View.GONE);

        loadProfileFromToken();
    }

    private void loadProfileFromToken() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("FoodAppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("AUTH_TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);

            String name = jwt.getClaim("name").asString();
            String email = jwt.getClaim("username").asString();
            String phoneNumber = ""; // Not in token
            String address = ""; // Not in token

            etFullName.setText(name);
            etEmail.setText(email);
            etPhoneNumber.setText(phoneNumber);
            etBio.setText(address);

            etFullName.setEnabled(false);
            etEmail.setEnabled(false);
            etPhoneNumber.setEnabled(false);
            etBio.setEnabled(false);

        } catch (Exception e) {
            Log.e(TAG, "JWT Decode Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Failed to read user information.", Toast.LENGTH_SHORT).show();
        }
    }
}
