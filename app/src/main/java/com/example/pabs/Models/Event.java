package com.example.pabs.Models;

/**
 * Event cards
 */

public class Event {
    private String Title;
    private int Thumbnail;
    private String StartDate;

    public Event(){}

    public Event(String title, int thumbnail, String startDate) {
        Title = title;
        Thumbnail = thumbnail;
        StartDate = startDate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getStartDate() { return StartDate; }

    public void setStartDate(String startDate) { StartDate = startDate; }
}
