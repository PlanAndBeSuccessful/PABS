package com.example.pabs.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.pabs.R;

/**
 * Sets Nickname for new Users
 */

public class NicknameDialogFragment extends AppCompatDialogFragment {

    //edittext
    private EditText nickname_et = null;
    private NicknameDialogListener nicknameDialogListener;

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_nickname, null);

        //building the custom dialog fragment
        builder.setView(view)
                .setTitle("Nickname")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nickname = nickname_et.getText().toString();
                        //calling interface to set the nickname
                        nicknameDialogListener.applyNickname(nickname);
                    }
                });

        nickname_et = view.findViewById(R.id.d_n_nickname);

        return builder.create();
    }



    /**
     * when the NicknameDialogListener is implemented in another class
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            nicknameDialogListener = (NicknameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                "Must implement NicknameDialogListener");
        }
    }

    /**
     * interface to set the nickname
     */
    public interface NicknameDialogListener{
        void applyNickname(String nickname);
    }
}