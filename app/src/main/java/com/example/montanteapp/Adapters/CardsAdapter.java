package com.example.montanteapp.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import java.util.List;

import com.example.montanteapp.Models.DisplayCard;
import com.example.montanteapp.R;

import android.widget.Spinner;

public class CardsAdapter extends ArrayAdapter<DisplayCard>
{

    Context context;
    public CardsAdapter(Context context, int resourceId, List<DisplayCard> items)
    { super(context, resourceId, items); }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        DisplayCard card_item = getItem(position);
        if (convertView == null)
        { convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false); }

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView nameAndSurname = (TextView) convertView.findViewById(R.id.nameAndSurname);
        TextView club = (TextView) convertView.findViewById(R.id.club);
        TextView weapons = (TextView) convertView.findViewById(R.id.weapons);


        nameAndSurname.setText(card_item.getNameAndSurname());
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
