package com.example.montanteapp.Models;

public class ChatCaption
{
    private String chatId;
    private String userId;
    private String userName;
    private String userSurname;
    private String profileImageUrl;

    public ChatCaption(String chatId, String userId, String userName, String userSurname, String profileImageUrl) {
        this.chatId = chatId;
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.profileImageUrl = profileImageUrl;
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserSurname() { return userSurname; }
    public void setUserSurname(String userSurname) { this.userSurname = userSurname; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
