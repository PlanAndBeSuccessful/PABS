package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import static com.example.pabs.R.layout.activity_register;

public class RegisterActivity extends AppCompatActivity {

    //UI
    private EditText name_et = null, password_et = null, email_et = null;
    private Button register_btn = null;
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

        //creating a user type object
        final User user = new User("04",email,"",password,name);

        //getting an instance of firebase authentication token
        currAuth = FirebaseAuth.getInstance();
        currAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            reference.child(currAuth.getUid()).setValue(user);
                            finish();
                        }
                        else{
                            if( password.length() < 6){
                                Toast.makeText(RegisterActivity.this, "The given password is too short \nIt needs to be at least 6 characters!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "The given E-mail may contain errors!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        currAuth.signOut();
    }

    private void openNextActivity(){

    }
}