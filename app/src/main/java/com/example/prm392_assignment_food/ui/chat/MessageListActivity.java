package com.example.prm392_assignment_food.ui.chat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages, recyclerViewNotifications;
    private MessageListAdapter messageAdapter;
    private NotificationAdapter notificationAdapter;
    private TextView tabNotifications, tabMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        // --- Ánh xạ view ---
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        tabNotifications = findViewById(R.id.tabNotifications);
        tabMessages = findViewById(R.id.tabMessages);

        // --- Dữ liệu Messages ---
        List<MessageUser> users = new ArrayList<>();
        users.add(new MessageUser("Royal Parvej", "Sounds awesome!", "19:37", 1));
        users.add(new MessageUser("Cameron Williamson", "Ok, just hurry up little bit...", "19:37", 2));
        users.add(new MessageUser("Ralph Edwards", "Thanks dude.", "19:37", 0));
        users.add(new MessageUser("Cody Fisher", "How is going...?", "19:37", 0));
        users.add(new MessageUser("Eleanor Pena", "Thanks for the awesome food man...!", "19:37", 0));

        messageAdapter = new MessageListAdapter(users, this::openChat);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // --- Dữ liệu Notifications ---
        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("Tanbir Ahmed", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Salim Smith", "left a 5 star review", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Royal Bengol", "agreed to cancel", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Pabel Vulya", "Placed a new order", "20 min ago", R.drawable.pizza1));

        notifications.add(new NotificationItem("Tanbir Ahmed", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Salim Smith", "left a 5 star review", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Royal Bengol", "agreed to cancel", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Pabel Vulya", "Placed a new order", "20 min ago", R.drawable.pizza1));

        notifications.add(new NotificationItem("Tanbir Ahmed", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Salim Smith", "left a 5 star review", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Royal Bengol", "agreed to cancel", "20 min ago", R.drawable.pizza1));
        notifications.add(new NotificationItem("Pabel Vulya", "Placed a new order", "20 min ago", R.drawable.pizza1));
        notificationAdapter = new NotificationAdapter(notifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // --- Tab mặc định là Notifications ---
        showNotifications();

        // --- Xử lý sự kiện khi nhấn vào Tabs ---
        tabNotifications.setOnClickListener(v -> showNotifications());
        tabMessages.setOnClickListener(v -> showMessages());
    }

    // ===================== TAB SWITCH =====================

    private void showNotifications() {
        recyclerViewNotifications.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);

        tabNotifications.setTextColor(getColor(R.color.orange));
        tabNotifications.setTypeface(null, Typeface.BOLD);
        tabMessages.setTextColor(getColor(R.color.dark_gray));
        tabMessages.setTypeface(null, Typeface.NORMAL);
    }

    private void showMessages() {
        recyclerViewMessages.setVisibility(View.VISIBLE);
        recyclerViewNotifications.setVisibility(View.GONE);

        tabMessages.setTextColor(getColor(R.color.orange));
        tabMessages.setTypeface(null, Typeface.BOLD);
        tabNotifications.setTextColor(getColor(R.color.dark_gray));
        tabNotifications.setTypeface(null, Typeface.NORMAL);
    }

    // ===================== CHUYỂN CHAT =====================
    private void openChat(MessageUser user) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
