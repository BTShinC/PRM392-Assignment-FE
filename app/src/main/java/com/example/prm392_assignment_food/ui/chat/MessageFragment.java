package com.example.prm392_assignment_food.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    private ApiService apiService;
    private UUID currentUserId;
    private RecyclerView recyclerViewMessages;
    private MessageListAdapter messageAdapter;
    private TextView tvEmptyMessages;

    // <<< THÊM DÒNG NÀY: ĐỊNH NGHĨA ID CỦA ADMIN >>>
    private static final UUID ADMIN_ID = UUID.fromString("dc662642-5435-4487-8b78-e4df3c339aa9");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        tvEmptyMessages = view.findViewById(R.id.tvEmptyMessages);
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
        loadConversations();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageListAdapter(new ArrayList<>(), this::openChat);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void openChat(Conversation conversation) {
        // 1. Tạo một "lá thư" (Intent) để gửi đến ChatActivity
        Intent intent = new Intent(getActivity(), ChatActivity.class);

        // 2. Ghi ID của người nhận lên lá thư (đã có)
        intent.putExtra("receiver_id", conversation.getOtherUserId().toString());

        // 3. <<< THÊM DÒNG NÀY: Ghi TÊN của người nhận lên lá thư >>>
        intent.putExtra("receiver_name", conversation.getOtherUserName());

        // 4. Gửi lá thư đi
        startActivity(intent);
    }

    private void loadConversations() {
        apiService.getChatHistory(currentUserId).enqueue(new Callback<List<ChatMessageResponse>>() {
            @Override
            public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<ChatMessageResponse> allMessages = response.body();
                    List<Conversation> conversations = processMessagesToConversations(allMessages);
                    if (conversations.isEmpty()) {
                        recyclerViewMessages.setVisibility(View.GONE);
                        tvEmptyMessages.setVisibility(View.VISIBLE);
                    } else {
                        recyclerViewMessages.setVisibility(View.VISIBLE);
                        tvEmptyMessages.setVisibility(View.GONE);
                        messageAdapter.updateData(conversations);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChatMessageResponse>> call, Throwable t) {
                if(isAdded()) {
                    Toast.makeText(getActivity(), "Error loading messages.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private List<Conversation> processMessagesToConversations(List<ChatMessageResponse> messages) {
//        Map<UUID, List<ChatMessageResponse>> groupedMessages = new ConcurrentHashMap<>();
//        for (ChatMessageResponse msg : messages) {
//            UUID otherUserId;
//            if (msg.getSenderId().equals(currentUserId)) {
//                otherUserId = msg.getReceiverId();
//            } else {
//                otherUserId = msg.getSenderId();
//            }
//            groupedMessages.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(msg);
//        }
//        List<Conversation> conversations = new ArrayList<>();
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        for (Map.Entry<UUID, List<ChatMessageResponse>> entry : groupedMessages.entrySet()) {
//            Conversation conversation = new Conversation();
//            List<ChatMessageResponse> conversationMessages = entry.getValue();
//            if (conversationMessages.isEmpty()) continue;
//            conversationMessages.sort(Comparator.comparing(ChatMessageResponse::getCreatedAt).reversed());
//            ChatMessageResponse lastMessage = conversationMessages.get(0);
//            conversation.setOtherUserId(entry.getKey());
//            conversation.setOtherUserName("User " + entry.getKey().toString().substring(0, 4));
//            conversation.setLastMessageContent(lastMessage.getContent());
//            try {
//                LocalDateTime dateTime = LocalDateTime.parse(lastMessage.getCreatedAt());
//                conversation.setLastMessageTime(dateTime.format(outputFormatter));
//            } catch (Exception e) {
//                conversation.setLastMessageTime("");
//            }
//            long unread = conversationMessages.stream()
//                    .filter(m -> !m.isRead() && !m.getSenderId().equals(currentUserId))
//                    .count();
//            conversation.setUnreadCount((int) unread);
//            conversations.add(conversation);
//        }
//        return conversations;
//    }
private List<Conversation> processMessagesToConversations(List<ChatMessageResponse> messages) {
    Map<UUID, List<ChatMessageResponse>> groupedMessages = new ConcurrentHashMap<>();
    for (ChatMessageResponse msg : messages) {
        UUID otherUserId;
        if (msg.getSenderId().equals(currentUserId)) {
            otherUserId = msg.getReceiverId();
        } else {
            otherUserId = msg.getSenderId();
        }
        groupedMessages.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(msg);
    }

    List<Conversation> conversations = new ArrayList<>();
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // <<< THÊM BIẾN NÀY ĐỂ KIỂM TRA >>>
    boolean adminConversationExists = false;

    for (Map.Entry<UUID, List<ChatMessageResponse>> entry : groupedMessages.entrySet()) {
        Conversation conversation = new Conversation();
        List<ChatMessageResponse> conversationMessages = entry.getValue();
        if (conversationMessages.isEmpty()) continue;
        conversationMessages.sort(Comparator.comparing(ChatMessageResponse::getCreatedAt).reversed());
        ChatMessageResponse lastMessage = conversationMessages.get(0);

        conversation.setOtherUserId(entry.getKey());

        // <<< KIỂM TRA XEM CUỘC TRÒ CHUYỆN NÀY CÓ PHẢI LÀ VỚI ADMIN KHÔNG >>>
        if (conversation.getOtherUserId().equals(ADMIN_ID)) {
            adminConversationExists = true;
            // Bạn có thể đặt tên thân thiện hơn cho Admin
            conversation.setOtherUserName("Admin Support");
        } else {
            // Giữ logic cũ của bạn cho những người dùng khác
            conversation.setOtherUserName("User " + entry.getKey().toString().substring(0, 4));
        }

        conversation.setLastMessageContent(lastMessage.getContent());
        try {
            LocalDateTime dateTime = LocalDateTime.parse(lastMessage.getCreatedAt());
            conversation.setLastMessageTime(dateTime.format(outputFormatter));
        } catch (Exception e) {
            conversation.setLastMessageTime("");
        }
        long unread = conversationMessages.stream()
                .filter(m -> !m.isRead() && !m.getSenderId().equals(currentUserId))
                .count();
        conversation.setUnreadCount((int) unread);
        conversations.add(conversation);
    }

    // <<< LOGIC CHÍNH: NẾU KHÔNG TÌM THẤY ADMIN, TẠO MỘT CUỘC TRÒ CHUYỆN MỚI >>>
    if (!adminConversationExists) {
        Conversation adminConversation = new Conversation();
        adminConversation.setOtherUserId(ADMIN_ID);
        adminConversation.setOtherUserName("Admin Support"); // Đặt tên

        // Lời chào mặc định theo yêu cầu của bạn
        adminConversation.setLastMessageContent("Chào bạn, chúng tôi có thể giúp gì cho bạn?");

        // Đặt thời gian là hiện tại
        adminConversation.setLastMessageTime(LocalDateTime.now().format(outputFormatter));

        // Đặt là 1 tin nhắn chưa đọc (vì admin "vừa gửi" cho bạn)
        adminConversation.setUnreadCount(1);

        // Thêm vào đầu danh sách để luôn ở trên cùng
        conversations.add(0, adminConversation);
    }

    return conversations;
}
}