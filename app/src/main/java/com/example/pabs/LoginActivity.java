package com.example.pabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pabs.Fragments.NicknameDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity implements NicknameDialogFragment.NicknameDialogListener {

    //DEBUG
    private static final String TAG = "LoginActivity";

    //UI
    private EditText email_et = null, password_et = null;
    private Button forgot_password_btn = null, login_btn = null, register_btn = null;
    private ProgressDialog mDialog = null;

    //firebase
    private DatabaseReference reference = null;
    private FirebaseAuth mAuth = null;
    private FirebaseUser user = null;
    private String token = null;

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

        //login click listener
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog on loading
                mDialog = new ProgressDialog(LoginActivity.this);

                mDialog.setMessage("Please wait...");
                mDialog.show();

                login_btn.setClickable(false);
                //check internet connection
                if(isInternetConnectionActivated()){
                    //internet is active
                    login();
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
                    login_btn.setClickable(true);
                    mDialog.dismiss();
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
     * Set token to avoid losing it
     */
    private void setToken(String s){
        this.token = s;
    }

    /**
     * Login
     */
    private void login(){

        // check if fields are empty
        if (!TextUtils.isEmpty(email_et.getText().toString()) && !TextUtils.isEmpty(password_et.getText().toString()) ) {

            //get information from the edit text fields
            final String email = email_et.getText().toString();
            final String password = password_et.getText().toString();

            //get token for user
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            token = task.getResult();
                            //we have to use a setter else we lose the information inside onComplete
                            setToken(token);
                        }
                    });

            //Sign in using auth(email, pass)
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                user = mAuth.getCurrentUser();

                                //Verify if user has nickname
                                reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //check if user is online
                                        if(snapshot.child("online").getValue().equals("false")){
                                            //check if use has a nickname
                                            if (snapshot.child("nickname").getValue().equals("")) {
                                                //set nickname
                                                openNicknameDialogFragment();
                                                login_btn.setClickable(true);
                                                mDialog.dismiss();
                                            } else {
                                                //proceed to next activity
                                                loginSuccessful();
                                                mDialog.dismiss();
                                            }
                                        }
                                        else{
                                            Log.d(TAG, "User is already online!");
                                            Toast.makeText(LoginActivity.this, "User is already online!", Toast.LENGTH_SHORT).show();
                                            login_btn.setClickable(true);
                                            mDialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //database error
                                        login_btn.setClickable(true);
                                        mDialog.dismiss();
                                        Log.w(TAG, "On cancelled: " + error);
                                    }
                                });
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                login_btn.setClickable(true);
                                mDialog.dismiss();
                            }
                        }
                    });
        }
        else
        {
            // If sign in fails, display a message to the user.
            Toast.makeText(LoginActivity.this, "Wrong E-mail or Password!", Toast.LENGTH_SHORT).show();
            login_btn.setClickable(true);
            mDialog.dismiss();
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

        //settings for user
        reference.child(user.getUid()).child("online").setValue("true");
        reference.child(user.getUid()).child("token").setValue(token);
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
        startActivity(intent);
    }

    /**
     * open next activity
     */
    protected void openNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER", user.getUid());
        startActivity(intent);
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        login_btn.setClickable(true);
    }
}