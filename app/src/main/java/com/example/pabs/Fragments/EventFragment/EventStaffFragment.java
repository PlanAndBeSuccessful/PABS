package com.example.pabs.Fragments.EventFragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.pabs.Adapters.EventStaffRecyclerViewAdapter;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Add/kick staff from event
 */

public class EventStaffFragment extends Fragment {

    //database event
    private final DatabaseEvent databaseEvent;
    //UI
    private View containerView;
    private EditText et;
    private Button addStaffBtn;
    private Button removeStaffBtn;
    private ImageView iv;
    private EventStaffRecyclerViewAdapter myAdapter;

    /**
     * Constructor
     */
    EventStaffFragment(DatabaseEvent dE) {
        databaseEvent = dE;
    }


    /**
     * hideKeyboard
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        View view = inflater.inflate(R.layout.fragment_event_staff, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        if (databaseEvent.getStaff_members() == null) {
            ArrayList<String> temp = new ArrayList<>();
            databaseEvent.setStaff_members(temp);
        }

        //init UI
        et = view.findViewById(R.id.f_e_s_et);
        addStaffBtn = view.findViewById(R.id.f_e_s_addStaffBtn);
        removeStaffBtn = view.findViewById(R.id.f_e_s_removeStaffBtn);
        iv = view.findViewById(R.id.f_e_s_backImg);

        //add Staff button click listener
        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStaffToDatabase();
            }
        });

        //remove Staff button click listener
        removeStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeStaffFromDatabase();
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) view.findViewById(R.id.f_e_s_recyclerView);
        //create Adapter with lstEvent in this context
        myAdapter = new EventStaffRecyclerViewAdapter(getActivity(), databaseEvent.getStaff_members());
        // set layout
        myRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        //set adapter for RecyclerView
        myRv.setAdapter(myAdapter);

        //update adapter
        myAdapter.notifyDataSetChanged();

        return view;
    }

    /**
     * add staff to database of the current event
     */
    private void addStaffToDatabase() {
        if (!TextUtils.isEmpty(et.getText().toString())) {
            final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
            refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (final DataSnapshot event : snapshot.getChildren()) {
                        //Loop 1 to go through all child nodes of events
                        if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {

                            final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("USER");
                            refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    boolean foundUser = false;

                                    for (final DataSnapshot user : snapshot.getChildren()) {
                                        //Loop 1 to go through all child nodes of users
                                        if ((et.getText().toString()).equals(user.child("user_name").getValue().toString())) {
                                            if (databaseEvent.getJoined_members().contains(user.getKey())) {
                                                if (!(databaseEvent.getStaff_members().contains(user.getKey()))) {
                                                    //add member to staff
                                                    databaseEvent.addToStaffListEnd(user.getKey());
                                                    myAdapter.notifyDataSetChanged();
                                                    et.setText("");
                                                    hideKeyboard(getActivity());
                                                    event.getRef().child("staff_members").setValue(databaseEvent.getStaff_members());
                                                } else {
                                                    Toast.makeText(getActivity(), "Member is already a staff!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "User is not a member!", Toast.LENGTH_SHORT).show();
                                            }
                                            foundUser = true;
                                            break;
                                        }
                                    }
                                    if (!foundUser) {
                                        Toast.makeText(getActivity(), "User does not exist!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //canceled
                                    System.err.println("Listener was cancelled");
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //canceled
                    System.err.println("Listener was cancelled");
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please type in a staff name!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * remove staff from database from the current event
     */
    private void removeStaffFromDatabase() {
        if (!TextUtils.isEmpty(et.getText().toString())) {
            final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
            refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (final DataSnapshot event : snapshot.getChildren()) {
                        //Loop 1 to go through all child nodes of users
                        if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {

                            final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("USER");
                            refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    boolean foundUser = false;

                                    for (final DataSnapshot user : snapshot.getChildren()) {
                                        //Loop 1 to go through all child nodes of users
                                        if ((et.getText().toString()).equals(user.child("user_name").getValue().toString())) {
                                            if (databaseEvent.getJoined_members().contains(user.getKey())) {
                                                if ((databaseEvent.getStaff_members().contains(user.getKey()))) {
                                                    //remove member from staff
                                                    databaseEvent.deleteStaffListElement(user.getKey());
                                                    myAdapter.notifyDataSetChanged();
                                                    et.setText("");
                                                    hideKeyboard(getActivity());
                                                    event.getRef().child("staff_members").setValue(databaseEvent.getStaff_members());
                                                } else {
                                                    Toast.makeText(getActivity(), "Member is not a staff!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "User is not a member!", Toast.LENGTH_SHORT).show();
                                            }
                                            foundUser = true;
                                            break;
                                        }
                                    }
                                    if (!foundUser) {
                                        Toast.makeText(getActivity(), "User does not exist!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //canceled
                                    System.err.println("Listener was cancelled");
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //canceled
                    System.err.println("Listener was cancelled");
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please type in a staff name!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * clearBackstack
     */
    private void clearBackstack() {
        //clear all backstact
        getActivity().getSupportFragmentManager().popBackStack("EventStaffFragment", 1);
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