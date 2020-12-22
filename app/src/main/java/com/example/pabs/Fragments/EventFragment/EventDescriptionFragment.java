package com.example.pabs.Fragments.EventFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDescriptionFragment extends Fragment {

    private View containerView;

    private TextView tv;
    private EditText et;
    private ImageView iv;
    private Button setDescBtn;

    //database event
    private DatabaseEvent databaseEvent;

    EventDescriptionFragment(DatabaseEvent dE){
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
        View view = inflater.inflate(R.layout.fragment_event_description, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        tv = view.findViewById(R.id.f_e_d_tv);
        et = view.findViewById(R.id.f_e_d_et);
        iv = view.findViewById(R.id.f_e_d_backImg);
        setDescBtn = view.findViewById(R.id.f_e_d_setDescBtn);

        if(databaseEvent.getDescription() != null){
            et.setText(databaseEvent.getDescription());
        }

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        setDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
                refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot event : snapshot.getChildren()) {
                            //Loop 1 to go through all child nodes of users
                            if(event.child("event_name").getValue() == databaseEvent.getEvent_name()){
                                databaseEvent.setDescription(et.getText().toString());
                                refEvent.child(event.getKey()).child("description").setValue(databaseEvent.getDescription());
                                Log.d("EDF", "onDataChange: Successful");
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