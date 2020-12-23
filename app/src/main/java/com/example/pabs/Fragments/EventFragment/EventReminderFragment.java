package com.example.pabs.Fragments.EventFragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pabs.Adapters.EventStaffRecyclerViewAdapter;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventReminderFragment extends Fragment {

    private View containerView;

    //database event
    private DatabaseEvent databaseEvent;

    private SwitchCompat sw;
    private ImageView iv;
    private Spinner sp;
    private TextView tv;
    private Button bt;

    private boolean switch_on = false;

    EventReminderFragment(DatabaseEvent dE){
        databaseEvent = dE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_reminder, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        sw = view.findViewById(R.id.f_e_r_sw);
        iv = view.findViewById(R.id.f_e_r_iv);
        sp = view.findViewById(R.id.f_e_r_sp);
        tv = view.findViewById(R.id.f_e_r_rl3_tv);
        bt = view.findViewById(R.id.f_e_r_btn);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminder, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        sp.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        if(databaseEvent.getReminder() != null){
            if(!databaseEvent.getReminder().equals("")){
                sp.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                sw.setChecked(true);
                switch_on = true;

                ArrayList<String> reminderArr = new ArrayList<>();
                reminderArr.add("daily");
                reminderArr.add("weekly");
                reminderArr.add("monthly");
                int i=reminderArr.indexOf(databaseEvent.getReminder().toString());
                sp.setSelection(i);
            }
        }



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

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
                    refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (final DataSnapshot event : snapshot.getChildren()) {
                                //Loop 1 to go through all child nodes of users
                                if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {
                                    if (switch_on) {
                                        databaseEvent.setReminder(sp.getSelectedItem().toString());
                                    } else {
                                        databaseEvent.setReminder("");
                                    }
                                    event.getRef().child("reminder").setValue(databaseEvent.getReminder());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //database failed
                        }
                    });

            }
        });

        return view;
    }


    /**
     * clearBackstack
     */
    public void clearBackstack(){
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