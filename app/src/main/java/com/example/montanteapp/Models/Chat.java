package com.example.montanteapp.Models;

public class Chat
{
    private String message;
    private Boolean currentUser;

    public Chat(String message, Boolean currentUser)
    {
        this.message = message;
        this.currentUser = currentUser;
    }

    public String getMessage(){ return message; }
    public void setMessage(String userID){ this.message = message; }
    public Boolean getCurrentUser(){ return currentUser; }
    public void setCurrentUser(Boolean currentUser){ this.currentUser = currentUser; }

    public static String makeChatId (String currentUserId, String rivalId)
    {
        if (currentUserId.compareTo(rivalId) > 0)
        {
            return (currentUserId + rivalId);
        }

        else {return (rivalId + currentUserId);}
    }
}
