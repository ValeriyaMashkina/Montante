package com.example.montanteapp.Activities;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.montanteapp.Adapters.AdminCardsAdapter;
import com.example.montanteapp.Models.*;
import com.example.montanteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.*;

public class AdminActivity extends AppCompatActivity {

    private AdminCardsAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;
    private String userToActivate;
    List<DisplayAdminCard> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Fighters");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        getInactiveFighters();
        rowItems = new ArrayList<DisplayAdminCard>();

        arrayAdapter = new AdminCardsAdapter(this, R.layout.item_admin, rowItems);
        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter()
            {
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject)
            {
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                else { tv.setVisibility(View.INVISIBLE); }
            }

            @Override
            public void onRightCardExit(Object dataObject)
            {
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                else { tv.setVisibility(View.INVISIBLE); }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) { }
            @Override
            public void onScroll(float scrollProgressPercent) { }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClicked(int itemPosition, Object dataObject)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Уверены?");
                builder.setMessage("Желаете подтвердить учетную запись бойца?");
                builder.setPositiveButton("Да",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                DisplayAdminCard obj = (DisplayAdminCard) dataObject;
                                userToActivate = obj.getUserId();
                                toActivate(userToActivate);
                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(AdminActivity.this,
                                        "Вы только что подтвердили учетную запись нового бойца", Toast.LENGTH_SHORT).show();

                                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                                else { tv.setVisibility(View.INVISIBLE); }
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("Нет",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });
                builder.show();
            }
        });
    }




    public void getInactiveFighters()
    {
        usersDb.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(currentUId))
                {
                    String id = dataSnapshot.getKey();
                    User user = dataSnapshot.getValue(User.class);
                    boolean isActive = user.isIs_active();
                    String name = user.getName();
                    String surname = user.getSurname();
                    String region = user.getRegion();
                    String club = user.getClub();
                    String weapons = user.getWeapons();
                    String profileImageUrl = user.getProfileImageUrl();
                    if (!isActive)
                    {
                        DisplayAdminCard item = new DisplayAdminCard(id, name + " " + surname,
                                "Регион: " + region, "Клуб: " + club,
                                profileImageUrl, "Оружие: " + weapons);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                TextView tv = (TextView)findViewById(R.id.emptyBanner);
                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                else { tv.setVisibility(View.INVISIBLE); }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }



    public void toActivate(final String selectedUser)
    {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Fighters").
                child(selectedUser);
        userDb.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        Map userInfo = new HashMap();
                        userInfo.put("is_active", true);
                        userDb.updateChildren(userInfo);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
    }



    public void goToSettings(View view)
    {
        Intent intent = new Intent(AdminActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    public void goToRivals(View view)
    {
        Intent intent = new Intent(AdminActivity.this, RivalsActivity.class);
        startActivity(intent);
        return;
    }

    public void logoutUser(View view)
    {
        mAuth.signOut();
        Intent intent = new Intent(AdminActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToMain(View view)
    {
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(intent);
        return;
    }
}
