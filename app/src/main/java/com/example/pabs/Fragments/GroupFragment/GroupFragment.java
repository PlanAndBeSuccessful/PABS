package com.example.pabs.Fragments.GroupFragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pabs.Fragments.EventFragment.EventReminderFragment;
import com.example.pabs.Models.ChatMessage;
import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GroupFragment extends Fragment implements GroupOptionsDialogFragment.GroupOptionsDialogListener {
    private static final String TAG = "GroupFragment";
    //UI
    private Button back_button;
    private Button plus_button;
    private View containerView;
    private TextView group_name_tv;
    private TextView group_owner_tv;
    private FloatingActionButton fab;

    private final Group mGroup;
    private final String mUID;
    private int mState;
    private String nickname;
    private String group_owner_nickname;

    private EditText input_et;

    private FirebaseListAdapter<ChatMessage> adapter;

    /**
     * Constructor
     */
    public GroupFragment(Group grp, String uID) {
        mGroup = grp;
        mUID = uID;
    }

    private int getStatus() {
        if (mUID.equals(mGroup.getGroup_owner())) {
            //owner
            return 0;
        } else {
            //member
            return 1;
        }
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
        group_name_tv = view.findViewById(R.id.f_g_group_name);
        group_owner_tv = view.findViewById(R.id.f_g_owner_name);
        input_et = (EditText) view.findViewById(R.id.f_g_rlChat_et);

        mState = getStatus();

        //Getting events from database and setting them to recyclerview
        DatabaseReference databaseUserRef;
        databaseUserRef = FirebaseDatabase.getInstance().getReference().child("USER");

        databaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if ((user.getKey()).equals(mUID)) {
                        nickname = user.child("nickname").getValue().toString();
                    }
                    //nickname of group owner
                    if ((user.getKey()).equals(mGroup.getGroup_owner())) {
                        group_owner_nickname = user.child("nickname").getValue().toString();
                        group_owner_tv.setText(group_owner_nickname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        fab = (FloatingActionButton) view.findViewById(R.id.f_g_rlChat_sendBtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child("CHAT").child(mGroup.getGroup_id())
                        .push()
                        .setValue(new ChatMessage(input_et.getText().toString(), nickname)
                        );

                // Clear the input
                input_et.setText("");
            }
        });

        input_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //Perform your Actions here.
                    fab.performClick();
                }
                return handled;
            }
        });

        ListView listOfMessages = (ListView) view.findViewById(R.id.f_g_rlChat_lv);

        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("CHAT").child(mGroup.getGroup_id())) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

        //setting text in UI with Group data
        group_name_tv.setText(mGroup.getGroup_name());

        //back button
        back_button = view.findViewById(R.id.f_g_back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear all backstack
                clearBackstack();
            }
        });

        //plus button to open more options
        plus_button = view.findViewById(R.id.f_g_plus_button);

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGroupOptionsDialogFragment();
            }
        });

        return view;
    }

    /**
     * clearBackstack
     */
    public void clearBackstack() {
        //clear all backstact
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getActivity().getSupportFragmentManager().popBackStack("GroupFragment", 1);
        } else {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    /**
     * open group dialog fragment
     */
    private void openGroupOptionsDialogFragment() {
        GroupOptionsDialogFragment groupOptionsDialogFragment = new GroupOptionsDialogFragment();
        groupOptionsDialogFragment.setListener(GroupFragment.this, mState);
        groupOptionsDialogFragment.setCancelable(false);
        groupOptionsDialogFragment.show(getActivity().getSupportFragmentManager(), "groupDialogFragment");
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

    @Override
    public void CloseGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("GROUP").orderByChild("group_name").equalTo(mGroup.getGroup_name());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    //delete selected event
                    appleSnapshot.getRef().removeValue();

                    //clear it from backstack
                    clearBackstack();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //database failed
                Log.e("GroupFragment", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void KickMembers() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_container, new GroupKickMembersFragment(mGroup))
                .addToBackStack("GroupKickMembersFragment")
                .commit();
    }

    @Override
    public void GroupEvents() {

    }

    @Override
    public void LeaveGroup() {
        final DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference().child("GROUP");
        refGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot group : snapshot.getChildren()) {
                    //Loop 1 to go through all child nodes of users
                    if (group.child("group_name").getValue() == mGroup.getGroup_name()) {
                            mGroup.deleteMemberListElement(mUID);
                            group.getRef().child("joined_members").setValue(mGroup.getMember_list());
                            clearBackstack();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void ShowCode() {
        ShowCodeDialogFragment showCodeDialogFragment = new ShowCodeDialogFragment(mGroup.getInvite_code());
        showCodeDialogFragment.show(getActivity().getSupportFragmentManager(), "showCodeDialogFragment");
    }
}
