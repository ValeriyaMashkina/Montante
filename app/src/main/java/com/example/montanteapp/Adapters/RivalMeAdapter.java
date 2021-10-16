package com.example.montanteapp.Adapters;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.montanteapp.Activities.ChatActivity;
import com.example.montanteapp.Models.Chat;
import com.example.montanteapp.Models.Rival;
import com.example.montanteapp.Models.User;
import com.example.montanteapp.R;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RivalMeAdapter extends RecyclerView.Adapter<RivalMeAdapter.ViewHolder> {

    List<Rival> rivalList;
    private String currentUserId;
    private DatabaseReference currentChallengeDb;



    public RivalMeAdapter(List<Rival> rivalList) {
        this.rivalList = rivalList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_me_rival,null)); }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        holder.bindView(rivalList.get(position));
        holder.confirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            holder.confirm.setEnabled(false);
            holder.decline.setEnabled(false);
            holder.confirm.setClickable(false);
            holder.decline.setClickable(false);
            holder.confirm.setVisibility(View.INVISIBLE);
            holder.decline.setVisibility(View.INVISIBLE);
            toConfirm(holder.rivalId.getText().toString(), holder.challengeDate.getText().toString());
        }
    });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDecline(holder.rivalId.getText().toString(), holder.challengeDate.getText().toString());
                rivalList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    public void toConfirm(final String rivalId, final String  challengeDate)
    {
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentChallengeDb = FirebaseDatabase.getInstance().
                getReference().child("Challenges").child(currentUserId).child(rivalId)
                .child(challengeDate);
        Map challengeInfo = new HashMap<>();
        challengeInfo.put("is_pending", false);
        challengeInfo.put("is_confirmed", true);
        currentChallengeDb.updateChildren(challengeInfo);
        createChat (currentUserId,rivalId);
        sendNoteOfConfirmation (currentUserId,rivalId);
    }

    public void sendNoteOfConfirmation (final String userId, final String rivalId)
    {

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Fighters").child(userId);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener()
        {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot)
         {
           if (dataSnapshot.exists())
           {
             User user = dataSnapshot.getValue(User.class);
             String name = user.getName();
             String surname = user.getSurname();
             DatabaseReference eventsDb = FirebaseDatabase.getInstance().getReference().child("Events").child(rivalId).push();
             Map eventInfo = new HashMap();
             String text = name+" "+surname+" принимает ваш вызов";
             eventInfo.put("text", text);
             eventsDb.setValue(eventInfo);
           }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) { }

         });
    }

    public void createChat (final String currId, final String rivalId)
    {
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chats");
        String chatId = Chat.makeChatId(currId, rivalId);
        chatDb.child(chatId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.exists()) { chatDb.child(chatId).setValue(""); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void toDecline(final String rivalId, final String  challengeDate)
    {
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentChallengeDb = FirebaseDatabase.getInstance().
                getReference().child("Challenges").child(currentUserId).child(rivalId)
                .child(challengeDate);
        Map challengeInfo = new HashMap<>();
        challengeInfo.put("is_pending", false);
        challengeInfo.put("is_confirmed", false);
        currentChallengeDb.updateChildren(challengeInfo);
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
        public TextView nameAndSurname, region, club, weapons, challengeDate, rivalId;
        public Button confirm, decline;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);

            nameAndSurname = (TextView) itemView.findViewById(R.id.nameAndSurname);
            region = (TextView) itemView.findViewById(R.id.region);
            club = (TextView) itemView.findViewById(R.id.club);
            weapons = (TextView) itemView.findViewById(R.id.weapons);
            challengeDate = (TextView) itemView.findViewById(R.id.challengeDate);
            rivalId = (TextView) itemView.findViewById(R.id.rivalId);
            confirm = (Button) itemView.findViewById(R.id.confirm);
            decline = (Button) itemView.findViewById(R.id.decline);

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

            if(rival.isPending()) {
                confirm.setEnabled(true);
                decline.setEnabled(true);
                confirm.setClickable(true);
                decline.setClickable(true);
                confirm.setVisibility(View.VISIBLE);
                decline.setVisibility(View.VISIBLE);
            }

            else
            {
                confirm.setEnabled(false);
                decline.setEnabled(false);
                confirm.setClickable(false);
                decline.setClickable(false);
                confirm.setVisibility(View.INVISIBLE);
                decline.setVisibility(View.INVISIBLE);
            }
        }
    }
}

/*
public class RivalAdapter extends RecyclerView.Adapter<RivalViewHolders>
{
    private List<Rival> rivalsList;
    private Context context;

    public RivalAdapter(List<Rival> rivalsList, Context context)
    {
        this.rivalsList = rivalsList;
        this.context = context;
    }


    @Override
    public int getItemCount() { return this.rivalsList.size(); }


    @Override
    public RivalViewHolders onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_rival, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RivalViewHolders rcv = new RivalViewHolders(layoutView);
        return rcv;

    }

    @Override
    public void onBindViewHolder(RivalViewHolders holder, int position) {

        if(!rivalsList.get(position).getProfileImageUrl().equals("default"))
        {
            Glide.with(context).load(rivalsList.get(position).getProfileImageUrl()).into(holder.image);
        }

        holder.nameAndSurname.setText(rivalsList.get(position).getNameAndSurname());
        holder.region.setText(rivalsList.get(position).getRegion());
        holder.club.setText(rivalsList.get(position).getClub());
        holder.weapons.setText(rivalsList.get(position).getWeapons());
        holder.challengeDate.setText(rivalsList.get(position).getChallengeDate());
        holder.rivalId.setText(rivalsList.get(position).getUserId());


        /*
        if(rivalsList.get(position).isPending())
        {
            holder.confirm.setEnabled(true);
            holder.decline.setEnabled(true);
            holder.confirm.setClickable(true);
            holder.decline.setClickable(true);
            holder.confirm.setVisibility(View.VISIBLE);
            holder.decline.setVisibility(View.VISIBLE);

            //if(!rivalsList.get(position).isConfirmed())
           // { rivalsList.remove(position);}
        }


    }
}
  */

