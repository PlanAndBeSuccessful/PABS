package com.example.pabs.Fragments.GroupFragment;

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
 * Join to group with code
 */
public class CodeDialogFragment extends AppCompatDialogFragment {

    //UI
    private EditText code_et;
    private Dialog dialog;
    private Button join_bt;
    private Button cancel_bt;

    //listener
    private CodeDialogFragment.CodeDialogListener codeDialogListener;

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_code, null);

        //create new dialog with custom theme
        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        //init UI
        code_et = view.findViewById(R.id.d_c_code);
        join_bt = (Button) view.findViewById(R.id.d_c_join);
        cancel_bt = (Button) view.findViewById(R.id.d_c_cancel);

        //set click listener for  join button
        join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGroup();
            }
        });

        //set click listener for  cancel button
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //when clicked outside dialog it won't cancel
        setCancelable(false);

        //set content view
        dialog.setContentView(view);

        //set background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    /**
     * Join Group with code
     */
    private void joinGroup() {
        if (!TextUtils.isEmpty(code_et.getText().toString())) {
            String code = code_et.getText().toString();
            //calling interface to set the nickname
            codeDialogListener.applyCode(code);
            dialog.dismiss();
        } else {
            Toast.makeText(getActivity(), "Please type in the invite code!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * when the CodeDialogListener is implemented in another class
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            codeDialogListener = (CodeDialogFragment.CodeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement CodeDialogListener");
        }
    }

    /**
     * interface to set the nickname
     */
    public interface CodeDialogListener {
        void applyCode(String code);
    }
}
