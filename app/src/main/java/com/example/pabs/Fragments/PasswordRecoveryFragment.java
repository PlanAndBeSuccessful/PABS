package com.example.pabs.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pabs.LoginActivity;
import com.example.pabs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordRecoveryFragment extends Fragment {

    private Button PR_donebtn;
    private EditText PR_email;
    private FirebaseAuth auth;
    private View listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View PasswordRecoveryView = inflater.inflate(R.layout.fragment_password_recovery, container, false);
        listView = getActivity().findViewById(R.id.ActivityLoginLayout);

        //importing the objects from the layout
        PR_donebtn = PasswordRecoveryView.findViewById(R.id.PR_done_btn);
        PR_email = PasswordRecoveryView.findViewById(R.id.PR_email);

        //Setting a clicklistener for the button
        PR_donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reading the text from the EditText
                if(!TextUtils.isEmpty(PR_email.getText().toString())){
                String Emailaddress = PR_email.getText().toString();
                Log.d("Email:",Emailaddress);
                    //Sending the email with firebase auth
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(Emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "E-mail sent!", Toast.LENGTH_SHORT);
                                getActivity().getSupportFragmentManager().popBackStack("PasswordRecoveryFragment", 1);
                            }
                            else{
                                Toast.makeText(getActivity(), "An Eroor occured!", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(), "You forgot to give an e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return PasswordRecoveryView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        listView.setVisibility(View.VISIBLE);
    }

}