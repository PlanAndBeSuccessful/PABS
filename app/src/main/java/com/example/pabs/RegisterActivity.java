package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pabs.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.pabs.R.layout.activity_register;

public class RegisterActivity extends AppCompatActivity {

    private String TAG = "RegisterActivity";

    private String token = "";

    //UI
    private EditText name_et = null, password_et = null, email_et = null;
    private Button register_btn = null, back_to_login = null;
    private FirebaseAuth currAuth;

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

            back_to_login = findViewById(R.id.r_existing_account);

            //open login
            back_to_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

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
        final String password = password_et.getText().toString();

        if (!TextUtils.isEmpty(email_et.getText().toString()) && !TextUtils.isEmpty(password_et.getText().toString()) && !TextUtils.isEmpty(name_et.getText().toString())) {

            //creating a user type object
            final User user = new User("", email, "", password, name, "false");

            //getting an instance of firebase authentication token
            currAuth = FirebaseAuth.getInstance();
            currAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseMessaging.getInstance().deleteToken();

                                reference.child(currAuth.getUid()).setValue(user);
                                FirebaseAuth.getInstance().signOut();
                                finish();
                            } else {
                                if (password.length() < 6) {
                                    Toast.makeText(RegisterActivity.this, "The given password is too short \nIt needs to be at least 6 characters!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "The given E-mail may contain errors!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
        else
            {
            Toast.makeText(RegisterActivity.this, "Register failed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void openNextActivity(){

    }

}