package com.example.pabs.Models;

/**
 * Event cards
 */

public class Event {
    private String Title;
    private int Thumbnail;

    public Event(){}

    public Event(String title, int thumbnail) {
        Title = title;
        Thumbnail = thumbnail;
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
}
