package com.example.pabs.Models;

import android.net.Uri;

/**
 * Event cards
 */

public class Event {
    private String Title;
    private Uri Thumbnail;
    private String StartDate;

    public Event(){}

    public Event(String title, Uri thumbnail, String startDate) {
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

    public Uri getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getStartDate() { return StartDate; }

    public void setStartDate(String startDate) { StartDate = startDate; }
}
