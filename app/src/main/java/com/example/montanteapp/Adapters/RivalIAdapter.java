package com.example.montanteapp.Adapters;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.montanteapp.Activities.ChatActivity;
import com.example.montanteapp.Models.Chat;
import com.example.montanteapp.Models.Rival;
import com.example.montanteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;

public class RivalIAdapter extends RecyclerView.Adapter<RivalIAdapter.ViewHolder> {

    List<Rival> rivalList;
    private String currentUserId;


    public RivalIAdapter(List<Rival> rivalList) { this.rivalList = rivalList; }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_i_rival,null)); }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.bindView(rivalList.get(position));
    }


    @Override
    public int getItemCount() { return rivalList.size(); }
    @Override
    public long getItemId(int position) { return position;}
    @Override
    public int getItemViewType(int position) { return position;}

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView image;
        public TextView nameAndSurname, region, club, weapons, challengeDate, rivalId, status;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            nameAndSurname = (TextView) itemView.findViewById(R.id.nameAndSurname);
            region = (TextView) itemView.findViewById(R.id.region);
            club = (TextView) itemView.findViewById(R.id.club);
            weapons = (TextView) itemView.findViewById(R.id.weapons);
            challengeDate = (TextView) itemView.findViewById(R.id.challengeDate);
            rivalId = (TextView) itemView.findViewById(R.id.rivalId);
            status = (TextView) itemView.findViewById(R.id.status);

            image.setOnClickListener(new View.OnClickListener()
            { @Override
            public void onClick(View view)
            {
                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String chatId = Chat.makeChatId(currentUserId, rivalId.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).
                        addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            Intent intent = new Intent(view.getContext(), ChatActivity.class);
                            Bundle b = new Bundle();
                            b.putString("rivalId", rivalId.getText().toString());
                            intent.putExtras(b);
                            view.getContext().startActivity(intent); }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
            });
        }


        public void bindView(Rival rival)
        {

            if(!rival.getProfileImageUrl().equals("default"))
            {
                Glide.with(image.getContext()).load(rival.getProfileImageUrl()).into(image);
            }
            nameAndSurname.setText(rival.getNameAndSurname());
            region.setText(rival.getRegion());
            club.setText(rival.getClub());
            weapons.setText(rival.getWeapons());
            challengeDate.setText(rival.getChallengeDate());
            rivalId.setText(rival.getUserId());

            if(rival.isPending())
            {
                status.setText("Рассматривается");
                status.setTextColor(Color.BLUE);
            }

            else
            {

                if(rival.isConfirmed())
                {
                    status.setText("Принят");
                    status.setTextColor(Color.GREEN);
                }

                else
                {
                    status.setText("Отклонен");
                    status.setTextColor(Color.RED);
                }
            }
        }
    }
}

