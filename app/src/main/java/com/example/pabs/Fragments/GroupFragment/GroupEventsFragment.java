package com.example.pabs.Fragments.GroupFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Adapters.GroupEventsRecyclerVewAdapter;
import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * View Events where all group members are joined
 */

public class GroupEventsFragment extends Fragment {
    //data
    private final Group mGroup;
    //UI
    private View containerView;
    private ImageView iv;
    private GroupEventsRecyclerVewAdapter myAdapter;
    private ArrayList<String> all_members;
    private ArrayList<String> events;

    /**
     * Constructor
     */
    GroupEventsFragment(Group grp) {
        mGroup = grp;
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
        View view = inflater.inflate(R.layout.fragment_group_events, container, false);

        //container to replace view of activity layout with fragment layout
        containerView = getActivity().findViewById(R.id.activity_group_layout);

        //check if member list of group is empty or not
        if (mGroup.getMember_list() == null) {
            ArrayList<String> temp = new ArrayList<>();
            mGroup.setMember_list(temp);
        }

        //init UI
        iv = view.findViewById(R.id.f_g_e_backImg);

        //init arrays
        all_members = new ArrayList<>();
        events = new ArrayList<>();

        //add group members + owner to all members
        all_members.add(mGroup.getGroup_owner());
        all_members.addAll(mGroup.getMember_list());


        findEventsOfThisGroupFromDatabase();

        //exit image click listener
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
     * find events where all members of current group are members of it
     */
    private void findEventsOfThisGroupFromDatabase() {
        //reference to EVENT table in database
        final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
        refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot event : snapshot.getChildren()) {
                    final ArrayList<String> event_all_members = new ArrayList<>();

                    //add owner to event all members
                    event_all_members.add(event.child("owner_id").getValue().toString());

                    event.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot member : snapshot.getChildren()) {
                                //get all members from event
                                event_all_members.add(member.getValue().toString());
                            }

                            if (event_all_members.containsAll(all_members)) {
                                //if event members + owner contains everyone from the group, add this event to events array
                                events.add(event.getKey());
                                myAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //canceled
                            System.err.println("Listener was cancelled");
                        }
                    });
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
