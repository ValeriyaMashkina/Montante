package com.example.montanteapp.Models;

import java.util.ArrayList;

public class User {
    private String gender;
    private boolean is_active;
    private String name;
    private String region;
    private String club;
    private String surname;
    private String weapons;
    private String profileImageUrl;



    public String getClub() {
        return club;
    }
    public String getGender() {
        return gender;
    }
    public boolean isIs_active() {
        return is_active;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public String getSurname() {
        return surname;
    }
    public String getWeapons() {
        return weapons;
    }
    public String getProfileImageUrl() { return profileImageUrl; }



    public static String checkValidInputRegistrationUser(String email, String password, String name, String surname,
                                                          String gender, String region, String club, ArrayList<String> weapons)
    {
        if (email.trim().length()==0)
        {return "Не указан почтовый ящик";}
        else if (!email.trim().contains("@"))
        {return "Указан некорректный почтовый ящик";}
        else if (password.trim().length()==0)
        {return "Не указан пароль";}
        else if (password.trim().length()<6)
        {return "Укажите пароль длиннее 5 символов";}
        else if (name.trim().length()==0)
        {return "Не указано имя";}
        else if (surname.trim().length()==0)
        {return "Не указана фамилия";}
        else if (gender==null || gender.trim().length()==0)
        {return "Не указан пол";}
        else if (region.trim().length()==0)
        {return "Не указан регион";}
        else if (club.trim().length()==0)
        {return "Не указан клуб";}
        else if (weapons.size()==0)
        {return "Не указан ни один вид оружия";}
        else {return "ОК";}
    }


    public static String checkValidSettingsUser(String name, String surname, String region, String club, ArrayList<String> weapons)
    {
        if (name.trim().length()==0)
        {return "Не указано имя";}
        else if (surname.trim().length()==0)
        {return "Не указана фамилия";}
        else if (region.trim().length()==0)
        {return "Не указан регион";}
        else if (club.trim().length()==0)
        {return "Не указан клуб";}
        else if (weapons.size()==0)
        {return "Не указан ни один вид оружия";}
        else {return "ОК";}
    }
}
