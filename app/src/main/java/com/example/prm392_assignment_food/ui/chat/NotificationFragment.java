package com.example.prm392_assignment_food.ui.chat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewMessages, recyclerViewNotifications;
    private MessageListAdapter messageAdapter;
    private NotificationAdapter notificationAdapter;
    private TextView tabNotifications, tabMessages;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Ánh xạ view ---
        recyclerViewMessages = rootView.findViewById(R.id.recyclerViewMessages);
        recyclerViewNotifications = rootView.findViewById(R.id.recyclerViewNotifications);
        tabNotifications = rootView.findViewById(R.id.tabNotifications);
        tabMessages = rootView.findViewById(R.id.tabMessages);

        // --- Dữ liệu Messages ---
        List<MessageUser> users = new ArrayList<>();
        users.add(new MessageUser("Royal Parvej", "Sounds awesome!", "19:37", 1));
        users.add(new MessageUser("Cameron Williamson", "Ok, just hurry up little bit...", "19:37", 2));
        users.add(new MessageUser("Ralph Edwards", "Thanks dude.", "19:37", 0));
        users.add(new MessageUser("Cody Fisher", "How is going...?", "19:37", 0));
        users.add(new MessageUser("Eleanor Pena", "Thanks for the awesome food man...!", "19:37", 0));

        messageAdapter = new MessageListAdapter(users, this::openChat);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMessages.setAdapter(messageAdapter);

        // --- Dữ liệu Notifications ---
        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("Tanbir Ahmed", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Salim Smith", "left a 5 star review", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Royal Bengol", "agreed to cancel", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Pabel Vulya", "Placed a new order", "20 min ago", R.drawable.pizza1));
        // Add more for scrolling
        notifications.add(new NotificationItem("Tanbir Ahmed", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Salim Smith", "left a 5 star review", "20 min ago", R.drawable.pizza1));

        notificationAdapter = new NotificationAdapter(notifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // --- Tab mặc định là Notifications ---
        showNotifications();

        // --- Xử lý sự kiện khi nhấn vào Tabs ---
        tabNotifications.setOnClickListener(v -> showNotifications());
        tabMessages.setOnClickListener(v -> showMessages());
    }

    private void showNotifications() {
        recyclerViewNotifications.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);

        tabNotifications.setTextColor(requireContext().getColor(R.color.orange));
        tabNotifications.setTypeface(null, Typeface.BOLD);
        tabMessages.setTextColor(requireContext().getColor(R.color.dark_gray));
        tabMessages.setTypeface(null, Typeface.NORMAL);
    }

    private void showMessages() {
        recyclerViewMessages.setVisibility(View.VISIBLE);
        recyclerViewNotifications.setVisibility(View.GONE);

        tabMessages.setTextColor(requireContext(). getColor(R.color.orange));
        tabMessages.setTypeface(null, Typeface.BOLD);
        tabNotifications.setTextColor(requireContext().getColor(R.color.dark_gray));
        tabNotifications.setTypeface(null, Typeface.NORMAL);
    }

    private void openChat(MessageUser user) {
        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        startActivity(intent);
    }
}
