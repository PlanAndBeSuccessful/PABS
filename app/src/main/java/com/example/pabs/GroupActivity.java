package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pabs.Adapters.GroupRecyclerViewAdapter;
import com.example.pabs.Fragments.CalendarFragment;
import com.example.pabs.Fragments.GroupFragment.CodeDialogFragment;
import com.example.pabs.Fragments.GroupFragment.CreateGroupFragment;
import com.example.pabs.Models.Group;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CodeDialogFragment.CodeDialogListener {

    //UI
    private ImageView create_group_img_btn;
    private ImageView open_event_img_btn;
    private ImageView group_code_img_btn;

    //firebase
    private DatabaseReference reference;
    private String uID;

    //events
    private ArrayList<Group> lstGroup = new ArrayList<>();

    //drawer
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;

    private String mCode;

    //
    private SearchView sw;
    private GroupRecyclerViewAdapter myGroupAdapter;

    /**
     * On create
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //get uid of logged in user
        uID = getIntent().getStringExtra("USER");

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("USER");

        //set data for events example
        lstGroup = new ArrayList<>();

        //Getting events from database and setting them to recyclerview
        DatabaseReference databaseGroupRef;
        databaseGroupRef = FirebaseDatabase.getInstance().getReference().child("GROUP");

        databaseGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearEvents();
                for (DataSnapshot group : snapshot.getChildren()) {

                    //Create temporary Group
                    final Group tempGrp;
                    tempGrp = new Group();

                    tempGrp.setGroup_name(group.child("group_name").getValue().toString());
                    tempGrp.setGroup_owner(group.child("group_owner").getValue().toString());
                    tempGrp.setInvite_code(group.child("invite_code").getValue().toString());

                    final ArrayList<String> joined_members =  new ArrayList<>();
                    group.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot member : snapshot.getChildren()){
                                joined_members.add(member.getValue().toString());
                            }

                            tempGrp.setMember_list(joined_members);

                            if(joined_members.contains(uID) || uID.equals(tempGrp.getGroup_owner())){
                                //add events to array
                                addToGroupArray(tempGrp);

                                //Set and show events on main screen
                                setGroups();
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

            }
        });

        //nav view and drawer
        navigationView = findViewById(R.id.a_g_nav_view);
        drawer = findViewById(R.id.a_g_drawer_layout);

        //handle navigation drawer open/close with toggle
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                for (int i = 0; i < navigationView.getMenu().size(); ++i)
                    navigationView.getMenu().getItem(i).setChecked(false);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //add toggle to drawer
        drawer.addDrawerListener(toggle);

        //sync toggle
        toggle.syncState();

        // Write a string to database when this client loses connection
        reference.child(uID).child("online").onDisconnect().setValue("false");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //user is connected
                } else {
                    //user disconnected
                    reference.child(uID).child("online").onDisconnect().setValue("false");
                    openLoginActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        //create event button
        create_group_img_btn = findViewById(R.id.a_g_create_group_button);
        create_group_img_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    openCreateGroupFragment();
                }
                return false;
            }
        });
        //
        open_event_img_btn = findViewById(R.id.a_g_open_event_button);
        open_event_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
                openEventActivity();
            }
        });

        group_code_img_btn = findViewById(R.id.a_g_group_code_button);
        group_code_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCodeDialogFragment();
            }
        });

        //
        sw = findViewById(R.id.g_search_bar);
        sw.setQueryHint("Search group name...");

        sw.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myGroupAdapter.filter(query);
                hideKeyboard(GroupActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myGroupAdapter.filter(newText);
                return true;
            }
        });

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

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openCodeDialogFragment(){
        CodeDialogFragment codeDialogFragment = new CodeDialogFragment();
        codeDialogFragment.show(getSupportFragmentManager(),"codeDialogFragment");
    }

    /**
     * Add to events inside DataChanged method so we don't lose the results
     */
    public void addToGroupArray(Group tempEv){
        lstGroup.add(tempEv);
    }

    /**
     * Clear events inside DataChanged method
     */
    public void clearEvents(){
        lstGroup.clear();
    }

    /**
     * Set events inside DataChanged method
     */
    public void setGroups(){
        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) findViewById(R.id.g_recycler_view);
        //create Adapter with lstEvent in this context
        myGroupAdapter = new GroupRecyclerViewAdapter(this, lstGroup, getSupportFragmentManager(), uID);
        //separate the Recyclerview to 3 columns
        myRv.setLayoutManager(new GridLayoutManager(this, 3));
        //set adapter for RecyclerView
        myRv.setAdapter(myGroupAdapter);
    }

    /**
     * Called on selecting item from navigation list
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_events:
                clearBackstack();
                openEventActivity();
                Toast.makeText(this, "nav_events", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_calendar:
                clearBackstack();
                openCalendarFragment();
                Toast.makeText(this, "nav_calendar", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_groups:
                Toast.makeText(this, "nav_groups", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "nav_profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "nav_settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "nav_logout", Toast.LENGTH_SHORT).show();
                reference.child(uID).child("online").setValue("false");
                finish();
                break;

            default:
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                break;
        }

        //close drawer on item clicked
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearBackstack(){
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * open event activity
     */
    private void openEventActivity(){
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("USER", uID);
        startActivity(intent);
    }

    /**
     * open create event fragment
     */
    private void openCreateGroupFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_container, new CreateGroupFragment(uID))
                .addToBackStack("CreateGroupFragment")
                .commit();
    }

    /**
     * open calendar event fragment
     */
    private void openCalendarFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new CalendarFragment())
                .addToBackStack("CalendarFragment")
                .commit();
    }

    /**
     * Called when the activity is exiting
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        lstGroup.clear();
        reference.child(uID).child("online").setValue("false");
    }

    /**
     * Called when the activity started
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void applyCode(String code) {
        mCode = code;

        //Getting events from database and setting them to recyclerview
        DatabaseReference databaseGroupRef;
        databaseGroupRef = FirebaseDatabase.getInstance().getReference().child("GROUP");

        databaseGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearEvents();
                for (final DataSnapshot group : snapshot.getChildren()) {

                    if ((group.child("invite_code").getValue().toString()).equals(mCode)) {
                        //Create temporary Group
                        final Group tempGrp;
                        tempGrp = new Group();

                        tempGrp.setGroup_name(group.child("group_name").getValue().toString());
                        tempGrp.setGroup_owner(group.child("group_owner").getValue().toString());
                        tempGrp.setInvite_code(group.child("invite_code").getValue().toString());

                        final ArrayList<String> joined_members = new ArrayList<>();
                        group.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot member : snapshot.getChildren()) {
                                    joined_members.add(member.getValue().toString());
                                }

                                joined_members.add(uID);

                                group.getRef().child("joined_members").setValue(joined_members);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}