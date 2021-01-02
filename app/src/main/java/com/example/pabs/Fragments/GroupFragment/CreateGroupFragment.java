package com.example.pabs.Fragments.GroupFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;

/**
 * Creates a new group
 */

public class CreateGroupFragment extends Fragment {

    //static Codes
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String TAG = "CreateGroupFragment";
    static SecureRandom rnd = new SecureRandom();
    private final String mUID;
    //UI
    private View containerView;
    private Button back_button;
    private Button next_button;
    private EditText name_et;
    private FrameLayout FragmentGroupContainer;
    private ProgressDialog mDialog = null;
    //firebase
    private DatabaseReference reference = null;


    /**
     * Constructor
     */
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

        //container to replace view of activity layout with fragment layout
        FragmentGroupContainer = getActivity().findViewById(R.id.fragment_group_container);

        //firebase database -> get reference to GROUP table
        reference = FirebaseDatabase.getInstance().getReference().child("GROUP");

        //back button
        back_button = CreateEventView.findViewById(R.id.c_g_back_button);

        //back button click listener
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack("CreateGroupFragment", 1);
            }
        });

        //group name
        name_et = CreateEventView.findViewById(R.id.c_g_group_name_edit);

        //next button
        next_button = CreateEventView.findViewById(R.id.c_g_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupInDatabase();
            }

        });

        //return view
        return CreateEventView;
    }

    /**
     * create new group in database
     */
    private void createGroupInDatabase() {

        //dialog on loading
        mDialog = new ProgressDialog(getActivity());

        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        //check for empty fields
        if (!TextUtils.isEmpty(name_et.getText().toString())) {

            DatabaseReference databaseGroupReference;
            //set reference to GROUP table in database
            databaseGroupReference = FirebaseDatabase.getInstance().getReference().child("GROUP");

            databaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean group_name_is_occupied = false;

                    for (DataSnapshot user : snapshot.getChildren()) {
                        if (user.child("group_name").getValue().toString().equals(name_et.getText().toString())) {
                            group_name_is_occupied = true;
                        }
                    }

                    if (!group_name_is_occupied) {
                        //new Database created from field contents written in by user
                        Group group = new Group();
                        group.setGroup_name(name_et.getText().toString());
                        group.setGroup_owner(mUID);
                        group.setInvite_code(randomCode(8));
                        group.setGroup_id(reference.push().getKey());

                        //pushing databaseEvent to database
                        reference.child(group.getGroup_id()).setValue(group);

                        //open event
                        openGroup(group, mUID);

                        mDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Group name is occupied!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mDialog.dismiss();
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "Empty Fields!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }

    }

    /**
     * random secure Code Generator
     */
    private String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * open EventFragment with Data of created event
     */
    private void openGroup(Group group, String mUID) {
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
