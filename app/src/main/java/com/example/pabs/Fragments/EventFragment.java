package com.example.pabs.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Handle an event
 */

public class EventFragment extends Fragment implements OnMapReadyCallback {

    //UI
    private Button back_button;
    private Button delete_button;
    private View containerView;

    private MapView mapView;

    private TextView event_name_tv;
    private TextView event_date_start_tv;
    private TextView event_date_end_tv;
    private TextView event_description_tv;
    private TextView location_text_tv;

    //map
    private GoogleMap mMap;

    //database event
    private DatabaseEvent databaseEvent;

    /**
     * Constructor
     */
    public EventFragment(DatabaseEvent dbE) {
        databaseEvent = dbE;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);

        //init UI
        event_name_tv = view.findViewById(R.id.fe_event_name);
        event_date_start_tv = view.findViewById(R.id.fe_event_date_start);
        event_date_end_tv = view.findViewById(R.id.fe_event_date_end);
        event_description_tv = view.findViewById(R.id.fe_event_description);
        location_text_tv = view.findViewById(R.id.fe_location_text);

        //setting text in UI with databaseEvent data
        event_name_tv.setText(databaseEvent.getEvent_name());
        event_date_start_tv.setText(databaseEvent.getStart_date());
        event_date_end_tv.setText(databaseEvent.getEnd_date());
        event_description_tv.setText(databaseEvent.getDescription());
        location_text_tv.setText(databaseEvent.getLocation_name());

        //back button
        back_button = view.findViewById(R.id.fe_back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().popBackStack("EventFragment", 1);
                //clear all backstack
                clearBackstack();
            }
        });

        //delete button
        delete_button = view.findViewById(R.id.fe_plus_button);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("EVENT").orderByChild("event_name").equalTo(databaseEvent.getEvent_name());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            clearBackstack();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("EventFragment", "onCancelled", databaseError.toException());
                    }
                });
            }
        });

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.fe_map);
        mapView.onCreate(savedInstanceState);

        //Sync Map View
        mapView.getMapAsync(this);

        return view;
    }

    public void clearBackstack(){
        //clear all backstack
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getActivity().getSupportFragmentManager().popBackStack("EventFragment", 1);
        } else {
            for(int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    /**
     * onMapReady
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //get lat lng from databaseEvent
        LatLng eventLocation = new LatLng(databaseEvent.getLocation_x(), databaseEvent.getLocation_y());

        //init mMap
        mMap = googleMap;

        //add marker to map to location from databaseEvent
        mMap.addMarker(new MarkerOptions()
                .position(eventLocation)
                .title(databaseEvent.getLocation_name()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 10));

        //set on Map Click Listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getActivity(), "Clicked on map", Toast.LENGTH_SHORT).show();
            }
        });
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
     * onResume
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * on fragment stop
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * onLowMemory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}