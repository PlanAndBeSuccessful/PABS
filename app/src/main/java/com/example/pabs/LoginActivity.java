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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements NicknameDialogFragment.NicknameDialogListener {

    //UI
    private EditText name_et = null, password_et = null;
    private Button forgot_password_btn = null, login_btn = null, register_btn = null;

    //firebase
    DatabaseReference reference;
    String currentUser;
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
                //check internet connection
                if(isInternetConnectionActivated()){
                    //internet is active
                    proceedLogin();
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
     * proceedLogin
     */
    private void proceedLogin(){
        final String name = name_et.getText().toString();
        final String password = password_et.getText().toString();

        //reference to all the ID's of USER
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean login_successful = false;
                boolean user_has_nickname = true;
                //iterate through ID elements of USER
                for (DataSnapshot child : snapshot.getChildren()) {
                    //examine if user credentials are correct
                    if((child.child("user_name").getValue().toString().equals(name)) && (child.child("password").getValue().toString().equals(password))){
                        login_successful = true;
                        currentUser = child.getKey().toString();

                        if(child.child("nickname").getValue().equals("")){
                            user_has_nickname = false;
                        }
                    }
                }

                if(login_successful){
                    //if login was successful
                    if(user_has_nickname){
                        loginSuccessful();
                    }
                    else {
                        openNicknameDialogFragment();
                    }
                }
                else
                {
                    //if login failed
                    Toast.makeText(LoginActivity.this, "Wrong name or password!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * apply nickname on dialog pressed OK
     */
    @Override
    public void applyNickname(String nickname) {
        reference.child(currentUser).child("nickname").setValue(nickname);
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
        startActivity(intent);

    }

    /**
     * open next activity
     */
    protected void openNextActivity(){

    }
}