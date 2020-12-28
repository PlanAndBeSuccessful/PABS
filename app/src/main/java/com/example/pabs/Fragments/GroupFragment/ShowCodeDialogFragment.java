package com.example.pabs.Fragments.GroupFragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pabs.R;

/**
 * Show code of group
 */
public class ShowCodeDialogFragment extends AppCompatDialogFragment {
    //helper variables
    private final String codeText;
    //UI
    private TextView code_tv;
    private Dialog dialog;
    private Button cancel_bt;

    /**
     * Constructor
     */
    ShowCodeDialogFragment(String codeText) {
        this.codeText = codeText;
    }

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_show_code, null);

        //create new dialog with custom theme
        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        //init UI
        code_tv = view.findViewById(R.id.d_s_c_code);
        cancel_bt = (Button) view.findViewById(R.id.d_s_c_close);

        //set code to TextView
        code_tv.setText(codeText);

        //set click listener to text view to copy code to clipboard
        code_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyCodeToClipboard();
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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    /**
     * Copy code to clipboard
     */
    private void copyCodeToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("code text", codeText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "Code copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

}
