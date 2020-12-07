package com.example.pabs.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.Models.Event;
import com.example.pabs.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Handle an event
 */

public class EventFragment extends Fragment implements OnMapReadyCallback {

    public static final int GET_FROM_GALLERY = 3;
    private static final String TAG = "EventFragment";
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

    //image handling
    private ImageView image_view;
    private StorageReference mStorageRef;
    private Button ch, up;
    public Uri imgUri;
    private StorageTask uploadTask;

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

        image_view = view.findViewById(R.id.fe_event_image);

        ch =  view.findViewById(R.id.fe_change_button);
        up =  view.findViewById(R.id.fe_upload_button);

        if(databaseEvent.getThumbnail() != null){
            //set image if it's not null
            Uri myUri = Uri.parse(databaseEvent.getThumbnail());

            //Picasso license
            Picasso.get().load(myUri).into(image_view);
        }

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
                //clear all backstack
                clearBackstack();
            }
        });

        //delete button to delete event
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
                            //delete selected event
                            appleSnapshot.getRef().removeValue();

                            //clear it from backstack
                            clearBackstack();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //database failed
                        Log.e("EventFragment", "onCancelled", databaseError.toException());
                    }
                });
            }
        });

        //change image of event
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGallery();
            }
        });

        //upload image to database
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgUri != null){
                    //if we have an image selected
                    if (uploadTask != null && uploadTask.isInProgress()){
                        //if upload is not finished
                        Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                    }else {
                        //if upload is not started
                        fileUploader();
                    }
                }
                else{
                    //if image is not selected
                    Toast.makeText(getActivity(), "Image not selected!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //get reference to firebase storage "Images/" path
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.fe_map);
        mapView.onCreate(savedInstanceState);

        //Sync Map View
        mapView.getMapAsync(this);

        return view;
    }

    /**
     * fileUploader
     */
    private String getExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader(){
        //get reference to name
        final StorageReference ref = mStorageRef.child(System.currentTimeMillis() +"."+getExtension(imgUri));

        //set upload task to imgUri
        final UploadTask uploadTask = ref.putFile(imgUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //start upload
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //if image is uploaded
                            Toast.makeText(getActivity(), "Image uploaded succesfully", Toast.LENGTH_SHORT).show();

                            final Uri downloadUri = task.getResult();
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("EVENT");

                            //connect firebase storage with firebase realtime database
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot event : snapshot.getChildren()) {
                                        //Loop 1 to go through all child nodes of users
                                        if(event.child("event_name").getValue() == databaseEvent.getEvent_name()){
                                            //set thumbnail
                                            setThumbnail(event.getKey(), ref, downloadUri);
                                            databaseEvent.setThumbnail(downloadUri.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //database failed
                                }
                            });


                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //upload task failed
            }
        });
    }

    /**
     * set Thumbnail
     */
    private void setThumbnail(String key, DatabaseReference ref, Uri downloadUri){
        ref.child(key).child("thumbnail").setValue(downloadUri.toString());
    }

    /**
     * Open Gallery
     */
    private void viewGallery(){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                imgUri = data.getData();
                image_view.setImageURI(imgUri);

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                Log.d(TAG, "Image Selected Successfully!");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * clearBackstack
     */
    public void clearBackstack(){
        //clear all backstact
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