package com.example.prm392_assignment_food.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageListAdapter adapter;
    private List<MessageUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        users = new ArrayList<>();
        users.add(new MessageUser("Royal Parvej", "Sounds awesome!", "19:37", 1));
        users.add(new MessageUser("Cameron Williamson", "Ok, just hurry up little bit...", "19:37", 2));
        users.add(new MessageUser("Ralph Edwards", "Thanks dude.", "19:37", 0));
        users.add(new MessageUser("Cody Fisher", "How is going...?", "19:37", 0));
        users.add(new MessageUser("Eleanor Pena", "Thanks for the awesome food man...!", "19:37", 0));

        adapter = new MessageListAdapter(users, this::openChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void openChat(MessageUser user) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
