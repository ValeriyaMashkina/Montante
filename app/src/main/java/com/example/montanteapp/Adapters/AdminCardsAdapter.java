package com.example.montanteapp.Adapters;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.bumptech.glide.Glide;
import com.example.montanteapp.Models.DisplayAdminCard;
import com.example.montanteapp.R;

public class AdminCardsAdapter extends ArrayAdapter<DisplayAdminCard> {

        Context context;
        public AdminCardsAdapter(Context context, int resourceId, List<DisplayAdminCard> items)
        { super(context, resourceId, items); }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            DisplayAdminCard card_item = getItem(position);
            if (convertView == null)
            { convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin, parent, false); }

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView nameAndSurname = (TextView) convertView.findViewById(R.id.nameAndSurname);
        TextView region = (TextView) convertView.findViewById(R.id.region);
        TextView club = (TextView) convertView.findViewById(R.id.club);
        TextView weapons = (TextView) convertView.findViewById(R.id.weapons);


        nameAndSurname.setText(card_item.getNameAndSurname());
        region.setText(card_item.getRegion());
        club.setText(card_item.getClub());
        weapons.setText(card_item.getWeapons());

        switch(card_item.getProfileImageUrl())
        { case "default":
        {Glide.with(convertView.getContext()).load(R.drawable.fencer).into(image);
        break;}
default:
        {Glide.clear(image);
        Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
        break;}


        }
        return convertView;
        }
}
