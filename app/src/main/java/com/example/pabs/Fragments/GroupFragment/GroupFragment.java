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

/**
 * Handle group, OptionDialogFragment to access UI and override the functions in it, show/send text messages
 */

public class GroupFragment extends Fragment implements GroupOptionsDialogFragment.GroupOptionsDialogListener {

    //Static code
    private static final String TAG = "GroupFragment";
    //helper variables
    private final Group mGroup;
    private final String mUID;
    //UI
    private Button back_button;
    private Button plus_button;
    private View containerView;
    private TextView group_name_tv;
    private TextView group_owner_tv;
    private FloatingActionButton fab;
    private EditText input_et;
    private int mState;
    private String nickname;
    private String group_owner_nickname;

    //firebase
    private FirebaseListAdapter<ChatMessage> adapter;

    /**
     * Constructor
     */
    public GroupFragment(Group grp, String uID) {
        mGroup = grp;
        mUID = uID;
    }

    /**
     * get status of current user
     */
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

        //container to replace view of activity layout with fragment layout
        containerView = getActivity().findViewById(R.id.activity_group_layout);

        //init UI
        group_name_tv = view.findViewById(R.id.f_g_group_name);
        group_owner_tv = view.findViewById(R.id.f_g_owner_name);
        input_et = (EditText) view.findViewById(R.id.f_g_rlChat_et);
        fab = (FloatingActionButton) view.findViewById(R.id.f_g_rlChat_sendBtn);
        ListView listOfMessages = (ListView) view.findViewById(R.id.f_g_rlChat_lv);
        back_button = view.findViewById(R.id.f_g_back_button);
        plus_button = view.findViewById(R.id.f_g_plus_button);

        //get status of current user
        mState = getStatus();

        //set nickname of current user from database
        setCurrentUserNicknameFromDatabase();

        //floating button click listener, send message to database
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToDatabase();
            }
        });

        //trigger send button on pressing next
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

        //set adapter for messages incoming
        receiveMessagesInAdapter();

        //set adapter for list of messages
        listOfMessages.setAdapter(adapter);

        //setting text in UI with Group data
        group_name_tv.setText(mGroup.getGroup_name());

        //back button
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear all backstack
                clearBackstack();
            }
        });

        //plus button to open more options
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGroupOptionsDialogFragment();
            }
        });

        return view;
    }

    /**
     * Receive messages in adapter
     */
    private void receiveMessagesInAdapter() {
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
    }

    /**
     * Send ChatMessage type to database and store it as a message for this group
     */
    private void sendMessageToDatabase() {
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

    /**
     * Set current user nickname from database
     */
    private void setCurrentUserNicknameFromDatabase() {
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

    /**
     * delete Group from database
     */
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

    /**
     * kick members from group
     */
    @Override
    public void KickMembers() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_container, new GroupKickMembersFragment(mGroup))
                .addToBackStack("GroupKickMembersFragment")
                .commit();
    }

    /**
     * show events where are group members are joined
     */
    @Override
    public void GroupEvents() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_container, new GroupEventsFragment(mGroup))
                .addToBackStack("GroupEventsFragment")
                .commit();
    }

    /**
     * leave from group
     */
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
                //canceled
                System.err.println("Listener was cancelled");
            }
        });
    }

    /**
     * show code to send to others who want to join
     */
    @Override
    public void ShowCode() {
        ShowCodeDialogFragment showCodeDialogFragment = new ShowCodeDialogFragment(mGroup.getInvite_code());
        showCodeDialogFragment.show(getActivity().getSupportFragmentManager(), "showCodeDialogFragment");
    }
}
