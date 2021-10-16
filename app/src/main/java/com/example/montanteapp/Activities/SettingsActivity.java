package com.example.montanteapp.Activities;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.montanteapp.Models.User;
import com.example.montanteapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mSurnameField;
    private ArrayAdapter regionsAdapter;
    List<String> regions;
    private ArrayAdapter clubsAdapter;
    List<String> clubs;
    private Spinner mRegionSpinner, mClubSpinner;


    private CheckBox boxLongsword, boxSwordBackler, boxRapierDaga, boxRapierSolo,boxSable, boxMontante;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name, surname, region, club, weapons, profileImageUrl;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Fighters").child(userId);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mNameField = (EditText) findViewById(R.id.name);
        mSurnameField = (EditText) findViewById(R.id.surname);


        searchRegions();
        regions = new ArrayList<String>();
        regionsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, regions);
        mRegionSpinner = (Spinner) findViewById(R.id.regionSpinner);
        mRegionSpinner.setAdapter(regionsAdapter);

        searchClubs();
        clubs = new ArrayList<String>();
        clubsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, clubs);
        mClubSpinner = (Spinner) findViewById(R.id.clubSpinner);
        mClubSpinner.setAdapter(clubsAdapter);

        boxLongsword = (CheckBox)findViewById(R.id.boxLongsword);
        boxSwordBackler = (CheckBox)findViewById(R.id.boxSwordBackler);
        boxRapierDaga = (CheckBox)findViewById(R.id.boxRapierDaga);
        boxRapierSolo = (CheckBox)findViewById(R.id.boxRapierSolo);
        boxSable = (CheckBox)findViewById(R.id.boxSable);
        boxMontante = (CheckBox)findViewById(R.id.boxMontante);



        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
                return;
            }
        });

        //checkAndDisplay(SettingsActivity.this, userId);
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
    private void getUserInfo()
    {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    name = user.getName();
                    mNameField.setText(name);

                    surname = user.getSurname();
                    mSurnameField.setText(surname);

                    region = user.getRegion();
                    int i = regions.indexOf(region);
                    if (i > -1) {
                        mRegionSpinner.setSelection(i);
                    }


                    else {
                        mRegionSpinner.setSelection(0);
                    }

                    club = user.getClub();
                    int j = clubs.indexOf(club);
                    if (j > -1) {
                        mClubSpinner.setSelection(j);
                    }

                    else {
                        mClubSpinner.setSelection(0);
                    }

                    weapons = user.getWeapons();
                    if (weapons.contains("Длинный меч")) {
                        boxLongsword.setChecked(true);
                    }

                    if (weapons.contains("Меч и баклер")) {
                        boxSwordBackler.setChecked(true);
                    }

                    if (weapons.contains("Рапира и дага")) {
                        boxRapierDaga.setChecked(true);
                    }

                    if (weapons.contains("Рапира solo")) {
                        boxRapierSolo.setChecked(true);
                    }

                    if (weapons.contains("Военная сабля")) {
                        boxSable.setChecked(true);
                    }

                    if (weapons.contains("Монтанте")) {
                        boxMontante.setChecked(true);
                    }

                    Glide.clear(mProfileImage);
                    profileImageUrl = user.getProfileImageUrl();

                    if (profileImageUrl.equals("default"))
                    {
                        Glide.with(getApplication()).load(R.drawable.fencer).into(mProfileImage);
                    }

                    else
                    {
                        Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    private void saveUserInformation()
    {

        name = mNameField.getText().toString();
        surname = mSurnameField.getText().toString();
        region = mRegionSpinner.getSelectedItem().toString();
        club = mClubSpinner.getSelectedItem().toString();

        ArrayList<String> w = new ArrayList<String> ();
        if(boxLongsword.isChecked()) {w.add("Длинный меч");}
        if(boxSwordBackler.isChecked()) {w.add("Меч и баклер");}
        if(boxRapierDaga.isChecked()) {w.add("Рапира и дага");}
        if(boxRapierSolo.isChecked()) {w.add("Рапира solo");}
        if(boxSable.isChecked()) {w.add("Военная сабля");}
        if(boxMontante.isChecked()) {w.add("Монтанте");}

        String res = User.checkValidSettingsUser(name, surname, region, club, w);
        if (res.equals("ОК"))
        {
            Map userInfo = new HashMap();
            userInfo.put("name", name);
            userInfo.put("surname", surname);
            userInfo.put("region", region);
            userInfo.put("club", club);
            userInfo.put("weapons", String.join(",", w));
            mUserDatabase.updateChildren(userInfo);
            if(resultUri != null)
            {
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                }
                catch (IOException e) { e.printStackTrace(); }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        finish();
                    }
                });

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoLink = uri.toString();
                                Map userInfo = new HashMap();
                                userInfo.put("profileImageUrl", photoLink);
                                mUserDatabase.updateChildren(userInfo);
                            }
                        });
                        Toast.makeText(SettingsActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
            else
            { Toast.makeText(SettingsActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        else
        { Toast.makeText(SettingsActivity.this, res, Toast.LENGTH_SHORT).show(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
