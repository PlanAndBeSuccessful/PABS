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
import com.example.pabs.Models.ToDoList;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    //helper variables
    private final String mUID;
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
    private TextView groupTv;
    private ArrayAdapter<String> groupAdapter;
    //list
    private ArrayList<String> availableGroups;
    //firebase
    private DatabaseReference reference = null;
    private DatabaseReference databaseGroupReference;
    private DatabaseReference referenceToDO;

    /**
     * Constructor
     */
    public CreateEventFragment(String uID) {
        mUID = uID;
    }

    /**
     * Verifies if startDate is after\equal or before endDate
     */
    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            String myFormatString = "yyyy/MM/dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            return date1.after(startingDate) || date1.equals(startingDate);
        } catch (Exception e) {

            return false;
        }
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

        //container which the fragment is kept in
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

        //set on item selected listener to spinner dropdown
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // on item selected
                if (position == 0) {
                    //if public selected
                    groupTv.setVisibility(View.GONE);
                    group_dropdown.setVisibility(View.GONE);
                } else {
                    //if private selected
                    groupTv.setVisibility(View.VISIBLE);
                    group_dropdown.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // on nothing selected
            }

        });

        //adapter to show groups that the user is joined in
        groupAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, availableGroups);
        //set the spinners adapter to the previously created one.
        group_dropdown.setAdapter(groupAdapter);

        //reference to To Do table in database
        referenceToDO = FirebaseDatabase.getInstance().getReference().child("TODO");

        //reference to Group table in database
        databaseGroupReference = FirebaseDatabase.getInstance().getReference().child("GROUP");

        //get available groups
        getAvailableGroups();

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
                createEventInDatabase();
            }
        });

        //return view
        return CreateEventView;
    }

    /**
     * get Available Groups to set data in spinner
     */
    private void getAvailableGroups() {
        //listener for single value event for databaseGroupReference
        databaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //loop through groups
                for (final DataSnapshot group : snapshot.getChildren()) {

                    //if he is owner
                    if ((group.child("group_owner").getValue().toString()).equals(mUID)) {

                        //add group to availableGroups
                        availableGroups.add(group.child("group_name").getValue().toString());

                        //update adapter
                        groupAdapter.notifyDataSetChanged();
                    } else {
                        //if he is joined in group
                        group.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot member : snapshot.getChildren()) {
                                    //if getValues are not null
                                    if (member.getValue() != null && group.child("group_name").getValue() != null) {
                                        //if member was found in group
                                        if ((member.getValue().toString()).equals(mUID)) {
                                            //add group to availableGroups
                                            availableGroups.add(group.child("group_name").getValue().toString());
                                            break;
                                        }
                                    }
                                }
                                //update adapter
                                groupAdapter.notifyDataSetChanged();

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //canceled
                System.err.println("Listener was cancelled");
            }
        });
    }

    /**
     * Create event in database
     */
    private void createEventInDatabase() {
        if (!TextUtils.isEmpty(location_et.getText().toString())) {
            //if location field is not empty

            //getting lat and lng from location
            final LatLng latLng = getLocationFromAddress(getActivity(), location_et.getText().toString());

            //if location is found
            if (latLng != null) {
                //getting user logged in
                final FirebaseUser fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

                //check for empty fields
                if (!TextUtils.isEmpty(name_et.getText().toString()) && !TextUtils.isEmpty(start_date_et.getText().toString()) && !TextUtils.isEmpty(end_date_et.getText().toString())) {
                    //new Database created from field contents written in by user
                    if (isDateAfter(start_date_et.getText().toString(), end_date_et.getText().toString())) {

                        DatabaseReference databaseEventReference;
                        //set reference to EVENT table in database
                        databaseEventReference = FirebaseDatabase.getInstance().getReference().child("EVENT");

                        databaseEventReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean user_name_is_occupied = false;

                                for (DataSnapshot user : snapshot.getChildren()) {
                                    if (user.child("event_name").getValue().toString().equals(name_et.getText().toString())) {
                                        user_name_is_occupied = true;
                                    }
                                }

                                if (!user_name_is_occupied) {
                                    //create new databaseEvent
                                    final DatabaseEvent databaseEvent = new DatabaseEvent();
                                    //set data in databaseEvent from EditText
                                    databaseEvent.setLocation_x(latLng.latitude);
                                    databaseEvent.setLocation_y(latLng.longitude);
                                    databaseEvent.setEvent_name(name_et.getText().toString());
                                    databaseEvent.setLocation_name(location_et.getText().toString());
                                    databaseEvent.setStart_date(start_date_et.getText().toString());
                                    databaseEvent.setEnd_date(end_date_et.getText().toString());
                                    databaseEvent.setPriv_pub(dropdown.getSelectedItem().toString());
                                    databaseEvent.setOwner_id(fireBaseUser.getUid());
                                    //set basic thumbnail
                                    databaseEvent.setThumbnail("https://firebasestorage.googleapis.com/v0/b/pabs-fa777.appspot.com/o/Images%2Fno-image-found-360x250.png?alt=media&token=77870c1c-7a00-4f6b-ba33-1f8c9abc6b73");

                                    //if databaseEvent is set to Private show groups
                                    if (databaseEvent.getPriv_pub().equals("Private")) {
                                        //set listener for single value event
                                        databaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot group : snapshot.getChildren()) {
                                                    //if group name get value is not null
                                                    if (group.child("group_name").getValue() != null) {
                                                        //get selected item
                                                        if ((group.child("group_name").getValue().toString()).equals(group_dropdown.getSelectedItem().toString())) {
                                                            //init joined_members
                                                            final ArrayList<String> joined_members = new ArrayList<>();
                                                            //reference to joined members so we can create databaseEvent with group members
                                                            group.child("joined_members").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot member : snapshot.getChildren()) {
                                                                        //add members to joined_members
                                                                        joined_members.add(member.getValue().toString());
                                                                    }

                                                                    //set format of date
                                                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                                    Date date = null;
                                                                    try {
                                                                        //make date from getEnd_date in databaseEvent
                                                                        date = (Date) formatter.parse(databaseEvent.getEnd_date() + " " + "00" + ":" + "00" + ":" + "00");

                                                                        //add 1 day to it
                                                                        Calendar c = Calendar.getInstance();
                                                                        c.setTime(date);
                                                                        c.add(Calendar.DATE, 1);
                                                                        date = c.getTime();

                                                                        //getting id of event
                                                                        String eventID = reference.push().getKey();

                                                                        //set timestamp in databaseEvent
                                                                        databaseEvent.setTimestamp(date.getTime());

                                                                        //pushing databaseEvent to database
                                                                        reference.child(eventID).setValue(databaseEvent);

                                                                        //set to do to databaseEvent
                                                                        createMyToDo(databaseEvent, eventID);

                                                                        //open event
                                                                        openEvent(databaseEvent);

                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    //canceled
                                                                    System.err.println("Listener was cancelled");
                                                                }
                                                            });
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //canceled
                                                System.err.println("Listener was cancelled");
                                            }
                                        });
                                    } else {
                                        //if databaseEvent is set to Public avoid selection of groups

                                        //set format of date
                                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        Date date = null;
                                        try {
                                            //make date from getEnd_date in databaseEvent
                                            date = (Date) formatter.parse(databaseEvent.getEnd_date() + " " + "00" + ":" + "00" + ":" + "00");

                                            //add 1 day to it
                                            Calendar c = Calendar.getInstance();
                                            c.setTime(date);
                                            c.add(Calendar.DATE, 1);
                                            date = c.getTime();

                                            //getting id of event
                                            String eventID = reference.push().getKey();

                                            //set timestamp in databaseEvent
                                            databaseEvent.setTimestamp(date.getTime());

                                            //pushing databaseEvent to database
                                            reference.child(eventID).setValue(databaseEvent);

                                            //set to do to databaseEvent
                                            createMyToDo(databaseEvent, eventID);

                                            //open event
                                            openEvent(databaseEvent);

                                        } catch (ParseException e) {
                                            //error in formatter of date
                                            e.printStackTrace();
                                        }

                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Name of event is already occupied", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //canceled
                                System.err.println("Listener was cancelled");
                            }
                        });

                    } else {
                        //if starting date > ending date
                        Toast.makeText(getActivity(), "Wrong date!", Toast.LENGTH_SHORT).show();
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
     * Create my to do
     */
    private void createMyToDo(DatabaseEvent dbEv, String eventID) {
        ToDoList temp = new ToDoList();
        temp.setToDoListTitle(dbEv.getEvent_name());
        temp.setOwner(eventID);
        Log.d("Elipszis", "createMyToDo: " + eventID + ", " + temp.getToDoListTitle());
        referenceToDO.child(eventID).push().setValue(temp);
        referenceToDO.child(eventID).child("Type").setValue("G");
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