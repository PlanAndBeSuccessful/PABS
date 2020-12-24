package com.example.pabs.Models;

import java.util.List;

/**
 * Class to store Events from Firebase Database
 */

public class DatabaseEvent {
    String description;
    String start_date;
    String end_date;
    String reminder;
    String repetition;
    String inv_code;
    String event_name;
    double location_x;
    double location_y;
    String location_name;
    String thumbnail;
    String priv_pub;
    List<String> staff_members;
    String owner_id;
    List<String> joined_members;

    public DatabaseEvent(){};

    public DatabaseEvent(String description, String start_date, String end_date, String reminder, String repetition, String inv_code, String event_name, double location_x, double location_y, String location_name, String thumbnail, String priv_pub, List<String> staff_members, String user_id, List<String> joined_members) {
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reminder = reminder;
        this.repetition = repetition;
        this.inv_code = inv_code;
        this.event_name = event_name;
        this.location_x = location_x;
        this.location_y = location_y;
        this.location_name = location_name;
        this.thumbnail = thumbnail;
        this.priv_pub = priv_pub;
        this.staff_members = staff_members;
        this.owner_id = user_id;
        this.joined_members = joined_members;
    }

    public List<String> getJoined_members() { return joined_members; }

    public void setJoined_members(List<String> joined_members) { this.joined_members = joined_members; }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public String getInv_code() {
        return inv_code;
    }

    public void setInv_code(String inv_code) {
        this.inv_code = inv_code;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public double getLocation_x() {
        return location_x;
    }

    public void setLocation_x(double location_x) {
        this.location_x = location_x;
    }

    public double getLocation_y() {
        return location_y;
    }

    public void setLocation_y(double location_y) {
        this.location_y = location_y;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPriv_pub() {
        return priv_pub;
    }

    public void setPriv_pub(String priv_pub) {
        this.priv_pub = priv_pub;
    }

    public List<String> getStaff_members() {
        return staff_members;
    }

    public void addToStaffListEnd(String str){
        staff_members.add(str);
    }

    public void deleteStaffListElement(String str){
        staff_members.remove(str);
    }

    public void addToJoinedListEnd(String str){
        joined_members.add(str);
    }

    public void deleteJoinedListElement(String str){
        joined_members.remove(str);
    }

    public void setStaff_members(List<String> staff_members) {
        this.staff_members = staff_members;
    }
}
