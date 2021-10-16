package com.example.montanteapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.montanteapp.Activities.ChatActivity;
import com.example.montanteapp.Models.ChatCaption;
import com.example.montanteapp.R;
import com.google.firebase.database.*;

import java.util.List;

public class ChatCaptionAdapter extends RecyclerView.Adapter<ChatCaptionAdapter.ViewHolder>
{
    private List<ChatCaption> chatList;
    private Context context;

    public ChatCaptionAdapter(List<ChatCaption> chatList, Context context){
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_chats, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ViewHolder rcv = new ViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.bindView(chatList.get(position));
    }
    @Override
    public int getItemCount() { return this.chatList.size(); }
    @Override
    public long getItemId(int position) { return position;}
    @Override
    public int getItemViewType(int position) { return position;}

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView image;
        public TextView name, surname, chatId, rivalId;
        public LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            surname = (TextView) itemView.findViewById(R.id.surname);
            chatId = (TextView) itemView.findViewById(R.id.chatId);
            rivalId = (TextView) itemView.findViewById(R.id.rivalId);

            item.setOnClickListener(new View.OnClickListener()
            { @Override
            public void onClick(View view)
            {
                DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chats");
                chatDb.child(chatId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Intent intent = new Intent(view.getContext(), ChatActivity.class);
                            Bundle b = new Bundle();
                            b.putString("rivalId", rivalId.getText().toString());
                            intent.putExtras(b);
                            view.getContext().startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }});
        }


        public void bindView(ChatCaption chat)
        {

            if(!chat.getProfileImageUrl().equals("default"))
            {
                Glide.with(image.getContext()).load(chat.getProfileImageUrl()).into(image);
            }
            name.setText(chat.getUserName());
            surname.setText(chat.getUserSurname());
            chatId.setText(chat.getChatId());
            rivalId.setText(chat.getUserId());
        }
    }
}
