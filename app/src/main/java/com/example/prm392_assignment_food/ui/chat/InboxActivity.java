package com.example.prm392_assignment_food.ui.chat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.prm392_assignment_food.R;

public class InboxActivity extends AppCompatActivity {

    private TextView tabNotifications, tabMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        tabNotifications = findViewById(R.id.tabNotifications);
        tabMessages = findViewById(R.id.tabMessages);

        tabNotifications.setOnClickListener(v -> selectTab(true));
        tabMessages.setOnClickListener(v -> selectTab(false));

        // Mặc định chọn tab Notifications khi mở màn hình
        if (savedInstanceState == null) {
            selectTab(true);
        }
    }

    private void selectTab(boolean isNotificationsSelected) {
        if (isNotificationsSelected) {
            // Cập nhật giao diện tab
            tabNotifications.setTextColor(ContextCompat.getColor(this, R.color.orange_active));
            tabNotifications.setTypeface(null, Typeface.BOLD);
            tabMessages.setTextColor(ContextCompat.getColor(this, R.color.gray_inactive));
            tabMessages.setTypeface(null, Typeface.NORMAL);

            // Thay thế Fragment
            replaceFragment(new NotificationFragment());
        } else {
            // Cập nhật giao diện tab
            tabNotifications.setTextColor(ContextCompat.getColor(this, R.color.gray_inactive));
            tabNotifications.setTypeface(null, Typeface.NORMAL);
            tabMessages.setTextColor(ContextCompat.getColor(this, R.color.orange_active));
            tabMessages.setTypeface(null, Typeface.BOLD);

            // Thay thế Fragment
            replaceFragment(new MessageFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}