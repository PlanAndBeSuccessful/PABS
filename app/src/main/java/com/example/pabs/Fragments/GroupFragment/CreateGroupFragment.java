package com.example.pabs.Fragments.GroupFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;

public class CreateGroupFragment extends Fragment {

    //
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String TAG = "CreateGroupFragment";
    static SecureRandom rnd = new SecureRandom();
    //UI
    private View containerView;
    private Button back_button;
    private Button next_button;
    private EditText name_et;
    private FrameLayout FragmentGroupContainer;
    //firebase
    private DatabaseReference reference = null;
    private final String mUID;

    public CreateGroupFragment(String uID) {
        mUID = uID;
    }

    /**
     * on fragment created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * on view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View CreateEventView = inflater.inflate(R.layout.fragment_create_group, container, false);
        containerView = getActivity().findViewById(R.id.activity_group_layout);
        //
        FragmentGroupContainer = getActivity().findViewById(R.id.fragment_group_container);

        //back button
        back_button = CreateEventView.findViewById(R.id.c_g_back_button);


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack("CreateGroupFragment", 1);
            }
        });

        //group name
        name_et = CreateEventView.findViewById(R.id.c_g_group_name_edit);

        //firebase database -> get reference to GROUP table
        reference = FirebaseDatabase.getInstance().getReference().child("GROUP");

        //next button
        next_button = CreateEventView.findViewById(R.id.c_g_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for empty fields
                if (!TextUtils.isEmpty(name_et.getText().toString())) {
                    //new Database created from field contents written in by user
                    Group group = new Group();
                    group.setGroup_name(name_et.getText().toString());
                    group.setGroup_owner(mUID);
                    group.setInvite_code(randomString(8));
                    group.setGroup_id(reference.push().getKey());

                    //pushing databaseEvent to database
                    reference.child(group.getGroup_id()).setValue(group);

                    //open event
                    openGroup(group, mUID);
                } else {
                    Toast.makeText(getActivity(), "Fields are empty!", Toast.LENGTH_SHORT).show();
                }

            }

        });

        //return view
        return CreateEventView;
    }


    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * open EventFragment with Data of created event
     */
    public void openGroup(Group group, String mUID) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_container, new GroupFragment(group, mUID))
                .addToBackStack("GroupFragment")
                .commit();
    }


    /**
     * on fragment start
     */
    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        containerView.setVisibility(View.GONE);
    }

    /**
     * on fragment stop
     */
    @Override
    public void onStop() {
        super.onStop();
        containerView.setVisibility(View.VISIBLE);
    }
}
