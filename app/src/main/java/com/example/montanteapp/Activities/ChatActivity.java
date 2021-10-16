package com.example.montanteapp.Activities;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.montanteapp.Models.Chat;
import com.example.montanteapp.Adapters.ChatAdapter;
import com.example.montanteapp.Models.User;
import com.example.montanteapp.R;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private FirebaseAuth mAuth;

    private EditText mSendEditText;
    private ImageButton mSendButton;

    private String currentUserID, rivalId, chatId;

    private DatabaseReference mDatabaseChat;
    List<Chat> resultsChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        rivalId = getIntent().getExtras().getString("rivalId");
        currentUserID = mAuth.getCurrentUser().getUid();
        chatId = Chat.makeChatId(currentUserID, rivalId);


        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        getChatMessages();
        resultsChat = new ArrayList<Chat>();
        mChatAdapter = new ChatAdapter(resultsChat, ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sendMessage(); }
        });

    }


    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);
            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);
    }


    private void getChatMessages()
    {

        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    //Chat chat = dataSnapshot.getValue(Chat.class);
                    String message = null;
                    String createdByUser = null;
                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(message!=null && createdByUser!=null)
                    {
                        Boolean currentUserBoolean = createdByUser.equals(currentUserID);
                        Chat newMessage = new Chat(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

}
