package com.example.montanteapp.Models;

public class Rival {

    private String userId;
    private String nameAndSurname;
    private String region;
    private String club;
    private String weapons;
    private String profileImageUrl;
    private boolean isPending;
    private boolean isConfirmed;
    private String challengeDate;

    public Rival(String userId, String nameAndSurname, String region,
                 String club, String weapons, String profileImageUrl,
                 boolean isPending, boolean isConfirmed, String challengeDate) {
        this.userId = userId;
        this.nameAndSurname = nameAndSurname;
        this.region = region;
        this.club = club;
        this.weapons = weapons;
        this.profileImageUrl = profileImageUrl;
        this.isPending = isPending;
        this.isConfirmed = isConfirmed;
        this.challengeDate = challengeDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public void setNameAndSurname(String nameAndSurname) {
        this.nameAndSurname = nameAndSurname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getWeapons() {
        return weapons;
    }

    public void setWeapons(String weapons) {
        this.weapons = weapons;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getChallengeDate() {
        return challengeDate;
    }

    public void setChallengeDate(String challengeDate) {
        this.challengeDate = challengeDate;
    }
}
