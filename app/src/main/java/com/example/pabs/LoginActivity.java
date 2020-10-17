package com.example.pabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pabs.Fragments.NicknameDialogFragment;
import com.example.pabs.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements NicknameDialogFragment.NicknameDialogListener {

    //DEBUG
    private static final String TAG = "LoginActivity";

    //UI
    private EditText email_et = null, password_et = null;
    private Button forgot_password_btn = null, login_btn = null, register_btn = null;

    //firebase
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    /**
     * on create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //edittext
        email_et = findViewById(R.id.l_email);
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

        //login click listener
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check internet connection
                if(isInternetConnectionActivated()){
                    //internet is active
                    Login();
                }
                else{
                    //internet is not active
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("No internet connection!")
                            .setMessage("Please enable your internet to login!")
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setPositiveButton("Okay", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("USER");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Check internet connection
     */
    private boolean isInternetConnectionActivated() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else{
            //we are not connected to a network
            return false;
        }
    }

    /**
     * Login
     */
    private void Login(){
        mAuth.signOut();
        user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "User is already signed in!");
        } else {
            // User is not signed in
            final String email = email_et.getText().toString();
            final String password = password_et.getText().toString();

            //Sign in using auth(email, pass)
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                user = mAuth.getCurrentUser();

                                //Verify if user has nickname
                                reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.child("nickname").getValue().equals("")){
                                            //set nickname
                                            openNicknameDialogFragment();
                                        }
                                        else{
                                            //proceed to next activity
                                            loginSuccessful();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //database error
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    }

    /**
     * apply nickname on dialog pressed OK
     */
    @Override
    public void applyNickname(String nickname) {
        reference.child(user.getUid()).child("nickname").setValue(nickname);
        loginSuccessful();
    }

    /**
     * login successful
     */
    private void loginSuccessful(){
        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        openNextActivity();
    }

    /**
     * open nickname dialog fragment
     */
    private void openNicknameDialogFragment(){
        NicknameDialogFragment nicknameDialogFragment = new NicknameDialogFragment();
        nicknameDialogFragment.show(getSupportFragmentManager(),"nicknameDialogFragment");
    }

    /**
     * open forgot password fragment
     */
    private void openForgotPasswordFragment(){

    }

    /**
     * open register activity
     */
    private void openRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        mAuth.signOut();
        startActivity(intent);
    }

    /**
     * open next activity
     */
    protected void openNextActivity(){

    }
}