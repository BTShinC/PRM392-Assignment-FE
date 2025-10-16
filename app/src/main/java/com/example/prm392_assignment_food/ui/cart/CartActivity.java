package com.example.prm392_assignment_food.ui.cart;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import java.util.List;
import java.util.Locale;

/**
 * CartActivity hiển thị giỏ hàng và xử lý các tương tác của người dùng.
 * Activity này implement OnCartChangeListener để lắng nghe các thay đổi từ Adapter.
 */
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems;
    private CartManager cartManager;
    private TextView textViewTotalPrice;
    private TextView textViewEditAddress;
    private TextView textViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Lấy instance của CartManager để quản lý dữ liệu giỏ hàng
        cartManager = CartManager.getInstance();

        // Ánh xạ các view từ layout XML
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice); // ID cho tổng tiền
        textViewEditAddress = findViewById(R.id.textViewEditAddress); // ID cho nút "EDIT" địa chỉ
        textViewAddress = findViewById(R.id.textViewAddress); // ID cho TextView hiển thị địa chỉ

        // ---- PHẦN NÀY DÙNG ĐỂ GIẢ LẬP VIỆC THÊM MÓN ĂN ----
        // Trong ứng dụng thực tế, bạn sẽ thêm món ăn từ một màn hình khác.
        if (cartManager.getCartItems().isEmpty()) {
            addSampleItems();
        }
        // ---------------------------------------------------

        // Lấy danh sách sản phẩm từ CartManager
        cartItems = cartManager.getCartItems();

        // Cấu hình RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo Adapter, truyền "this" vì Activity này đã implement listener
        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Gán sự kiện click cho nút "EDIT" địa chỉ
        textViewEditAddress.setOnClickListener(v -> showEditAddressDialog());

        // Cập nhật tổng tiền lần đầu khi mở màn hình
        updateTotalPrice();
    }

    /**
     * Phương thức này được gọi lại từ Adapter mỗi khi giỏ hàng có thay đổi (tăng/giảm số lượng).
     */
    @Override
    public void onCartChanged() {
        updateTotalPrice();
    }

    /**
     * Cập nhật hiển thị tổng tiền trên giao diện.
     */
    private void updateTotalPrice() {
        int totalPrice = cartManager.calculateTotalPrice();
        textViewTotalPrice.setText(String.format(Locale.US, "$%d", totalPrice));
    }

    /**
     * Hiển thị một hộp thoại (AlertDialog) để người dùng nhập địa chỉ mới.
     */
    private void showEditAddressDialog() {
        // Tạo một trình xây dựng Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Delivery Address");

        // Tạo một EditText để người dùng nhập liệu
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Lấy địa chỉ hiện tại và điền sẵn vào EditText
        input.setText(textViewAddress.getText());
        builder.setView(input);

        // Thiết lập nút "OK"
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            // Chỉ cập nhật nếu người dùng có nhập địa chỉ mới
            if (!newAddress.isEmpty()) {
                textViewAddress.setText(newAddress);
            }
        });
        // Thiết lập nút "Cancel"
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Hiển thị Dialog
        builder.show();
    }

    /**
     * Phương thức giả lập thêm sản phẩm vào giỏ để test.
     */
    private void addSampleItems() {
        Cart item1 = new Cart("Pizza Calzone European", "14\"", 64, R.drawable.pizza, "10-20 min", 1);
        cartManager.addItem(item1);

        Cart item2 = new Cart("Pizza Pepperoni", "12\"", 32, R.drawable.pizza, "10-15 min", 1);
        cartManager.addItem(item2);
    }
}

