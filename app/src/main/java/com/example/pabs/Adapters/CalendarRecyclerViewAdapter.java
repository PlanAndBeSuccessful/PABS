package com.example.pabs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;

import java.util.List;

/**
 * Creates an array of card elements
 */

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.ViewHolder> {

    //Store a member variable for the events
    private final List<DatabaseEvent> member_Events;

    //Pass-in the contact array into the constructor
    public CalendarRecyclerViewAdapter(List<DatabaseEvent> events) {
        member_Events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate custom layout
        View calendar_listview = inflater.inflate(R.layout.calendarfragment_list_row, parent, false);
        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(calendar_listview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data model based on position
        DatabaseEvent ev = member_Events.get(position);

        //set item views based on views and data model
        TextView textView = holder.event_name;
        textView.setText(ev.getEvent_name());
        ImageView imgView = holder.event_picture;
    }

    @Override
    public int getItemCount() {
        return member_Events.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView event_name;
        public ImageView event_picture;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            event_name = (TextView) itemView.findViewById(R.id.eventName);
            event_picture = (ImageView) itemView.findViewById(R.id.icon_list_row_calendarfragment);
        }
    }
}
