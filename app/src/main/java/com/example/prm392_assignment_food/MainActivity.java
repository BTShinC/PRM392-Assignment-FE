package com.example.prm392_assignment_food;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_assignment_food.ui.admin.AdminActivity;
import com.example.prm392_assignment_food.ui.admin.AddItemActivity;
import com.example.prm392_assignment_food.ui.auth.ProfileActivity;
import com.example.prm392_assignment_food.ui.cart.CartActivity;
import com.example.prm392_assignment_food.ui.location.AccessLocationActivity;
import com.example.prm392_assignment_food.ui.customer.FoodListActivity;
import com.example.prm392_assignment_food.ui.profile.AdminProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccessLocationActivity.class);
                startActivity(intent);
            }
        });

        Button btnFoodList = findViewById(R.id.btnFoodList);
        btnFoodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodListActivity.class);
                startActivity(intent);
            }
        });

        Button btnCartTest = findViewById(R.id.btnCartTest);
        btnCartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        Button btnAdminProfile = findViewById(R.id.btnAdminProfile);
        btnAdminProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminProfileActivity.class);
                startActivity(intent);
            }
        });

        Button btnTestProfile = findViewById(R.id.btnTestProfile);
        btnTestProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button btnTestAddItem = findViewById(R.id.btnTestAddItem);
        btnTestAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        Button btnTestAdmin = findViewById(R.id.btn_test_admin);
        btnTestAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }
}
