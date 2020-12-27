package com.example.pabs.Fragments.EventFragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pabs.HelperClass.DateInputMask;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.view.View.GONE;

/**
 * Creates a new event
 */

public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateEventFragment";

    //UI
    private View containerView;
    private Button back_button;
    private Button next_button;
    private EditText start_date_et;
    private EditText end_date_et;
    private EditText name_et;
    private Spinner dropdown;
    private Spinner group_dropdown;
    private EditText location_et;
    private FrameLayout FragmentEventContainer;

    private  ArrayList<String> availableGroups;

    private TextView groupTv;

    //firebase
    private DatabaseReference reference = null;
    private DatabaseReference databaseGroupReference;

    private final String mUID;

    public CreateEventFragment(String uID) {
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
        View CreateEventView = inflater.inflate(R.layout.fragment_create_event, container, false);
        containerView = getActivity().findViewById(R.id.activity_event_layout);
        //
        FragmentEventContainer = getActivity().findViewById(R.id.fragment_event_container);

        //back button
        back_button = CreateEventView.findViewById(R.id.c_e_back_button);


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack("CreateEventFragment", 1);
            }
        });

        //date edit text
        //start date
        start_date_et = CreateEventView.findViewById(R.id.c_e_event_start_date_edit);

        new DateInputMask(start_date_et);

        //end date
        end_date_et = CreateEventView.findViewById(R.id.c_e_event_end_date_edit);

        new DateInputMask(end_date_et);

        availableGroups = new ArrayList<>();

        //spinner
        //get the spinner from the xml.
        dropdown = CreateEventView.findViewById(R.id.c_e_public_private_spinner);
        group_dropdown = CreateEventView.findViewById(R.id.c_e_private_group_spinner);
        groupTv = CreateEventView.findViewById(R.id.c_e_public_group_text);

        //create a list of items for the spinner.
        String[] items = new String[]{"Public", "Private"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(position == 0){
                    groupTv.setVisibility(View.GONE);
                    group_dropdown.setVisibility(View.GONE);
                }else{
                    groupTv.setVisibility(View.VISIBLE);
                    group_dropdown.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Log.d("KUKA", "ASD");

        final ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, availableGroups);
        //set the spinners adapter to the previously created one.
        group_dropdown.setAdapter(groupAdapter);

        databaseGroupReference = FirebaseDatabase.getInstance().getReference().child("GROUP");
        databaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(final DataSnapshot group : snapshot.getChildren()){

                    //if he is owner
                    if ((group.child("group_owner").getValue().toString()).equals(mUID)) {
                        availableGroups.add(group.child("group_name").getValue().toString());

                        groupAdapter.notifyDataSetChanged();
                    }
                    else{
                    //if he is joined in group
                        group.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot user : snapshot.getChildren()) {
                                    if (user.getValue() != null && group.child("group_name").getValue() != null) {
                                        if ((user.getValue().toString()).equals(mUID)) {
                                            availableGroups.add(group.child("group_name").getValue().toString());
                                            break;
                                        }
                                    }
                                }
                                groupAdapter.notifyDataSetChanged();

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


        //location
        location_et = CreateEventView.findViewById(R.id.c_e_event_location_edit);

        //event name
        name_et = CreateEventView.findViewById(R.id.c_e_event_name_edit);

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("EVENT");

        //next button
        next_button = CreateEventView.findViewById(R.id.c_e_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(location_et.getText().toString())) {
                    //if location field is not empty

                    //getting lat and lng from location
                    LatLng latLng = getLocationFromAddress(getActivity(), location_et.getText().toString());

                    //if location is found
                    if (latLng != null) {
                        //getting user logged in
                        FirebaseUser fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        //check for empty fields
                        if (!TextUtils.isEmpty(name_et.getText().toString()) && !TextUtils.isEmpty(start_date_et.getText().toString()) && !TextUtils.isEmpty(end_date_et.getText().toString())) {
                            //new Database created from field contents written in by user
                            final DatabaseEvent databaseEvent = new DatabaseEvent();
                            databaseEvent.setLocation_x(latLng.latitude);
                            databaseEvent.setLocation_y(latLng.longitude);
                            databaseEvent.setEvent_name(name_et.getText().toString());
                            databaseEvent.setLocation_name(location_et.getText().toString());
                            databaseEvent.setStart_date(start_date_et.getText().toString());
                            databaseEvent.setEnd_date(end_date_et.getText().toString());
                            databaseEvent.setPriv_pub(dropdown.getSelectedItem().toString());
                            databaseEvent.setOwner_id(fireBaseUser.getUid());
                            //set basic thumbnail
                            //databaseEvent.setThumbnail("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2FNo_image_3x4.svg.png?alt=media&token=1a73a7ae-0447-4827-87c9-9ed1bb463351");

                            if(databaseEvent.getPriv_pub().equals("Private")){
                                databaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot group : snapshot.getChildren()) {
                                            if(group.child("group_name").getValue() != null){
                                                if((group.child("group_name").getValue().toString()).equals(group_dropdown.getSelectedItem().toString())){
                                                    final ArrayList<String> joined_members = new ArrayList<>();
                                                    group.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot member : snapshot.getChildren()) {
                                                                joined_members.add(member.getValue().toString());
                                                            }
                                                            databaseEvent.setJoined_members(joined_members);

                                                            //pushing databaseEvent to database
                                                            reference.push().setValue(databaseEvent);

                                                            //open event
                                                            openEvent(databaseEvent);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else{
                                //pushing databaseEvent to database
                                reference.push().setValue(databaseEvent);

                                //open event
                                openEvent(databaseEvent);
                            }


                        }

                        getAddress(latLng.latitude, latLng.longitude);

                    } else {
                        //if location is not found
                        Toast.makeText(getActivity(), "Wrong location!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //if fields are empty
                    Toast.makeText(getActivity(), "Empty Fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //return view
        return CreateEventView;
    }


    /**
     * open EventFragment with Data of created event
     */
    public void openEvent(DatabaseEvent databaseEvent) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_event_container, new EventFragment(databaseEvent, mUID))
                .addToBackStack("EventFragment")
                .commit();
    }


    /**
     * Get Address from location
     */
    public void getAddress(double lat, double lng) {
        try {
            //init
            Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            //get location address from lat lng
            List<Address> addresses = geo.getFromLocation(lat, lng, 5);
            if (addresses.isEmpty()) {
                Log.d(TAG, "Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    //if location from lat lng was found write address
                    Log.d(TAG, addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                }
            }
        } catch (Exception e) {
            //if location lat lng was not found
            Log.d(TAG, "No Location Name Found");
        }
    }

    /**
     * Get Location From Address
     */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        //init
        Geocoder coder = new Geocoder(context);
        //get executer
        ExecutorService service = Executors.newSingleThreadExecutor();
        //creating new thread
        MyThread myThread = new MyThread(strAddress, coder);
        //future variable to get the value after thread completed
        Future<LatLng> future = service.submit(myThread);
        //init p1
        LatLng p1 = null;
        try {
            //get LatLng result and give it to p1
            p1 = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //return lat lng of location
        return p1;
    }

    /**
     * on fragment start
     */
    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        containerView.setVisibility(GONE);
        Log.d(TAG, "onStart: " + getActivity().getSupportFragmentManager().getBackStackEntryCount());
    }

    /**
     * on fragment stop
     */
    @Override
    public void onStop() {
        super.onStop();
        containerView.setVisibility(View.VISIBLE);
    }

    /**
     * Thread for handling the location search, to avoid UI failure
     */
    public class MyThread implements Callable<LatLng> {

        private final String strAddress;
        private final Geocoder coder;

        public MyThread(String strAddress, Geocoder coder) {
            this.strAddress = strAddress;
            this.coder = coder;
        }

        @Override
        public LatLng call() {
            LatLng p1 = null;
            List<Address> address;
            try {
                // May throw an IOException

                //getting first 5 results of address
                address = coder.getFromLocationName(strAddress, 5);
                if (address == null) {
                    return null;
                }

                if (address.size() < 1) {
                    //if location not found
                    //Toast.makeText(context, "Invalid Location", Toast.LENGTH_SHORT).show();
                } else {
                    //get lat lng from location
                    Address location = address.get(0);
                    p1 = new LatLng(location.getLatitude(), location.getLongitude());
                }


            } catch (IOException ex) {

                ex.printStackTrace();
            }
            return p1;
        }

    }
}