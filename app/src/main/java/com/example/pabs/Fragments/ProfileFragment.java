package com.example.pabs.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private String uID;
    private View listView;

    public ProfileFragment(String uid){
        uID = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("USER");

        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        listView = getActivity().findViewById(R.id.activity_event_layout);

        Button prof_back = profileView.findViewById(R.id.profile_back_btn);
        final TextView prof_email = profileView.findViewById(R.id.profile_email);
        final TextView prof_uname = profileView.findViewById(R.id.profile_username);
        final TextView prof_nname = profileView.findViewById(R.id.profile_nickname);

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot usr : snapshot.getChildren()) {
                    if(usr.getKey().equals(uID)) {
                        prof_email.setText(usr.child("e_mail").getValue(String.class));
                        prof_uname.setText(usr.child("user_name").getValue(String.class));
                        prof_nname.setText(usr.child("nickname").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        prof_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear all backstack
                clearBackstack();
            }
        });

        return profileView;
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
            getActivity().getSupportFragmentManager().popBackStack("ProfileFragment", 1);
        } else {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}