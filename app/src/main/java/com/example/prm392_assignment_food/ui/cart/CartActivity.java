package com.example.prm392_assignment_food.ui.cart;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;
import com.example.prm392_assignment_food.data.model.CartResponse;
import com.example.prm392_assignment_food.data.model.UpdateQuantityRequest;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemInteractionListener {

    private static final String TAG = "CartActivity";

    // Views
    private RecyclerView cartRecyclerView;
    private TextView textViewTotalPrice, textViewEditAddress, textViewAddress, textViewEditItems;
    private ProgressBar progressBar;
    private ImageView backButton;
    private Button buttonPlaceOrder, buttonDeleteSelected;

    // API & Data
    private ApiService apiService;
    private TokenManager tokenManager;
    private CartAdapter cartAdapter;
    private String currentUserId;
    private boolean isInEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        initApi();
        setupRecyclerView();
        setupListeners();
        loadInitialCartData();
    }

    private void initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        textViewEditAddress = findViewById(R.id.textViewEditAddress);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewEditItems = findViewById(R.id.textViewEditItems);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
        buttonDeleteSelected = findViewById(R.id.buttonDeleteSelected);
    }

    private void initApi() {
        ApiClient.init(this);
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new ArrayList<>(), this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        textViewEditAddress.setOnClickListener(v -> showEditAddressDialog());
        textViewEditItems.setOnClickListener(v -> toggleEditMode());
        buttonDeleteSelected.setOnClickListener(v -> deleteSelectedItems());
    }

    private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        cartAdapter.setEditMode(isInEditMode);

        if (isInEditMode) {
            textViewEditItems.setText("CANCEL");
            buttonPlaceOrder.setVisibility(View.GONE);
            buttonDeleteSelected.setVisibility(View.VISIBLE);
            onSelectionChanged(0); // Cập nhật text nút xóa lần đầu
        } else {
            textViewEditItems.setText("EDIT ITEMS");
            buttonPlaceOrder.setVisibility(View.VISIBLE);
            buttonDeleteSelected.setVisibility(View.GONE);
        }
    }

    private void deleteSelectedItems() {
        Set<CartItemResponse> selectedItems = cartAdapter.getSelectedItems();
        if (currentUserId == null || selectedItems.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa " + selectedItems.size() + " sản phẩm đã chọn?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);

                    final AtomicInteger successCount = new AtomicInteger(0);
                    final int totalItems = selectedItems.size();

                    for (CartItemResponse item : selectedItems) {
                        apiService.removeCartItem(currentUserId, item.getMenuItemId().toString()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Đã xóa thành công item: " + item.getMenuItemId());
                                } else {
                                    Log.e(TAG, "Lỗi xóa item: " + item.getMenuItemId() + " - Code: " + response.code());
                                }
                                // Kiểm tra nếu đã xử lý xong tất cả item
                                if (successCount.incrementAndGet() == totalItems) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(CartActivity.this, "Đã hoàn tất thao tác xóa.", Toast.LENGTH_SHORT).show();
                                        toggleEditMode(); // Thoát chế độ edit
                                        loadInitialCartData(); // Tải lại toàn bộ giỏ hàng
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, "Lỗi kết nối khi xóa item: " + item.getMenuItemId(), t);
                                if (successCount.incrementAndGet() == totalItems) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(CartActivity.this, "Đã có lỗi xảy ra trong quá trình xóa.", Toast.LENGTH_SHORT).show();
                                        toggleEditMode();
                                        loadInitialCartData();
                                    });
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSelectionChanged(int selectedSize) {
        if (selectedSize > 0) {
            buttonDeleteSelected.setText(String.format(Locale.US, "DELETE (%d)", selectedSize));
            buttonDeleteSelected.setEnabled(true);
        } else {
            buttonDeleteSelected.setText("DELETE");
            buttonDeleteSelected.setEnabled(false);
        }
    }
    
    // ... (Các phương thức còn lại: loadInitialCartData, onIncreaseQuantity, ... giữ nguyên) ...
    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Delivery Address");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(textViewAddress.getText());
        input.setPadding(50, 50, 50, 50);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                textViewAddress.setText(newAddress);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadInitialCartData() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
            return;
        }

        this.currentUserId = JwtUtils.getUserId(token);
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);

        apiService.getCart(currentUserId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUiWithCartData(response.body());
                } else {
                    handleApiError("Lấy giỏ hàng thất bại", response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    @Override
    public void onIncreaseQuantity(CartItemResponse item) {
        updateItemQuantity(item, item.getQuantity() + 1);
    }

    @Override
    public void onDecreaseQuantity(CartItemResponse item) {
        updateItemQuantity(item, item.getQuantity() - 1);
    }

    @Override
    public void onRemoveItem(CartItemResponse item) {
        updateItemQuantity(item, 0);
    }

    private void updateItemQuantity(CartItemResponse item, int newQuantity) {
        if (currentUserId == null) {
            Toast.makeText(this, "Không tìm thấy User ID. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID menuItemId = item.getMenuItemId();
        if (menuItemId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID của sản phẩm.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        UpdateQuantityRequest request = new UpdateQuantityRequest(newQuantity);

        apiService.updateItem(currentUserId, menuItemId.toString(), request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CartActivity.this, "Giỏ hàng đã được cập nhật", Toast.LENGTH_SHORT).show();
                    updateUiWithCartData(response.body());
                } else {
                    handleApiError("Cập nhật thất bại", response.code());
                    loadInitialCartData();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                handleApiFailure(t);
                loadInitialCartData();
            }
        });
    }

    private void updateUiWithCartData(CartResponse cart) {
        progressBar.setVisibility(View.GONE);
        cartRecyclerView.setVisibility(View.VISIBLE);

        cartAdapter.updateItems(cart.getItems());

        BigDecimal totalPrice = cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO;
        textViewTotalPrice.setText(String.format(Locale.US, "$%.2f", totalPrice));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleApiError(String message, int code) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message + ". Code: " + code, Toast.LENGTH_LONG).show();
        Log.e(TAG, "API call failed with code: " + code);
    }

    private void handleApiFailure(Throwable t) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(TAG, "API call failed on failure: ", t);
    }
}
