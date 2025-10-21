package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminDashboardFragment())
                    .commit();
        }
    }
}
