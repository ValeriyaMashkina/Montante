package com.example.montanteapp.Models;

import java.util.ArrayList;

public class DisplayCard {

        private String userId;
        private String nameAndSurname;
        private String club;
        private String profileImageUrl;
        private String weapons;

    public DisplayCard(String userId, String nameAndSurname, String club, String profileImageUrl, String weapons)
    {
        this.userId = userId;
        this.nameAndSurname = nameAndSurname;
        this.club = club;
        this.profileImageUrl = profileImageUrl;
        this.weapons = weapons;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNameAndSurname() { return nameAndSurname; }
    public void setNameAndSurname(String nameAndSurname) { this.nameAndSurname = nameAndSurname; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getWeapons() { return weapons; }
    public void setWeapons(String weapons) { this.weapons = weapons; }


}
