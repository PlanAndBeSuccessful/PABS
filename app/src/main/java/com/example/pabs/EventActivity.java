package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pabs.Adapters.EventRecyclerViewAdapter;
import com.example.pabs.Models.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Main screen, handles more fragments, events, groups
 */

public class EventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //firebase
    private DatabaseReference reference;
    private String uID;

    //events
    List<Event> lstEvent;

    //drawer
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;

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

        lstEvent.add(new Event("Balette Eloadas", R.drawable.balette_eloadas));
        lstEvent.add(new Event("Football", R.drawable.football));
        lstEvent.add(new Event("Halloween Party", R.drawable.halloween_party));
        lstEvent.add(new Event("Hiking", R.drawable.hiking));
        lstEvent.add(new Event("Party at OFE", R.drawable.party_at_ofe));
        lstEvent.add(new Event("Wedding", R.drawable.wedding));
        lstEvent.add(new Event("Balette Eloadas", R.drawable.balette_eloadas));
        lstEvent.add(new Event("Football", R.drawable.football));
        lstEvent.add(new Event("Halloween Party", R.drawable.halloween_party));
        lstEvent.add(new Event("Hiking", R.drawable.hiking));
        lstEvent.add(new Event("Party at OFE", R.drawable.party_at_ofe));
        lstEvent.add(new Event("Wedding", R.drawable.wedding));
        lstEvent.add(new Event("Balette Eloadas", R.drawable.balette_eloadas));
        lstEvent.add(new Event("Football", R.drawable.football));
        lstEvent.add(new Event("Halloween Party", R.drawable.halloween_party));
        lstEvent.add(new Event("Hiking", R.drawable.hiking));
        lstEvent.add(new Event("Party at OFE", R.drawable.party_at_ofe));
        lstEvent.add(new Event("Wedding", R.drawable.wedding));

        //create and set RecyclerView
        RecyclerView myRv = (RecyclerView) findViewById(R.id.e_recycler_view);
        //create Adapter with lstEvent in this context
        EventRecyclerViewAdapter myAdapter = new EventRecyclerViewAdapter(this, lstEvent);
        //separate the Recyclerview to 3 columns
        myRv.setLayoutManager(new GridLayoutManager(this, 3));
        //set adapter for RecyclerView
        myRv.setAdapter(myAdapter);

        //nav view and drawer
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        //handle navigation drawer open/close with toggle
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                for(int i = 0; i < navigationView.getMenu().size(); ++i)
                    navigationView.getMenu().getItem(i).setChecked(false);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //add toggle to drawer
        drawer.addDrawerListener(toggle);

        //sync toggle
        toggle.syncState();

        // Write a string when this client loses connection
        reference.child(uID).child("online").onDisconnect().setValue("false");
    }

    /**
     * Called on selecting item from navigation list
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_events:
                Toast.makeText(this, "nav_events", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_calendar:
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
}