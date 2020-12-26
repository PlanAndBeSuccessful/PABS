package com.example.pabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
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
    private ImageView open_group_btn;

    //firebase
    private DatabaseReference reference;
    private String uID;

    //events
    private List<Event> lstEvent;

    //drawer
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;

    //
    private SearchView sw;
    private EventRecyclerViewAdapter myEventAdapter;

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
     * On create
     */
    @SuppressLint("ClickableViewAccessibility")
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

        //Getting events from database and setting them to recyclerview
        DatabaseReference databaseEvents;
        databaseEvents = FirebaseDatabase.getInstance().getReference().child("EVENT");

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearEvents();
                for (DataSnapshot event : snapshot.getChildren()) {
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
                    Uri testUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2FNo_image_3x4.svg.png?alt=media&token=1a73a7ae-0447-4827-87c9-9ed1bb463351");

                    //Create temporary Event
                    Event tempEv;

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

                    //add events to array
                    addToEventsArray(tempEv);
                }

                //Set and show events on main screen
                setEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        create_event_img_btn = findViewById(R.id.a_e_create_event_button);
        create_event_img_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    openCreateEventFragment();
                }
                return false;
            }
        });
        //

        //open group button
        open_group_btn = findViewById(R.id.a_e_open_group_button);
        open_group_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    clearBackstack();
                    openGroupActivity();
                }
                return false;
            }
        });

        //search view
        sw = findViewById(R.id.e_search_bar);
        sw.setQueryHint("Search event name...");

        sw.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideKeyboard(EventActivity.this);
                return false;
            }
        });

        sw.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myEventAdapter.filter(query);
                hideKeyboard(EventActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myEventAdapter.filter(newText);
                return true;
            }
        });

    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Add to events inside DataChanged method so we don't lose the results
     */
    public void addToEventsArray(Event tempEv) {
        lstEvent.add(tempEv);
    }

    /**
     * Clear events inside DataChanged method
     */
    public void clearEvents() {
        lstEvent.clear();
    }

    /**
     * Set events inside DataChanged method
     */
    public void setEvents() {
        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) findViewById(R.id.e_recycler_view);
        //create Adapter with lstEvent in this context
        myEventAdapter = new EventRecyclerViewAdapter(this, lstEvent, getSupportFragmentManager(), uID);
        //separate the Recyclerview to 3 columns
        myRv.setLayoutManager(new GridLayoutManager(this, 3));
        //set adapter for RecyclerView
        myRv.setAdapter(myEventAdapter);
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

    private void clearBackstack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * open group activity
     */
    private void openGroupActivity() {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("USER", uID);
        finish();
        startActivity(intent);
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
     * open calendar event fragment
     */
    private void openCalendarFragment() {
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