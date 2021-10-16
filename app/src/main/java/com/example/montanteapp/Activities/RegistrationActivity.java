package com.example.montanteapp.Activities;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import com.example.montanteapp.Models.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.montanteapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;


public class RegistrationActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName, mSurname;


    private Spinner mRegion, mClub;

    private ArrayAdapter regionsAdapter;
    List<String> regions;
    private ArrayAdapter clubsAdapter;
    List<String> clubs;


    private RadioGroup mRadioGroup;

    private CheckBox boxLongsword;
    private CheckBox boxSwordBackler;
    private CheckBox boxRapierDaga;
    private CheckBox boxRapierSolo;
    private CheckBox boxSable;
    private CheckBox boxMontante;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mRadioGroup = (RadioGroup) findViewById(R.id.genderSelectGroup);

        ////////////////////////////////////////////
        searchRegions();
        regions = new ArrayList<String>();
        regionsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, regions);
        mRegion = (Spinner) findViewById(R.id.regionSpinner);
        mRegion.setAdapter(regionsAdapter);

        searchClubs();
        clubs = new ArrayList<String>();
        clubsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, clubs);
        mClub = (Spinner) findViewById(R.id.clubSpinner);
        mClub.setAdapter(clubsAdapter);
        ////////////////////////////////////////////




        boxLongsword = (CheckBox)findViewById(R.id.boxLongsword);
        boxSwordBackler = (CheckBox)findViewById(R.id.boxSwordBackler);
        boxRapierDaga = (CheckBox)findViewById(R.id.boxRapierDaga);
        boxRapierSolo = (CheckBox)findViewById(R.id.boxRapierSolo);
        boxSable = (CheckBox)findViewById(R.id.boxSable);
        boxMontante = (CheckBox)findViewById(R.id.boxMontante);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                if (radioButton.getText() == null)
                {
                    Toast.makeText(RegistrationActivity.this, "Не указан пол", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String surname = mSurname.getText().toString();
                final String gender = radioButton.getText().toString();
                final String region = mRegion.getSelectedItem().toString();
                final String club = mClub.getSelectedItem().toString();

                ArrayList<String> weapons = new ArrayList<String> ();
                if(boxLongsword.isChecked()) {weapons.add("Длинный меч");}
                if(boxSwordBackler.isChecked()) {weapons.add("Меч и баклер");}
                if(boxRapierDaga.isChecked()) {weapons.add("Рапира и дага");}
                if(boxRapierSolo.isChecked()) {weapons.add("Рапира solo");}
                if(boxSable.isChecked()) {weapons.add("Военная сабля");}
                if(boxMontante.isChecked()) {weapons.add("Монтанте");}

                String res = User.checkValidInputRegistrationUser(email, password, name, surname, gender, region, club, weapons);
                if (res.equals("ОК"))
                {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful())
                                    { Toast.makeText(RegistrationActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show(); }
                                    else {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Fighters").child(userId);
                                        Map userInfo = new HashMap<>();
                                        userInfo.put("name", name);
                                        userInfo.put("surname", surname);
                                        userInfo.put("is_active", false);
                                        userInfo.put("gender", gender);
                                        userInfo.put("region", region);
                                        userInfo.put("club", club);
                                        userInfo.put("weapons",String.join(",",weapons));
                                        userInfo.put("profileImageUrl", "default");

                                        currentUserDb.updateChildren(userInfo);
                                        Toast.makeText(RegistrationActivity.this,
                                                "Ваш профиль сохранен и направлен на проверку", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, res, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    public void searchRegions()
    {
        FirebaseDatabase.getInstance().getReference().child("Regions").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            for(DataSnapshot region : dataSnapshot.getChildren())
                            {
                                regions.add(region.getValue(String.class));
                                regionsAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });


    }

    public void searchClubs()
    {

        FirebaseDatabase.getInstance().getReference().child("Clubs").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            for(DataSnapshot club : dataSnapshot.getChildren())
                            {
                                clubs.add(club.getValue(String.class));
                                clubsAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
