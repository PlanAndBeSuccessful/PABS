package com.example.pabs.Fragments.GroupFragment;

import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pabs.Models.Group;
import com.example.pabs.R;

public class GroupFragment extends Fragment {
    private static final String TAG = "GroupFragment";
    //UI
    private Button back_button;
    private Button plus_button;
    private View containerView;
    private TextView group_name_tv;
    private TextView group_owner_tv;
    private TextView chat_output_tv;

    private Group mGroup;
    private String mUID;
    /**
     * Constructor
     */
    public GroupFragment(Group grp, String uID) {
        mGroup = grp;
        mUID = uID;
    }

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On Create View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        containerView = getActivity().findViewById(R.id.activity_group_layout);

        //init UI
        group_name_tv = view.findViewById(R.id.fg_group_name);
        group_owner_tv = view.findViewById(R.id.fg_owner_name);
        chat_output_tv = view.findViewById(R.id.fg_chat_output);

        chat_output_tv.setMovementMethod(new ScrollingMovementMethod());


        //setting text in UI with Group data
        group_name_tv.setText(mGroup.getGroup_name());
        group_owner_tv.setText(mGroup.getGroup_owner());

        //back button
        back_button = view.findViewById(R.id.fg_back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear all backstack
                clearBackstack();
            }
        });

        //delete button to delete event
        plus_button = view.findViewById(R.id.fg_plus_button);

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // openGroupOptionsDialogFragment();
            }
        });

        return view;
    }

    /**
     * clearBackstack
     */
    public void clearBackstack(){
        //clear all backstact
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getActivity().getSupportFragmentManager().popBackStack("GroupFragment", 1);
        } else {
            for(int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
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
