package com.example.prm392_assignment_food.ui.Billing;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentSuccessActivity extends AppCompatActivity {

    private Button btnTrackOrder;
    private RecyclerView recyclerView;
    private List<OrderItem> orderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);

        orderItems = new ArrayList<>();

        // 🔟 Mẫu dữ liệu món ăn (dùng ảnh pizza1.png)
        orderItems.add(new OrderItem(R.drawable.pizza1, "Pizza Calzone European", 64, "14″", 2));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Pepperoni Pizza", 58, "12″", 1));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Hawaiian Pizza", 60, "14″", 3));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Veggie Pizza", 52, "13″", 1));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Seafood Pizza", 72, "14″", 2));
        orderItems.add(new OrderItem(R.drawable.pizza1, "BBQ Chicken Pizza", 66, "15″", 1));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Cheese Lover’s Pizza", 55, "12″", 2));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Italian Pizza", 68, "16″", 1));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Bacon Deluxe Pizza", 61, "14″", 3));
        orderItems.add(new OrderItem(R.drawable.pizza1, "Spicy Beef Pizza", 63, "13″", 2));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderItemAdapter(orderItems));

        btnTrackOrder.setOnClickListener(v ->
                Toast.makeText(this, "Tracking order...", Toast.LENGTH_SHORT).show()
        );
    }
}
