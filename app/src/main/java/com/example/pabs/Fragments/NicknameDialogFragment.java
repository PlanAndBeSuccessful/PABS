package com.example.pabs.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pabs.R;

/**
 * Sets Nickname for new Users
 */

public class NicknameDialogFragment extends AppCompatDialogFragment {

    //edittext
    private EditText nickname_et = null;
    private NicknameDialogListener nicknameDialogListener;
    //
    private Dialog dialog;
    //
    private Button continue_bt;

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_nickname, null);

        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        nickname_et = view.findViewById(R.id.d_n_nickname);

        continue_bt = (Button) view.findViewById(R.id.d_n_continue);

        continue_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(nickname_et.getText().toString())) {
                    String nickname = nickname_et.getText().toString();
                    //calling interface to set the nickname
                    nicknameDialogListener.applyNickname(nickname);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please type in you nickname!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setContentView(view);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
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
    public interface NicknameDialogListener {
        void applyNickname(String nickname);
    }
}