package com.example.pabs.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pabs.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private String uID;
    private View listView;

    public SettingsFragment(String uid) {
        uID = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //reference to User
        final DatabaseReference referenceUsr = FirebaseDatabase.getInstance().getReference().child("USER").child(uID);

        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        listView = getActivity().findViewById(R.id.activity_event_layout);

        Button sett_back = settingsView.findViewById(R.id.settings_back_btn);
        Button sett_confirm = settingsView.findViewById(R.id.settings_confirm_btn);
        final EditText sett_nname = settingsView.findViewById(R.id.settings_nname);

        sett_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(sett_nname.getText().toString())){
                    referenceUsr.child("nickname").setValue(sett_nname.getText().toString());
                }
            }
        });

        sett_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear all backstack
                clearBackstack();
            }
        });
        return settingsView;
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

    /**
     * clearBackstack
     */
    public void clearBackstack() {
        //clear all backstact
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getActivity().getSupportFragmentManager().popBackStack("SettingsFragment", 1);
        } else {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}