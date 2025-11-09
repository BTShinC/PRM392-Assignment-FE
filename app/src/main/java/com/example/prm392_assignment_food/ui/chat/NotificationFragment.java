package com.example.prm392_assignment_food.ui.chat;

import android.os.Bundle;
import android.util.Log;
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

        notificationAdapter.setOnItemClickListener(notification -> {

            // 1. Hiển thị dialog (Code này giờ đã đúng vì hàm đã được dời ra ngoài)
            showNotificationDetailDialog(notification);

            // 2. Gọi API để đánh dấu đã đọc
            if (notification.getStatus() != null && notification.getStatus().equals("UNREAD")) {
                if (notification.getNotificationId() != null) {
                    markNotificationAsRead(notification.getNotificationId().toString());
                }
            }
        });
    }

    private void markNotificationAsRead(String notificationId) {

        String newStatus = "READ";

        apiService.updateNotificationStatus(notificationId, newStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationFragment", "Đã cập nhật trạng thái thông báo " + notificationId + " thành READ");

                    // Code này giờ đã đúng vì hàm đã được dời ra ngoài
                    loadNotifications();
                } else {
                    Log.e("NotificationFragment", "Lỗi khi cập nhật thông báo. Code: " + response.code());
                    if (getContext() != null) { // Thêm kiểm tra getContext()
                        Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // <<< SỬA LỖI: THÊM HÀM 'onFailure' BỊ THIẾU >>>
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NotificationFragment", "Lỗi mạng khi cập nhật thông báo", t);
                if (getContext() != null) { // Thêm kiểm tra getContext()
                    Toast.makeText(getContext(), "Lỗi mạng, không thể cập nhật thông báo", Toast.LENGTH_SHORT).show();
                }
            }
        }); // <<< SỬA LỖI: THÊM DẤU '});' ĐỂ ĐÓNG 'enqueue' >>>

    } // <<< SỬA LỖI: THÊM DẤU '}' ĐỂ ĐÓNG HÀM 'markNotificationAsRead' >>>


    // <<< SỬA LỖI: HÀM NÀY PHẢI NẰM NGOÀI (ở class level) >>>
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

    // <<< SỬA LỖI: HÀM NÀY PHẢI NẰM NGOÀI (ở class level) >>>
    private void showNotificationDetailDialog(NotificationResponse notification) {
        if (getContext() == null) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notification_detail, null);

        TextView tvFullContent = dialogView.findViewById(R.id.tvFullNotificationContent);
        TextView tvTimestamp = dialogView.findViewById(R.id.tvNotificationTimestamp);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        tvFullContent.setText(notification.getContent());
        if (notification.getCreatedAt() != null && !notification.getCreatedAt().isEmpty()) {
            tvTimestamp.setText(notification.getCreatedAt().replace("T", " ").substring(0, 16));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }
} // <<< Dấu '}' cuối cùng của class