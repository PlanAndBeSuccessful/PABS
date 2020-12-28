package com.example.pabs.Fragments.GroupFragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Adapters.GroupEventsRecyclerVewAdapter;
import com.example.pabs.Adapters.GroupMemberRecyclerViewAdapter;
import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupEventsFragment extends Fragment {
    private View containerView;

    private ImageView iv;

    private GroupEventsRecyclerVewAdapter myAdapter;
    //database event
    private final Group mGroup;

    private ArrayList<String> all_members;
    private ArrayList<String> events;

    GroupEventsFragment(Group grp) {
        mGroup = grp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_events, container, false);
        containerView = getActivity().findViewById(R.id.activity_group_layout);

        if(mGroup.getMember_list() == null){
            ArrayList<String> temp = new ArrayList<>();
            mGroup.setMember_list(temp);
        }

        all_members = new ArrayList<>();
        events = new ArrayList<>();

        iv = view.findViewById(R.id.f_g_e_backImg);

        all_members.add(mGroup.getGroup_owner());
        all_members.addAll(mGroup.getMember_list());

        for(String x : all_members){
            Log.d("ASDASDASD", "all_members: " + x);
        }

        final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
        refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot event : snapshot.getChildren()) {
                    final ArrayList<String> event_all_members = new ArrayList<>();

                    event_all_members.add(event.child("owner_id").getValue().toString());

                    event.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot member : snapshot.getChildren()) {
                                event_all_members.add(member.getValue().toString());
                            }

                            for(String x : event_all_members){
                                Log.d("ASDASD", "event_all_members: " + x);
                            }

                            if(event_all_members.containsAll(all_members)){
                                Log.d("ASDASD", "event.getKey(): " + event.getKey());
                                events.add(event.getKey());
                                myAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //database failed
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) view.findViewById(R.id.f_g_e_recyclerView);
        //create Adapter with lstEvent in this context
        myAdapter = new GroupEventsRecyclerVewAdapter(getActivity(), events);
        // set layout
        myRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        //set adapter for RecyclerView
        myRv.setAdapter(myAdapter);

        myAdapter.notifyDataSetChanged();

        return view;
    }

    /**
     * clearBackstack
     */
    public void clearBackstack() {
        //clear all backstact
        getActivity().getSupportFragmentManager().popBackStack("GroupEventsFragment", 1);
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
