package com.example.montanteapp.Activities;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.montanteapp.Models.ChatCaption;
import com.example.montanteapp.Adapters.ChatCaptionAdapter;
import com.example.montanteapp.Models.User;
import com.example.montanteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ShowChatsActivity extends AppCompatActivity {

    private Button mBack;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference chatsDb;
    private DatabaseReference userDb;
    private RecyclerView showChatsList;
    private ChatCaptionAdapter adapter;
    List<ChatCaption> chatsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chats);
        mBack = (Button) findViewById(R.id.back);
        showChatsList =(RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(ShowChatsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        showChatsList.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Fighters");
        chatsDb = FirebaseDatabase.getInstance().getReference().child("Chats");


        searchChats();
        chatsList = new ArrayList<ChatCaption>();
        adapter = new ChatCaptionAdapter(chatsList, ShowChatsActivity.this);
        showChatsList.setAdapter(adapter);

        mBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
                return;
            }
        });
    }

    public void searchChats()
    {
        chatsDb = FirebaseDatabase.getInstance().getReference().child("Chats");
        chatsDb.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            for(DataSnapshot chat : dataSnapshot.getChildren())
                            {
                                if (chat.getKey().contains(currentUserId) )
                                {
                                    String rivalId = chat.getKey().replace(currentUserId,"");
                                    FetchChatInformation(chat.getKey(),rivalId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }


    private void FetchChatInformation(final String chatId, final String rivalId)
    {
        userDb = FirebaseDatabase.getInstance().getReference().child("Fighters").child(rivalId);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    String name = user.getName();
                    String surname = user.getSurname();
                    String profileImageUrl = user.getProfileImageUrl();
                    ChatCaption obj = new ChatCaption(chatId,rivalId,name, surname, profileImageUrl );
                    chatsList.add(obj);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}
