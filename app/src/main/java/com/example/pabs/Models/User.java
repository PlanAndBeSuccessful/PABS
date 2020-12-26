package com.example.pabs.Models;

/**
 * User from database
 */

public class User {
    private String token;
    private String e_mail;
    private String nickname;
    private String user_name;
    private String online;

    public User() {
    }

    public User(String token, String e_mail, String nickname, String user_name, String online) {
        this.token = token;
        this.e_mail = e_mail;
        this.nickname = nickname;
        this.user_name = user_name;
        this.online = online;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
