package com.example.prm392_assignment_food.ui.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ApiService apiService;
    private StompClient stompClient;
    private Gson gson;

    private RecyclerView recyclerViewChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageView btnBack;
    private TextView tvChatName;
    private ChatAdapter adapter;

    private UUID currentUserId;
    private UUID receiverId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        gson = new GsonBuilder().create();

        initViews();
        getIntentData();

        apiService = ApiClient.getAuthenticatedApiService();
        TokenManager tokenManager = new TokenManager(this);
        currentUserId = tokenManager.getCurrentUserId();

        if (currentUserId == null || receiverId == null) {
            Toast.makeText(this, "Error: Missing user or receiver information.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadChatHistory();
        connectWebSocket(); // Lỗi 2 được sửa ở đây

        btnSend.setOnClickListener(v -> sendMessage()); // Lỗi 3 được sửa ở đây
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvChatName = findViewById(R.id.tvChatName);
    }
    private void getIntentData() {
        // Lấy chuỗi ID và Tên từ Intent mà MessageFragment đã gửi
        String receiverIdStr = getIntent().getStringExtra("receiver_id");
        receiverName = getIntent().getStringExtra("receiver_name");

        Log.d(TAG, "Received from Intent -> receiver_id: " + receiverIdStr + ", receiver_name: " + receiverName);

        // BƯỚC QUAN TRỌNG: Kiểm tra và xử lý dữ liệu nhận được
        if (receiverIdStr != null && !receiverIdStr.isEmpty()) {
            try {
                // Chuyển đổi chuỗi ID thành đối tượng UUID
                receiverId = UUID.fromString(receiverIdStr);
            } catch (IllegalArgumentException e) {
                // Xử lý trường hợp chuỗi ID không đúng định dạng UUID
                Log.e(TAG, "Invalid UUID format for receiver_id: " + receiverIdStr, e);
                Toast.makeText(this, "Error: Invalid receiver ID.", Toast.LENGTH_LONG).show();
                finish(); // Đóng Activity nếu ID không hợp lệ để tránh lỗi
                return;
            }
        } else {
            // Xử lý trường hợp không có receiver_id nào được truyền qua
            Log.e(TAG, "Receiver ID was not passed to ChatActivity. Cannot start chat.");
            Toast.makeText(this, "Error: No receiver specified.", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity vì không biết chat với ai
            return;
        }

        // Nếu mọi thứ ổn, cập nhật tên trên thanh header
        if (receiverName != null) {
            tvChatName.setText(receiverName);
        } else {
            // Tên mặc định nếu vì lý do nào đó tên không được truyền qua
            tvChatName.setText("Chat");
        }
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(new ArrayList<>(), currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(adapter);
    }

    private void loadChatHistory() {
        apiService.getChatHistory(currentUserId).enqueue(new Callback<List<ChatMessageResponse>>() {
            @Override
            public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatMessageResponse> filteredMessages = new ArrayList<>();
                    for (ChatMessageResponse msg : response.body()) {
                        boolean isMessageInThisConversation = (msg.getSenderId().equals(currentUserId) && msg.getReceiverId().equals(receiverId)) ||
                                (msg.getSenderId().equals(receiverId) && msg.getReceiverId().equals(currentUserId));
                        if (isMessageInThisConversation) {
                            filteredMessages.add(msg);
                        }
                    }
                    adapter.setMessages(filteredMessages);
                    if (adapter.getItemCount() > 0) {
                        recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to load chat history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageResponse>> call, Throwable t) {
                Log.e(TAG, "API call failed for chat history", t);
            }
        });
    }

    // <<< HÀM CÒN THIẾU 1: connectWebSocket >>>
// <<< THAY THẾ TOÀN BỘ HÀM NÀY >>>
    @SuppressLint("CheckResult")
    private void connectWebSocket() {
        // SỬA LẠI ĐÚNG ĐỊA CHỈ IP VÀ CỔNG MÀ BACKEND CUNG CẤP
        String webSocketUrl = "ws://109.123.238.244:3050/ws/chat-websocket";

        // 1. Lấy token từ TokenManager
        TokenManager tokenManager = new TokenManager(this);
        String tokenWithBearer = tokenManager.getToken();

        if (tokenWithBearer == null) {
            Log.e(TAG, "Cannot connect to WebSocket: token is null.");
            Toast.makeText(this, "Authentication token not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Loại bỏ "Bearer " khỏi token
        String rawToken = tokenWithBearer;
        if (rawToken.toLowerCase().startsWith("bearer ")) {
            rawToken = rawToken.substring(7);
        }

        // 3. Xây dựng URL cuối cùng với token
        String webSocketUrlWithToken = webSocketUrl + "?token=" + rawToken;

        // In ra để kiểm tra
        Log.d(TAG, "Connecting to WebSocket with URL: " + webSocketUrlWithToken);

        // 4. Khởi tạo StompClient với URL ĐÚNG
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, webSocketUrlWithToken);

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "Stomp connection opened! SUCCESS!");
                    subscribeToTopic();
                    break;
                case ERROR:
                    Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "WebSocket Connection Error", Toast.LENGTH_SHORT).show());
                    break;
                case CLOSED:
                    Log.d(TAG, "Stomp connection closed.");
                    break;
            }
        });

        stompClient.connect();
    }

    private void subscribeToTopic() {
        String topic = "/topic/chat/" + currentUserId.toString();
        stompClient.topic(topic).subscribe(stompMessage -> {
            ChatMessageResponse newMessage = gson.fromJson(stompMessage.getPayload(), ChatMessageResponse.class);
            if (newMessage.getSenderId().equals(receiverId)) {
                runOnUiThread(() -> {
                    adapter.addMessage(newMessage);
                    recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
        }, throwable -> Log.e(TAG, "Error on topic subscription!", throwable));
        Log.d(TAG, "Successfully subscribed to topic: " + topic);
    }

    // <<< HÀM CÒN THIẾU 2: sendMessage >>>
    @SuppressLint("CheckResult")
    private void sendMessage() {
        Log.d(TAG, "Send button clicked. Sender ID: " + currentUserId + ", Receiver ID: " + receiverId);
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty() || stompClient == null || !stompClient.isConnected()) {
            return;
        }

        ChatMessageRequest request = new ChatMessageRequest(currentUserId, receiverId, content);
        String jsonPayload = gson.toJson(request);

        Log.d(TAG, "Sending JSON Payload: " + jsonPayload);

        stompClient.send("/app/chat.send", jsonPayload).subscribe(() -> {
            runOnUiThread(() -> {
                // Hiển thị tin nhắn của mình ngay lập tức để có trải nghiệm tốt hơn
                adapter.addMessage(requestToResponse(request));
                recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                etMessage.setText("");
            });
        }, throwable -> runOnUiThread(() -> {
            Log.e(TAG, "Failed to send message", throwable);
            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }));
    }

    // <<< HÀM PHỤ TRỢ CHO sendMessage >>>
    private ChatMessageResponse requestToResponse(ChatMessageRequest request) {
        return new ChatMessageResponse(
                UUID.randomUUID(),
                request.getSenderId(),
                request.getReceiverId(),
                request.getContent(),
                false, // Mặc định là chưa đọc
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    // <<< HÀM QUẢN LÝ VÒNG ĐỜI: onDestroy >>>
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.disconnect();
        }
    }
}
