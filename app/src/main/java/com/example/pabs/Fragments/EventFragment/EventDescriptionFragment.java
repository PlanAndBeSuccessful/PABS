package com.example.pabs.Fragments.EventFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Sets description of event
 */

public class EventDescriptionFragment extends Fragment {

    //database event
    private final DatabaseEvent databaseEvent;
    //UI
    private View containerView;
    private TextView tv;
    private EditText et;
    private ImageView iv;
    private Button setDescBtn;

    /**
     * Constructor
     */
    EventDescriptionFragment(DatabaseEvent dE) {
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
        View view = inflater.inflate(R.layout.fragment_event_description, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        //init view
        tv = view.findViewById(R.id.f_e_d_tv);
        et = view.findViewById(R.id.f_e_d_et);
        iv = view.findViewById(R.id.f_e_d_backImg);
        setDescBtn = view.findViewById(R.id.f_e_d_setDescBtn);

        //if description is not null in databaseEvent
        if (databaseEvent.getDescription() != null) {
            et.setText(databaseEvent.getDescription());
        }

        //set click listener to exit image
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        //set Description-set click listener
        setDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set description
                setDescriptionInDatabase();
            }
        });

        return view;
    }

    /**
     * Change description of event in database
     */
    private void setDescriptionInDatabase() {
        //reference to CHILD table in database
        final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
        //add single value event listener
        refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //loop through events
                for (DataSnapshot event : snapshot.getChildren()) {
                    //if current event is found
                    if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {
                        //set description to databaseEvent
                        databaseEvent.setDescription(et.getText().toString());

                        //set description in database
                        refEvent.child(event.getKey()).child("description").setValue(databaseEvent.getDescription());
                    }
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
        getActivity().getSupportFragmentManager().popBackStack("EventDescriptionFragment", 1);
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