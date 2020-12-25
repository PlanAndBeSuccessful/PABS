package com.example.pabs.Models;

import java.util.ArrayList;

public class Group {
    String group_name;
    String group_owner;
    String invite_code;
    ArrayList<String> member_list;

    public Group() {
    }

    public Group(String group_name, String group_owner, String invite_code, ArrayList<String> member_list) {
        this.group_name = group_name;
        this.group_owner = group_owner;
        this.invite_code = invite_code;
        this.member_list = member_list;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_owner() {
        return group_owner;
    }

    public void setGroup_owner(String group_owner) {
        this.group_owner = group_owner;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public ArrayList<String> getMember_list() {
        return member_list;
    }

    public void setMember_list(ArrayList<String> member_list) {
        this.member_list = member_list;
    }
}
