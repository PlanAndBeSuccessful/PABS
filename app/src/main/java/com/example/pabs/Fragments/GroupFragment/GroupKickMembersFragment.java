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

import com.example.pabs.Adapters.EventStaffRecyclerViewAdapter;
import com.example.pabs.Adapters.GroupMemberRecyclerViewAdapter;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupKickMembersFragment extends Fragment {
    private View containerView;


    private EditText et;
    private Button removeMemberBtn;
    private ImageView iv;
    private GroupMemberRecyclerViewAdapter myAdapter;
    //database event
    private final Group mGroup;

    GroupKickMembersFragment(Group grp) {
        mGroup = grp;
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_kick_members, container, false);
        containerView = getActivity().findViewById(R.id.activity_group_layout);

        if(mGroup.getMember_list() == null){
            ArrayList<String> temp = new ArrayList<>();
            mGroup.setMember_list(temp);
        }

        et = view.findViewById(R.id.f_g_k_m_et);
        removeMemberBtn = view.findViewById(R.id.f_g_k_m_removeStaffBtn);
        iv = view.findViewById(R.id.f_g_k_m_backImg);

        removeMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(et.getText().toString())) {
                    final DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference().child("GROUP");
                    refGroup.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (final DataSnapshot group : snapshot.getChildren()) {
                                //Loop 1 to go through all child nodes of users
                                if (group.child("group_name").getValue() == mGroup.getGroup_name()) {
                                    final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("USER");
                                    refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            boolean foundUser = false;

                                            for (final DataSnapshot user : snapshot.getChildren()) {
                                                //Loop 1 to go through all child nodes of users
                                                if((et.getText().toString()).equals(user.child("user_name").getValue().toString())){
                                                    if(mGroup.getMember_list().contains(user.getKey())){
                                                            mGroup.deleteMemberListElement(user.getKey());
                                                            myAdapter.notifyDataSetChanged();
                                                            et.setText("");
                                                            hideKeyboard(getActivity());
                                                            group.getRef().child("joined_members").setValue(mGroup.getMember_list());
                                                    }
                                                    else{
                                                        Toast.makeText(getActivity(), "User is not a member!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    foundUser = true;
                                                    break;
                                                }
                                            }
                                            if(!foundUser){
                                                Toast.makeText(getActivity(), "User does not exist!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //database failed
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Please type in a staff name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
            }
        });

        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) view.findViewById(R.id.f_g_k_m_recyclerView);
        //create Adapter with lstEvent in this context
        myAdapter = new GroupMemberRecyclerViewAdapter(getActivity(), mGroup.getMember_list());
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
        getActivity().getSupportFragmentManager().popBackStack("GroupKickMembersFragment", 1);
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
