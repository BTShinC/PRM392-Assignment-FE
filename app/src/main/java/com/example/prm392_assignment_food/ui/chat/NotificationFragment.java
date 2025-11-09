package com.example.prm392_assignment_food.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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


public class NotificationFragment extends Fragment {

    private ApiService apiService;
    private UUID currentUserId;
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private TextView tvEmptyNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        tvEmptyNotifications = view.findViewById(R.id.tvEmptyNotifications);
        apiService = ApiClient.getAuthenticatedApiService();

        TokenManager tokenManager = new TokenManager(requireActivity());
        String token = tokenManager.getToken();
        String userIdStr = JwtUtils.getUserId(token);
        if (userIdStr != null) {
            currentUserId = UUID.fromString(userIdStr);
        } else {
            Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        setupRecyclerView();
        loadNotifications();
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(getContext(), new ArrayList<>());
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // <<< SỬA ĐỔI: "Lắng nghe" sự kiện click từ Adapter >>>
        notificationAdapter.setOnItemClickListener(notification -> {
            // Khi một item được click, gọi hàm hiển thị dialog
            showNotificationDetailDialog(notification);
        });
    }

    // Hàm loadNotifications của bạn giữ nguyên, không cần sửa đổi
    private void loadNotifications() {
        apiService.getNotifications(currentUserId).enqueue(new Callback<ApiResponse<List<NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationResponse>>> call, Response<ApiResponse<List<NotificationResponse>>> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
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
                        Toast.makeText(getActivity(), "Failed to load notifications (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                        recyclerViewNotifications.setVisibility(View.GONE);
                        tvEmptyNotifications.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<NotificationResponse>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerViewNotifications.setVisibility(View.GONE);
                    tvEmptyNotifications.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // <<< SỬA ĐỔI: Thêm hàm hoàn toàn mới này để hiển thị dialog >>>
    private void showNotificationDetailDialog(NotificationResponse notification) {
        if (getContext() == null) return; // Đảm bảo fragment còn tồn tại

        // Sử dụng LayoutInflater của Fragment để "thổi" layout dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notification_detail, null);

        // Ánh xạ các thành phần trong layout dialog
        TextView tvFullContent = dialogView.findViewById(R.id.tvFullNotificationContent);
        TextView tvTimestamp = dialogView.findViewById(R.id.tvNotificationTimestamp);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        // Đổ dữ liệu từ `notification` vào các View
        tvFullContent.setText(notification.getContent());
        if (notification.getCreatedAt() != null && !notification.getCreatedAt().isEmpty()) {
            tvTimestamp.setText(notification.getCreatedAt().replace("T", " ").substring(0, 16));
        }

        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Xử lý sự kiện nhấn nút 'X' để đóng dialog
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Thiết lập nền trong suốt để bo góc của CardView được hiển thị
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Hiển thị dialog lên màn hình
        dialog.show();
    }
}