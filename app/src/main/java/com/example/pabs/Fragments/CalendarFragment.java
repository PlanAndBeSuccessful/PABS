package com.example.pabs.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pabs.Adapters.EventRecyclerViewAdapter;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.Models.Event;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

public class CalendarFragment extends Fragment {

    //calendar
    sun.bob.mcalendarview.views.ExpCalendarView customCalendar;

    //TextViews
    TextView curr_month, currDate;

    Context mContext;
    //List<DatabaseEvent> lstDatabaseEvent;

    private View listView;

    //firebase
    private String uID;

    //events
    List<Event> lstEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View calendarView = inflater.inflate(R.layout.fragment_calendar, container, false);

        listView = getActivity().findViewById(R.id.activity_event_layout);

        //setting the current date in CalendarView
        setCurrDateInCalendarFragment(calendarView);

        //getting the custom calendar view object
        customCalendar = calendarView.findViewById(R.id.calendar_calendar);

        //setting the current month and year in CalendarView
        curr_month = calendarView.findViewById(R.id.calendar_curr_month);
        int cmonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        int cyear = Calendar.getInstance().get(Calendar.YEAR);
        setCurrMonthandYear(curr_month, cmonth, cyear);

        customCalendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                setCurrMonthandYear(curr_month, month, year);
            }
        });

        //set data for events example
        lstEvent = new ArrayList<>();

        //Getting events from database and setting them to recyclerview
        DatabaseReference databaseEvents;
        databaseEvents = FirebaseDatabase.getInstance().getReference().child("EVENT");

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearEvents();
                for (DataSnapshot event : snapshot.getChildren()) {
                    //Loop 1 to go through all child nodes of events
                    String e_name = event.child("event_name").getValue().toString();
                    String event_startdate = event.child("start_date").getValue().toString();

                    //creating a temporary event object
                    Uri myUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2FNo_image_3x4.svg.png?alt=media&token=1a73a7ae-0447-4827-87c9-9ed1bb463351");
                    Event tempEv = new Event(e_name, myUri, event_startdate);

                    //pushing the temporary event object into an arraylist
                    addToEventsArray(tempEv);
                }
                //marking the Dates on which we have Events
                markEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return calendarView;
    }

    private void markEvents() {
        for(Event i : lstEvent){
            DateData temp = convertDate(i.getStartDate());
            customCalendar.markDate(temp.setMarkStyle(MarkStyle.LEFTSIDEBAR,Color.BLUE));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        listView.setVisibility(View.VISIBLE);
    }

    private void setCurrDateInCalendarFragment(View calendarView){
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String curr_date = df.format(d);
        currDate = calendarView.findViewById(R.id.calendar_curr_date);
        currDate.setText(curr_date);
    }

    private void setCurrMonthandYear(TextView curr_month, int month, int year){
        curr_month.setText(getMonth(month) + " " + year);
    }

    private DateData convertDate(String date){
        StringTokenizer token = new StringTokenizer(date,"/");
        int nap=0,honap=0,ev=0;
        int cnt = 0;
        while(token.hasMoreTokens()){
            if(cnt == 0) {
                nap = Integer.parseInt(token.nextToken());
                cnt++;
            }
            else if(cnt == 1){
                honap = Integer.parseInt(token.nextToken());
                cnt++;
            }
            else if(cnt == 2){
                ev = Integer.parseInt(token.nextToken());
                cnt++;
            }
        }
        DateData dateData = new DateData(ev,honap,nap);
        return dateData;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    public void addToEventsArray(Event tempEv){
        lstEvent.add(tempEv);
    }

    public void clearEvents(){
        lstEvent.clear();
    }
}