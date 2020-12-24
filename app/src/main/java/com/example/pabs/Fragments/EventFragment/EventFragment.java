    package com.example.pabs.Fragments.EventFragment;

    import android.app.Activity;
    import android.app.ProgressDialog;
    import android.content.ContentResolver;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.net.Uri;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;

    import android.os.Handler;
    import android.provider.MediaStore;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.webkit.MimeTypeMap;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.pabs.Models.DatabaseEvent;
    import com.example.pabs.R;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.MapView;
    import com.google.android.gms.maps.OnMapReadyCallback;
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
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Handle an event
     */

    public class EventFragment extends Fragment implements OnMapReadyCallback, EventOptionsDialogFragment.EventOptionsDialogListener {

        public static final int GET_FROM_GALLERY = 3;
        private static final String TAG = "EventFragment";
        //UI
        private Button back_button;
        private Button plus_button;
        private View containerView;
        private ProgressDialog mDialog = null;

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
        public Uri imgUri;
        private StorageTask<UploadTask.TaskSnapshot> uploadTask;

        private String mUID;
        private int mState;
        /**
         * Constructor
         */
        public EventFragment(DatabaseEvent dbE, String uID) {
            databaseEvent = dbE;
            mUID = uID;
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
            View view = inflater.inflate(R.layout.fragment_event, container, false);
            containerView = getActivity().findViewById(R.id.activity_event_layout);

            //init UI
            event_name_tv = view.findViewById(R.id.fe_event_name);
            event_date_start_tv = view.findViewById(R.id.fe_event_date_start);
            event_date_end_tv = view.findViewById(R.id.fe_event_date_end);
            event_description_tv = view.findViewById(R.id.fe_event_description);
            location_text_tv = view.findViewById(R.id.fe_location_text);
            image_view = view.findViewById(R.id.fe_event_image);

            if(databaseEvent.getThumbnail() != null){
                //set image if it's not null
                Uri myUri = Uri.parse(databaseEvent.getThumbnail());

                //Picasso license
                Picasso.get().load(myUri).into(image_view);
            }

            if(databaseEvent.getDescription() != null){
                event_description_tv.setText(databaseEvent.getDescription());
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
            plus_button = view.findViewById(R.id.fe_plus_button);

            plus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEventOptionsDialogFragment();
                }
            });

            //get reference to firebase storage "Images/" path
            mStorageRef = FirebaseStorage.getInstance().getReference("Images");

            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) view.findViewById(R.id.fe_map);
            mapView.onCreate(savedInstanceState);

            //Sync Map View
            mapView.getMapAsync(this);

            mState = getStatus();

            return view;
        }

        private int getStatus(){
            if(mUID.equals(databaseEvent.getOwner_id())){
                //owner
                return 0;
            }
            else if(databaseEvent.getJoined_members().contains(mUID)){
                //member
                return 1;
            }
            else if(databaseEvent.getStaff_members().contains(mUID)){
                //staff
                return 2;
            }
            else{
                //not joined
                return 3;
            }
        }

        /**
         * open event dialog fragment
         */
        private void openEventOptionsDialogFragment(){
            EventOptionsDialogFragment eventOptionsDialogFragment = new EventOptionsDialogFragment();
            eventOptionsDialogFragment.setListener(EventFragment.this, mState);
            eventOptionsDialogFragment.setCancelable(true);
            eventOptionsDialogFragment.show(getActivity().getSupportFragmentManager(),"eventDialogFragment");
        }

        /**
         * delete Image
         */
        private void deleteImage(DataSnapshot dataSnapshot){
            //clear image
            if(dataSnapshot.child("thumbnail").getValue() != null) {
                //init firebase storage
                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

                //reference to photo shown in this event
                StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(dataSnapshot.child("thumbnail").getValue().toString());

                //delete the photo from firebase storage
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });
            }
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
            uploadTask = ref.putFile(imgUri);

            //dialog on loading
            mDialog = new ProgressDialog(getActivity());

            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();

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

                                //get download Uri
                                final Uri downloadUri = task.getResult();
                                //reference to EVENT in firebase database
                                final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");

                                //connect firebase storage with firebase realtime database
                                refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot event : snapshot.getChildren()) {
                                            //Loop 1 to go through all child nodes of users
                                            if(event.child("event_name").getValue() == databaseEvent.getEvent_name()){

                                                if(event.child("thumbnail").getValue() == null){
                                                    //set thumbnail if there is no image
                                                    setThumbnail(event.getKey(), refEvent, downloadUri);
                                                    databaseEvent.setThumbnail(downloadUri.toString());
                                                    Log.d(TAG, "Nincs kep: ");
                                                }
                                                else{
                                                    //if there is already and image delete it, and replace it with selected one
                                                    deleteImage(event);

                                                    setThumbnail(event.getKey(), refEvent, downloadUri);
                                                    databaseEvent.setThumbnail(downloadUri.toString());
                                                }
                                                mDialog.dismiss();

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

        //called after viewGallery startActivityForResult returned in viewGallery
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
                    e.printStackTrace();
                } catch (IOException e) {
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

        @Override
        public void UpCh() {
            viewGallery();

            final Handler handler = new Handler();
            final int delay = 1000; //milliseconds

            handler.postDelayed(new Runnable(){
                public void run() {
                    if (imgUri != null) {
                        //if we have an image selected
                        if (uploadTask != null && uploadTask.isInProgress()) {
                            //if upload is not finished
                            Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            //if upload is not started
                            fileUploader();
                        }
                    }  else
                        handler.postDelayed(this, delay);
                 }
            }, delay);

        }

        @Override
        public void Repetition() {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.fragment_event_container , new EventRepetitionFragment(databaseEvent))
                    .addToBackStack("EventRepetitionFragment")
                    .commit();
        }

        @Override
        public void AddKickStaff() {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.fragment_event_container , new EventStaffFragment(databaseEvent))
                    .addToBackStack("EventStaffFragment")
                    .commit();
        }

        @Override
        public void Reminder() {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.fragment_event_container , new EventReminderFragment(databaseEvent))
                    .addToBackStack("EventReminderFragment")
                    .commit();
        }

        @Override
        public void Description() {
            getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace( R.id.fragment_event_container , new EventDescriptionFragment(databaseEvent))
                        .addToBackStack("EventDescriptionFragment")
                        .commit();
        }

        @Override
        public void CloseEvent() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("EVENT").orderByChild("event_name").equalTo(databaseEvent.getEvent_name());

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        //clear image
                        deleteImage(appleSnapshot);

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

        @Override
        public void JoinLeaveEvent() {
            final DatabaseReference refEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
            refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (final DataSnapshot event : snapshot.getChildren()) {
                        //Loop 1 to go through all child nodes of users
                        if (event.child("event_name").getValue() == databaseEvent.getEvent_name()) {
                            if(mState == 3){
                                databaseEvent.addToJoinedListEnd(mUID);
                                event.getRef().child("joined_members").setValue(databaseEvent.getJoined_members());
                                mState = 1;
                            }
                            else{
                                databaseEvent.deleteJoinedListElement(mUID);
                                event.getRef().child("joined_members").setValue(databaseEvent.getJoined_members());
                                mState = 3;
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }

        @Override
        public void ToDo() {

        }
    }