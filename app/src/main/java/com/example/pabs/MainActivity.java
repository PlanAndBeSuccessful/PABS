package com.example.pabs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //UI
    Button logout_btn;

    //firebase
    private DatabaseReference reference;
    private String uID;

    /**
     * on create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        logout_btn = findViewById(R.id.logout);

        //get uid of logged in user
        uID = getIntent().getStringExtra("USER");

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("USER");

        //logout button on click listener
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(uID).child("online").setValue("false");
                finish();
            }
        });
    }

    /**
     * on stop to remember if user is online or offline after app is closed
     */
    @Override
    protected void onStop() {
        super.onStop();
        reference.child(uID).child("online").setValue("false");
    }

    /**
     * on post resume to remember if user is online or offline after app is minimized
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        reference.child(uID).child("online").setValue("true");
    }
}