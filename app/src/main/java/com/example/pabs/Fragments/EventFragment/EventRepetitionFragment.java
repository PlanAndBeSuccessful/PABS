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

public class EventRepetitionFragment extends Fragment {

    private View containerView;

    //database event
    private final DatabaseEvent databaseEvent;

    private SwitchCompat sw;
    private ImageView iv;
    private Spinner sp;
    private TextView tv;
    private Button bt;

    private boolean switch_on = false;

    EventRepetitionFragment(DatabaseEvent dE) {
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
        View view = inflater.inflate(R.layout.fragment_event_repetition, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        sw = view.findViewById(R.id.f_e_rep_sw);
        iv = view.findViewById(R.id.f_e_rep_iv);
        sp = view.findViewById(R.id.f_e_rep_sp);
        tv = view.findViewById(R.id.f_e_rep_rl3_tv);
        bt = view.findViewById(R.id.f_e_rep_btn);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.repetition, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        sp.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        if (databaseEvent.getRepetition() != null) {
            if (!databaseEvent.getRepetition().equals("")) {
                sp.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                sw.setChecked(true);
                switch_on = true;

                ArrayList<String> repetitionArr = new ArrayList<>();
                repetitionArr.add("daily");
                repetitionArr.add("weekly");
                repetitionArr.add("monthly");
                int i = repetitionArr.indexOf(databaseEvent.getRepetition());
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
                                    databaseEvent.setRepetition(sp.getSelectedItem().toString());
                                } else {
                                    databaseEvent.setRepetition("");
                                }
                                event.getRef().child("repetition").setValue(databaseEvent.getRepetition());
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
    public void clearBackstack() {
        //clear all backstact
        getActivity().getSupportFragmentManager().popBackStack("EventRepetitionFragment", 1);
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