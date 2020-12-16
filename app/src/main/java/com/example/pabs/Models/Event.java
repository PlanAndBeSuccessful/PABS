package com.example.pabs.Models;

import android.net.Uri;

import java.util.List;

/**
 * Event cards
 */

public class Event {
    private String Title;
    private Uri Thumbnail;

    public Event(){}

    public Event(String title, Uri thumbnail, String startDate, List<String> j_members) {
        Title = title;
        Thumbnail = thumbnail;
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

}
