package com.example.prm392_assignment_food.ui.chat;


import android.os.Bundle; import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;


import com.example.prm392_assignment_food.data.model.ApiResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ApiService apiService;
    private UUID currentUserId;
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private TextView tvEmptyNotifications;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        initViews();
        apiService = ApiClient.getAuthenticatedApiService();

        // Lấy User ID
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        String userIdStr = JwtUtils.getUserId(token);

        if (userIdStr == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        currentUserId = UUID.fromString(userIdStr);

        setupRecyclerView();
        loadNotifications();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(this, new ArrayList<>());
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void loadNotifications() {

        apiService.getNotifications(currentUserId).enqueue(new Callback<ApiResponse<List<NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationResponse>>> call, Response<ApiResponse<List<NotificationResponse>>> response) {
                // Kiểm tra xem cuộc gọi có thành công và body có tồn tại không
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                    // Lấy danh sách notification từ bên trong trường "data"
                    List<NotificationResponse> notifications = response.body().getData();

                    if (notifications.isEmpty()) {
                        recyclerViewNotifications.setVisibility(View.GONE);
                        tvEmptyNotifications.setVisibility(View.VISIBLE);
                    } else {
                        recyclerViewNotifications.setVisibility(View.VISIBLE);
                        tvEmptyNotifications.setVisibility(View.GONE);
                        notificationAdapter.updateData(notifications);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to load notifications (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    recyclerViewNotifications.setVisibility(View.GONE);
                    tvEmptyNotifications.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationResponse>>> call, Throwable t) {
                // Xử lý lỗi mạng
                Toast.makeText(NotificationActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerViewNotifications.setVisibility(View.GONE);
                tvEmptyNotifications.setVisibility(View.VISIBLE);
            }
        });
    }}