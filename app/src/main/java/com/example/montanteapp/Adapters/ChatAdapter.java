package com.example.montanteapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.montanteapp.Models.Chat;
import com.example.montanteapp.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    private List<Chat> chatList;
    private Context context;


    public ChatAdapter(List<Chat> chatList, Context context)
    {
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ViewHolder rcv = new ViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            holder.mMessage.setGravity(Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#404040"));
            //holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }else{
            holder.mMessage.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#e30505"));
            //holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }

    }

    @Override
    public int getItemCount() { return this.chatList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView mMessage;
        public LinearLayout mContainer;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener
            (new View.OnClickListener()
            { @Override
            public void onClick(View view) {}});
            mMessage = itemView.findViewById(R.id.message);
            mContainer = itemView.findViewById(R.id.container);
        }
    }
}
