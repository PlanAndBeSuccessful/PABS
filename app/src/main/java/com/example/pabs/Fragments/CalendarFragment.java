package com.example.pabs.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Adapters.CalendarRecyclerViewAdapter;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class CalendarFragment extends Fragment {

    //calendar
    sun.bob.mcalendarview.views.ExpCalendarView customCalendar;

    //Views
    TextView curr_month, currDate;
    Button back_btn;

    Context mContext;
    //List<DatabaseEvent> lstDatabaseEvent;
    //events
    List<DatabaseEvent> lstEvent;
    private View listView;
    //firebase
    private String uID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get uid of logged in user
        uID = getActivity().getIntent().getStringExtra("USER");

        // Inflate the layout for this fragment
        View calendarView = inflater.inflate(R.layout.fragment_calendar, container, false);

        listView = getActivity().findViewById(R.id.activity_event_layout);
        final RecyclerView rvCalendarfragment = (RecyclerView) calendarView.findViewById(R.id.calendar_rec_view);

        //setting the current date in CalendarView
        setCurrDateInCalendarFragment(calendarView);

        //getting the back button object
        back_btn = calendarView.findViewById(R.id.calendar_back_btn);

        //getting the custom calendar view object
        customCalendar = calendarView.findViewById(R.id.calendar_calendar);

        //setting the current month and year in CalendarView
        curr_month = calendarView.findViewById(R.id.calendar_curr_month);
        int cmonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
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

        //databaseEvents.addValueEventListener(new ValueEventListener() {
        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstEvent.clear();
                for (final DataSnapshot event : snapshot.getChildren()) {
                    final DatabaseEvent tempEv = new DatabaseEvent();

                    //Loop 1 to go through all child nodes of events


                    final List<String> joined_users = new ArrayList<>();
                    event.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            joined_users.clear();
                            if(uID.equals(event.child("owner_id").getValue(String.class))){
                                joined_users.add(event.child("owner_id").getValue(String.class));
                            }
                            else {
                                for (DataSnapshot users : snapshot.getChildren()) {
                                    //Loop 1 to go through all child nodes of joined members
                                    if (uID.equals(users.getValue(String.class))) {
                                        joined_users.add(users.getValue(String.class));
                                    }
                                }
                            }


                            final Handler handler = new Handler();
                            final int delay = 1000; //milliseconds

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (!joined_users.isEmpty())//checking if the data is loaded or not
                                    {

                                        String e_name = event.child("event_name").getValue().toString();
                                        tempEv.setEvent_name(e_name);
                                        String event_startdate = event.child("start_date").getValue().toString();
                                        tempEv.setStart_date(event_startdate);
                                        tempEv.setOwner_id(event.child("owner_id").getValue(String.class));
                                        joined_users.add(tempEv.getOwner_id());
                                        tempEv.setJoined_members(joined_users);


                                        //pushing the temporary event object into an arraylist
                                        lstEvent.add(tempEv);
                                        /*for(DatabaseEvent i: lstEvent){
                                            Log.d("dbEvent", "run: " + i.getEvent_name() + ", " + i.getStart_date());
                                        }
                                        Log.d("dbEvent", "run: ");*/
                                        for (DatabaseEvent i : lstEvent) {
                                            /*if (uID.equals(i.getOwner_id())) {
                                                DateData temp = convertDate(i.getStart_date());
                                                customCalendar.markDate(temp.setMarkStyle(MarkStyle.LEFTSIDEBAR, Color.CYAN));
                                            }*/
                                            Log.d("tagitnowornever", "run: "+i.getEvent_name() + ", " + i.getStart_date());
                                            for (String j : i.getJoined_members()) {
                                                if (uID.equals(j)) {
                                                    //marking the Dates on which we have Events
                                                    //Log.d("cldr", "run: task " + j);
                                                    //Log.d("cldr", "run: " + i.getEvent_name());
                                                    DateData temp = convertDate(i.getStart_date());
                                                    customCalendar.markDate(temp.setMarkStyle(MarkStyle.LEFTSIDEBAR, Color.BLUE));
                                                    break;
                                                }
                                            }
                                        }
                                        Log.d("tagitnowornever", "run: ");

                                        final List<DatabaseEvent> onDateEvents = new ArrayList<>();
                                        customCalendar.setOnDateClickListener(new OnDateClickListener() {
                                            @Override
                                            public void onDateClick(View view, DateData clickedDate) {
                                                onDateEvents.clear();
                                                Log.d("DateClicked", "onDateClick: " + clickedDate);
                                                for (DatabaseEvent i : lstEvent) {
                                                    if (convertDate(i.getStart_date()).equals(clickedDate)) {
                                                        onDateEvents.add(i);
                                                    }
                                                }
                                                CalendarRecyclerViewAdapter adapter = new CalendarRecyclerViewAdapter(onDateEvents);
                                                rvCalendarfragment.setAdapter(adapter);
                                                rvCalendarfragment.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            }
                                        });
                                    } else
                                        handler.postDelayed(this, delay);
                                }
                            }, delay);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear all backstack
                clearBackstack();
            }
        });
        return calendarView;
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

    private void setCurrDateInCalendarFragment(View calendarView) {
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String curr_date = df.format(d);
        currDate = calendarView.findViewById(R.id.calendar_curr_date);
        currDate.setText(curr_date);
    }

    private void setCurrMonthandYear(TextView curr_month, int month, int year) {
        curr_month.setText(getMonth(month) + " " + year);
    }

    private DateData convertDate(String date) {
        StringTokenizer token = new StringTokenizer(date, "/");
        int nap = 0, honap = 0, ev = 0;
        int cnt = 0;
        while (token.hasMoreTokens()) {
            if (cnt == 0) {
                nap = Integer.parseInt(token.nextToken());
                cnt++;
            } else if (cnt == 1) {
                honap = Integer.parseInt(token.nextToken());
                cnt++;
            } else if (cnt == 2) {
                ev = Integer.parseInt(token.nextToken());
                cnt++;
            }
        }
        DateData dateData = new DateData(ev, honap, nap);
        return dateData;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public void clearBackstack() {
        //clear all backstact
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getActivity().getSupportFragmentManager().popBackStack("CalendarFragment", 1);
        } else {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}