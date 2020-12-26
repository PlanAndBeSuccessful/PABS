package com.example.pabs.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pabs.Fragments.EventFragment.EventOptionsDialogFragment;
import com.example.pabs.R;

/**
 * Sets Nickname for new Users
 */

public class AddTaskDialogFragment extends AppCompatDialogFragment {

    //edittext
    private EditText task_et = null;
    private AddTaskDialogListener addTaskDialogListener;
    //
    private Dialog dialog;
    //
    private Button confirm_bt;
    private Button cancel_bt;

    public void setListener(AddTaskDialogFragment.AddTaskDialogListener addTaskDialogListener) {
        this.addTaskDialogListener = addTaskDialogListener;
    }

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //setting view
        Log.d("Espania", "onCreateDialog: AddaTaskDialogFragment");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_task_dialog, null);

        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        task_et = view.findViewById(R.id.d_at_text);

        confirm_bt = (Button) view.findViewById(R.id.d_at_confirm_btn);

        confirm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(task_et.getText().toString())) {
                    String task = task_et.getText().toString();
                    //calling interface to set the nickname
                    addTaskDialogListener.applyText(task);
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Please fill the textdialog with a Task name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_bt = (Button) view.findViewById(R.id.d_at_cancel_btn);

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.setContentView(view);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    /**
     * interface to set the nickname
     */
    public interface AddTaskDialogListener{
        void applyText(String taskname);
    }
}