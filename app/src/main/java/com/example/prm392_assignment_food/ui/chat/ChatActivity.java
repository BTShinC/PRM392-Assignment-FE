package com.example.prm392_assignment_food.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageView btnBack;
    private ChatAdapter adapter;
    private final List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        setupRecyclerView();
        seedSampleMessages();
        setupSendAction();
        setupBackButton(); // 👈 Thêm hàm xử lý back
    }

    /** Xử lý nút quay lại **/
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, NotificationFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // kết thúc ChatActivity hiện tại
        });
    }

    /** Thiết lập RecyclerView */
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // tin nhắn mới ở cuối
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator()); // animation mặc định
    }

    /** Dữ liệu mẫu ban đầu */
    private void seedSampleMessages() {
        messages.add(new Message("Hey, congratulations for the order!", false));
        messages.add(new Message("Are you coming?", true));
        messages.add(new Message("I'm coming, just wait...", false));
        messages.add(new Message("Hurry up, man!", true));
        adapter.notifyDataSetChanged();
        scrollToBottom();
    }

    /** Sự kiện gửi tin nhắn */
    private void setupSendAction() {
        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    /** Gửi tin nhắn mới */
    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        Message newMessage = new Message(content, true);
        messages.add(newMessage);
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        etMessage.setText("");
        hideKeyboard();
    }

    /** Cuộn mượt xuống cuối danh sách */
    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1));
    }

    /** Ẩn bàn phím khi gửi */
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
