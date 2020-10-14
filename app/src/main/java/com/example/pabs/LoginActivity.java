package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pabs.Models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //UI
    private EditText name_et = null, password_et = null;
    private Button forgot_password_btn = null, login_btn = null, register_btn = null;

    //firebase
    DatabaseReference reference;

    /**
     * on create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //edittext
        name_et = findViewById(R.id.l_username);
        password_et = findViewById(R.id.l_password);

        //button
        forgot_password_btn = findViewById(R.id.l_forgot_password);
        login_btn = findViewById(R.id.l_login_button);
        register_btn = findViewById(R.id.l_register_button);

        //open forgotPasswordFragment
        forgot_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPasswordFragment();
            }
        });

        //open RegisterActivity
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

        //open NextActivity
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextActivity();
            }
        });

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("USER");

        //login click listener
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = name_et.getText().toString();
                final String password = password_et.getText().toString();

                //reference to all the ID's of USER
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean login_successful = false;
                        //iterate through ID elements of USER
                        for (DataSnapshot child : snapshot.getChildren()) {
                            //examine if user credentials are correct
                            if((child.child("user_name").getValue().toString().equals(name)) && (child.child("password").getValue().toString().equals(password))){
                                login_successful = true;
                            }
                        }

                        if(login_successful){
                            //if login was successful
                            LoginSuccessful();
                        }
                        else
                        {
                            //if login failed
                            Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    /**
     * login successful
     */
    private void LoginSuccessful(){
        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        openNextActivity();
    }

    /**
     * open forgot password fragment
     */
    protected void openForgotPasswordFragment(){

    }

    /**
     * open register activity
     */
    protected void openRegisterActivity(){

    }

    /**
     * open next activity
     */
    protected void openNextActivity(){

    }

}