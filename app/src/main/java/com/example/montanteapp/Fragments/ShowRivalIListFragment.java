package com.example.montanteapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.montanteapp.Adapters.RivalIAdapter;
import com.example.montanteapp.Models.Rival;
import com.example.montanteapp.Models.User;
import com.example.montanteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ShowRivalIListFragment extends Fragment {
    private RivalIAdapter adapter;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference rivalsDb;
    private DatabaseReference userDb;
    List<Rival> rivalList;
    private RecyclerView showRivalList;
    private boolean is_pending;
    private boolean is_confirmed;

    public ShowRivalIListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_show_rival_i_list, container, false);
        defineView(view);
        return view;
    }

    private void defineView(View view)
    {
        showRivalList =view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        showRivalList.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        fillRivalsWhoChallengedI();
        rivalList = new ArrayList<Rival>();
        adapter = new RivalIAdapter(rivalList);

        showRivalList.setAdapter(adapter);

    }

    private void fillRivalsWhoChallengedI()
    {
        showRivalList.removeAllViewsInLayout();
        rivalsDb = FirebaseDatabase.getInstance().getReference().child("Challenges");
        rivalsDb.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for(DataSnapshot user : dataSnapshot.getChildren())
                    {
                        if (!user.getKey().equals(currentUserId))
                        {
                            getChallengeIDateInfo(user.getKey());
                        }
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void getChallengeIDateInfo(final String rivalId)
    {
        rivalsDb.child(rivalId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for(DataSnapshot date : dataSnapshot.getChildren())
                    {
                        try { is_pending = date.child("is_pending").getValue(boolean.class); }
                        catch (Exception e) { is_pending = false; }

                        try { is_confirmed = date.child("is_confirmed").getValue(boolean.class); }
                        catch (Exception e) { is_confirmed = false; }
                        FetchRivalIInformation(rivalId, date.getKey(), is_pending, is_confirmed);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    private void FetchRivalIInformation(final String rivalKey, final String challengeDate,
                                        final boolean isPending, final boolean isConfirmed)
    {
        userDb = FirebaseDatabase.getInstance().getReference().child("Fighters").child(rivalKey);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    String region = user.getRegion();
                    String name = user.getName();
                    String surname = user.getSurname();
                    String club = user.getClub();
                    String profileImageUrl = user.getProfileImageUrl();
                    String weapons = user.getWeapons();

                    Rival obj = new Rival(rivalKey, name + " " + surname, region,
                            "Клуб: " + club, "Оружие: " + weapons,
                            profileImageUrl, isPending, isConfirmed, challengeDate);
                    rivalList.add(obj);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    @Override
    public void onAttach(Context context) { super.onAttach(context); }
    @Override
    public void onDetach() { super.onDetach(); }
}