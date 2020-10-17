package com.example.pabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pabs.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.pabs.R.layout.activity_register;

public class RegisterActivity extends AppCompatActivity {

    //UI
    private EditText name_et = null, password_et = null, email_et = null;
    private Button register_btn = null;

    //firebase
    DatabaseReference reference;
    FirebaseDatabase FbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_register);

            //edittext
            name_et = findViewById(R.id.r_username);
            password_et = findViewById(R.id.r_password);
            email_et = findViewById(R.id.r_email);

            //button
            register_btn = findViewById(R.id.r_signup_button);

            //open NextActivity
            register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openNextActivity();
                }
            });

            //firebase database -> get reference to USER table
            reference = FirebaseDatabase.getInstance().getReference().child("USER");

            register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register();
                }
            });


    }

    private void register(){
        //getting user credentials
        String name = name_et.getText().toString();
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        //creating a user type object
        User user = new User("04",email,"",password,name);
        //creating a new element in the database
        //reference.child(user.getUserID()).child(user.).setValue(user.getE_mail());
        /*reference.child(user.getUserID()).setValue(user);
        reference.child(user.getUserID()).child("userID").removeValue();*/
        String key_id = reference.push().getKey();
        reference.child(key_id).setValue(user);

    }

    private void openNextActivity(){

    }
}