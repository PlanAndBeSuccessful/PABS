package com.example.pabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Adapters.EventRecyclerViewAdapter;
import com.example.pabs.Fragments.CalendarFragment;
import com.example.pabs.Fragments.EventFragment.CreateEventFragment;
import com.example.pabs.Fragments.MyToDoFragment;
import com.example.pabs.Fragments.ProfileFragment;
import com.example.pabs.Fragments.SettingsFragment;
import com.example.pabs.Models.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Main screen, handles more fragments, events, groups
 */

public class EventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //UI
    private ImageView create_event_img_btn;
    private ImageView open_group_button;
    private ImageView show_my_events_button;
    private SearchView sw;
    private EventRecyclerViewAdapter myAdapter;
    //drawer
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;

    //firebase
    private DatabaseReference reference;
    private String uID;

    //events
    private List<Event> lstEvent;

    //helper variables
    private int mState = 0;

    /**
     * hide Keyboard
     */
    private static void hideKeyboard(Activity activity) {
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
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //get uid of logged in user
        uID = getIntent().getStringExtra("USER");

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("USER");

        //set data for events example
        lstEvent = new ArrayList<>();

        //setting lstEvent to recyclerview
        setEvents();

        //update list of events from database whenever a change occurs in it
        updateEventListFromDatabaseOnChange();

        //nav view and drawer
        navigationView = findViewById(R.id.a_e_nav_view);
        drawer = findViewById(R.id.a_e_drawer_layout);

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

        // Get reference to database so we can now if client is still connected
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

        //create event button + click listener
        create_event_img_btn = findViewById(R.id.a_e_create_event_button);
        create_event_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
                openCreateEventFragment();
            }
        });

        //open group button + click listener
        open_group_button = findViewById(R.id.a_e_open_group_button);
        open_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackstack();
                openGroupActivity();
            }
        });

        //show my events button + click listener
        show_my_events_button = findViewById(R.id.a_e_show_my_events_button);
        show_my_events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState == 0) {
                    //if public events are shown and want to update to private events
                    mState = 1;
                    updateEventViewFromDatabase();
                    setEvents();
                    //Todo: Change ICON
                    show_my_events_button.setImageResource(R.drawable.availableevents);
                    return;
                }
                if (mState == 1) {
                    //if private events are shown and want to update to public events
                    mState = 0;
                    updateEventViewFromDatabase();
                    setEvents();
                    //Todo: Change ICON
                    show_my_events_button.setImageResource(R.drawable.myeventsbutton);
                    return;
                }
            }
        });

        //search bar
        sw = findViewById(R.id.e_search_bar);
        sw.setQueryHint("Search event name...");

        sw.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideKeyboard(EventActivity.this);
                return false;
            }
        });

        //when something is being typed in search bar
        sw.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myAdapter.filter(query);
                hideKeyboard(EventActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.filter(newText);
                return true;
            }
        });

    }

    /**
     * Called when Event is database is changed/updated
     */
    private void updateEventListFromDatabaseOnChange() {
        //Getting events from database
        DatabaseReference databaseEventsRef;
        databaseEventsRef = FirebaseDatabase.getInstance().getReference().child("EVENT");

        //Value event listener for handling changes in EVENT table whenever activity is active
        databaseEventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear event lstEvent
                clearEvents();
                for (final DataSnapshot event : snapshot.getChildren()) {
                    //Loop 1 to go through all child nodes of events

                    //get event name in temp
                    String temp = event.child("event_name").getValue().toString();

                    //init Uri
                    Uri myUri = null;
                    String UriStr = null;

                    //if the event has a thumbnail, get Uri
                    if (event.child("thumbnail").getValue() != null) {
                        UriStr = event.child("thumbnail").getValue().toString();
                        myUri = Uri.parse(UriStr);
                    }

                    //get no image Uri
                    Uri testUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2Fno-image-found-360x250.png?alt=media&token=77870c1c-7a00-4f6b-ba33-1f8c9abc6b73");

                    //Create temporary Event
                    final Event tempEv;

                    //if Event has no thumbnail
                    if (UriStr == null) {
                        //Give the event, the no image thumbnail
                        tempEv = new Event();
                        tempEv.setTitle(temp);
                        tempEv.setThumbnail(testUri);
                    }
                    //if Event has thumbnail
                    else {
                        //Set thumbnail of event
                        tempEv = new Event();
                        tempEv.setTitle(temp);
                        tempEv.setThumbnail(myUri);
                    }

                    //if event is public and view is set to public events
                    if ((event.child("priv_pub").getValue().toString()).equals("Public") && mState == 0) {
                        //add events to array
                        addToEventsArray(tempEv);
                        //if event is created by logged in user and view is set to private events
                    } else if ((event.child("owner_id").getValue().toString()).equals(uID) && mState == 1) {
                        //add events to array
                        addToEventsArray(tempEv);
                    } else {
                        //if event is private
                        if (mState == 1) {
                            //get reference to joined members of event
                            event.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //loop through members of event
                                    for (DataSnapshot member : snapshot.getChildren()) {
                                        if (member.getValue().toString().equals(uID)) {
                                            //add events to array
                                            addToEventsArray(tempEv);
                                            myAdapter.notifyDataSetChanged();
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
                        }
                    }
                }
                //Set and show events on main screen
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //canceled
                System.err.println("Listener was cancelled");
            }
        });
    }

    /**
     * Called when user changes view from public events to MyEvents, vice-versa
     */
    private void updateEventViewFromDatabase() {
        DatabaseReference databaseEventsRef;
        databaseEventsRef = FirebaseDatabase.getInstance().getReference().child("EVENT");
        databaseEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearEvents();
                for (final DataSnapshot event : snapshot.getChildren()) {
                    //Loop 1 to go through all child nodes of events
                    String temp = event.child("event_name").getValue().toString();

                    Uri myUri = null;
                    String UriStr = null;

                    //if the event has a thumbnail, get Uri
                    if (event.child("thumbnail").getValue() != null) {
                        UriStr = event.child("thumbnail").getValue().toString();
                        myUri = Uri.parse(UriStr);
                    }

                    //get no image Uri
                    Uri testUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2Fno-image-found-360x250.png?alt=media&token=77870c1c-7a00-4f6b-ba33-1f8c9abc6b73");

                    //Create temporary Event
                    final Event tempEv;

                    //if Event has no thumbnail
                    if (UriStr == null) {
                        //Give the event, the no image thumbnail
                        tempEv = new Event();
                        tempEv.setTitle(temp);
                        tempEv.setThumbnail(testUri);
                    }
                    //if Event has thumbnail
                    else {
                        //Set thumbnail of event
                        tempEv = new Event();
                        tempEv.setTitle(temp);
                        tempEv.setThumbnail(myUri);
                    }

                    if ((event.child("priv_pub").getValue().toString()).equals("Public") && mState == 0) {
                        //add events to array
                        addToEventsArray(tempEv);
                    } else if ((event.child("owner_id").getValue().toString()).equals(uID) && mState == 1) {
                        addToEventsArray(tempEv);
                    } else {
                        if (mState == 1) {
                            event.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot member : snapshot.getChildren()) {
                                        if (member.getValue().toString().equals(uID)) {
                                            addToEventsArray(tempEv);
                                            //Set and show events on main screen
                                            myAdapter.notifyDataSetChanged();
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
                        }
                    }

                }
                //Set and show events on main screen
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //canceled
                System.err.println("Listener was cancelled");
            }
        });
    }

    /**
     * Open Login Activity
     */
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Open Group Activity
     */
    private void openGroupActivity() {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("USER", uID);
        finish();
        startActivity(intent);
    }

    /**
     * Add to events inside DataChanged method so we don't lose the results
     */
    private void addToEventsArray(Event tempEv) {
        lstEvent.add(tempEv);
    }

    /**
     * Clear events inside DataChanged method
     */
    private void clearEvents() {
        lstEvent.clear();
    }

    /**
     * Set events inside DataChanged method
     */
    private void setEvents() {
        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) findViewById(R.id.e_recycler_view);
        //create Adapter with lstEvent in this context
        myAdapter = new EventRecyclerViewAdapter(this, lstEvent, getSupportFragmentManager(), uID);
        //separate the Recyclerview to 3 columns
        myRv.setLayoutManager(new GridLayoutManager(this, 3));
        //set adapter for RecyclerView
        myRv.setAdapter(myAdapter);
    }

    /**
     * Called on selecting item from navigation list
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_events:
                clearBackstack();
                Toast.makeText(this, "nav_events", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_calendar:
                clearBackstack();
                openCalendarFragment();
                Toast.makeText(this, "nav_calendar", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_groups:
                clearBackstack();
                openGroupActivity();
                Toast.makeText(this, "nav_groups", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_profile:
                clearBackstack();
                openProfileFragment();
                Toast.makeText(this, "nav_profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                clearBackstack();
                openSettingsFragment();
                Toast.makeText(this, "nav_settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_todo:
                clearBackstack();
                openMyToDoFragment();
                Toast.makeText(this, "nav_todo", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                clearBackstack();
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

    /**
     * Clear all BackStack
     */
    private void clearBackstack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
    }


    /**
     * open create event fragment
     */
    private void openCreateEventFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new CreateEventFragment(uID))
                .addToBackStack("CreateEventFragment")
                .commit();
    }

    /**
     * open MyToDo fragment
     */
    private void openMyToDoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new MyToDoFragment())
                .addToBackStack("MyToDoFragment")
                .commit();
    }

    /**
     * open calendar fragment
     */
    private void openCalendarFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new CalendarFragment())
                .addToBackStack("CalendarFragment")
                .commit();
    }

    /**
     * open Profile fragment
     */
    private void openProfileFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new ProfileFragment(uID))
                .addToBackStack("ProfileFragment")
                .commit();
    }

    /**
     * open Settings fragment
     */
    private void openSettingsFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new SettingsFragment(uID))
                .addToBackStack("SettingsFragment")
                .commit();
    }

    /**
     * Called when the activity is exiting
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        lstEvent.clear();
        reference.child(uID).child("online").setValue("false");
    }

    /**
     * Called when the activity started
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

}