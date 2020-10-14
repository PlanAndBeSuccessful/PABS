package com.example.pabs.Models;

public class User {
    private String userID;
    private String e_mail;
    private String nickname;
    private String password;
    private String user_name;

    public User(){
    }

    public User(String userID, String e_mail, String nickname, String password, String user_name) {
        this.userID = userID;
        this.e_mail = e_mail;
        this.nickname = nickname;
        this.password = password;
        this.user_name = user_name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
