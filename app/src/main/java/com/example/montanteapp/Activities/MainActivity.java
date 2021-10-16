package com.example.montanteapp.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.montanteapp.Adapters.CardsAdapter;
import com.example.montanteapp.Models.*;
import com.example.montanteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




import android.app.AlertDialog;
import android.content.DialogInterface;


//import android.support.v7.app.AppCompatActivity;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MainActivity extends AppCompatActivity
{

    private CardsAdapter arrayAdapter;
    List<DisplayCard> rowItems;
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;
    private String userChallenged;

    private DatabaseReference adminsDb;
    private DatabaseReference сhallengesDb;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Fighters");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        searchFighters();
        rowItems = new ArrayList<DisplayCard>();
        arrayAdapter = new CardsAdapter(this, R.layout.item, rowItems);

        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter() {
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Уверены?");
                builder.setMessage("Желаете вызвать на бойца на дуэль?");
                builder.setPositiveButton("Да",  // устанавливаем слушатель
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                DisplayCard obj = (DisplayCard) dataObject;
                                userChallenged = obj.getUserId();
                                toChallenge(userChallenged);

                                //rowItems.remove(0);
                                //arrayAdapter.notifyDataSetChanged();

                                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                                else { tv.setVisibility(View.INVISIBLE); }
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("Нет",  // устанавливаем слушатель
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });
                builder.show();
            }
        });

    }





    public void toChallenge(final String selectedUser)
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            boolean is_active = user.isIs_active();
                            if (is_active)
                            {
                                ZoneId z = ZoneId.of("Europe/Moscow");
                                ZonedDateTime zdt = ZonedDateTime.now(z);
                                сhallengesDb = FirebaseDatabase.getInstance().getReference().child("Challenges");

                                String date = transformDate (zdt.toLocalDate().toString());

                                DatabaseReference currentChallengeDb =
                                        сhallengesDb.child(selectedUser).child(currentUId).child(date);
                                Map challengeInfo = new HashMap<>();
                                challengeInfo.put("is_pending", true);
                                challengeInfo.put("is_confirmed", false);
                                currentChallengeDb.updateChildren(challengeInfo);
                            }

                            else { createActiveAlertDialog(); }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }






    private String transformDate (String date)
    {
        String[] parts = date.split("-");
        StringBuilder stringBuilder = new StringBuilder(20);
        stringBuilder.append(parts[2]);
        stringBuilder.append("-");
        stringBuilder.append(parts[1]);
        stringBuilder.append("-");
        stringBuilder.append(parts[0]);
        return stringBuilder.toString();
    }



    private String userRegion;
    private ArrayList<String> userWeapons;

    public void searchFighters()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            userRegion = user.getRegion();
                            userWeapons = new ArrayList<String>(Arrays.asList(user.getWeapons().split(",")));
                            getPotentialRivals(userRegion, userWeapons);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    public void getPotentialRivals(final String r, final ArrayList<String> w)
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
                    String region = user.getRegion();
                    String name = user.getName();
                    String surname = user.getSurname();
                    String club = user.getClub();
                    String profileImageUrl = user.getProfileImageUrl();
                    String weapons = user.getWeapons();
                    ArrayList<String> rivalWeapons = new ArrayList<String>(Arrays.asList(weapons.split(",")));

                    if (region.equals(r) && isActive
                            && (!Collections.disjoint(rivalWeapons, w)))
                    {
                        DisplayCard item = new DisplayCard(id, name + " " + surname,
                                "Клуб: " + club, profileImageUrl, "Оружие: " + weapons);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }


                TextView tv = (TextView)findViewById(R.id.emptyBanner);
                if(rowItems.size() == 0) { tv.setVisibility(View.VISIBLE); }
                else { tv.setVisibility(View.INVISIBLE); }

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




    private void createActiveAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Учетная запись не подтверждена");
        builder.setMessage("К сожалению, ваша учетная запись еще не подтверждена админами.  " +
                "Вы пока не можете бросать вызов другим бойцам. " +
                "Если вам кажется, что о вашей заявке забыли, то " +
                "напишите по адресу: duels@inbox.ru");
        // устанавливаем кнопку, которая отвечает за позитивный ответ
        builder.setPositiveButton("OK",  // устанавливаем слушатель
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }



    public void goToSettings(View view)
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }


    public void goToChats(View view)
    {
        Intent intent = new Intent(MainActivity.this, ShowChatsActivity.class);
        startActivity(intent);
        return;
    }

    public void goToRivals(View view)
    {
        Intent intent = new Intent(MainActivity.this, RivalsActivity.class);
        startActivity(intent);
        return;
    }

    public void logoutUser(View view)
    {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }


    public void goToAdmin(View view)
    {
        adminsDb = FirebaseDatabase.getInstance().getReference().child("Admins");
        adminsDb.child(currentUId).addListenerForSingleValueEvent(
                    new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                startActivity(intent);
                                return;
                            }

                           else { createAdminAlertDialog(); }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
    }

    private void createAdminAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Сюда нельзя");
        builder.setMessage("К сожалению, вы пока не админ... " +
                "Но вы можете им стать, связавшись с нами по адресу: duels@inbox.ru. "+
                "Админы ведут реестр бойцов Федерации дуэльного фехтования и " +
                "подтверждают учетные записи новых пользователей этого приложения");
        builder.setPositiveButton("OK",  // устанавливаем слушатель
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
        builder.show();
    }
}
