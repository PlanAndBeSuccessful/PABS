package com.example.pabs.Fragments.EventFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Set reminder of event
 */

public class EventReminderFragment extends Fragment {

    //database event
    private final DatabaseEvent databaseEvent;
    //UI
    private View containerView;
    private SwitchCompat sw;
    private ImageView iv;
    private Spinner sp;
    private TextView tv;
    private Button bt;
    //helper variables
    private boolean switch_on = false;

    /**
     * Constructor
     */
    EventReminderFragment(DatabaseEvent dE) {
        databaseEvent = dE;
    }

    /**
     * onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_reminder, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        //init UI
        sw = view.findViewById(R.id.f_e_r_sw);
        iv = view.findViewById(R.id.f_e_r_iv);
        sp = view.findViewById(R.id.f_e_r_sp);
        tv = view.findViewById(R.id.f_e_r_rl3_tv);
        bt = view.findViewById(R.id.f_e_r_btn);

        //set click listener of exit image
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        //set adapter for reminder
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminder, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        //init visibility
        sp.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        //if reminder is not null
        if (databaseEvent.getReminder() != null) {
            if (!databaseEvent.getReminder().equals("")) {
                //if reminder has options set
                sp.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                sw.setChecked(true);
                switch_on = true;

                ArrayList<String> reminderArr = new ArrayList<>();
                reminderArr.add("daily");
                reminderArr.add("weekly");
                reminderArr.add("monthly");
                int i = reminderArr.indexOf(databaseEvent.getReminder());
                sp.setSelection(i);
            }
        }


        //set on checked change listener for switch
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //switch on
                    sp.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    switch_on = true;
                } else {
                    //switch off
                    sp.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    switch_on = false;
                }
            }
        });

        //confirm button
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChangesToDatabase();
            }
        });

        return view;
    }

    /**
     * send Changes to Database
     */
    private void sendChangesToDatabase() {
        //reference to EVENT table in database
        final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
        //listener for single value event
        refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if current event is not deleted
                if (snapshot.exists()) {
                    for (final DataSnapshot event : snapshot.getChildren()) {
                        //Loop 1 to go through all child nodes of events
                        if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {
                            //if switch is on
                            if (switch_on) {
                                //set reminder to databaseEvent
                                databaseEvent.setReminder(sp.getSelectedItem().toString());
                            }
                            //if switch is off
                            else {
                                //set reminder to databaseEvent
                                databaseEvent.setReminder("");
                            }
                            //set value of reminder in database
                            event.getRef().child("reminder").setValue(databaseEvent.getReminder());
                        }
                    }
                }
                //if event is deleted
                else {
                    Toast.makeText(getActivity(), "Event has been deleted!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //canceled
                System.err.println("Listener was cancelled");
            }
        });
    }

    /**
     * clearBackstack
     */
    public void clearBackstack() {
        //clear all backstact
        getActivity().getSupportFragmentManager().popBackStack("EventReminderFragment", 1);
    }

    /**
     * onStart
     */
    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        containerView.setVisibility(View.GONE);
    }

    /**
     * onStop
     */
    @Override
    public void onStop() {
        super.onStop();
        containerView.setVisibility(View.VISIBLE);
    }
}