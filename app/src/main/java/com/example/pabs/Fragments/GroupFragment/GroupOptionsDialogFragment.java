package com.example.pabs.Fragments.GroupFragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pabs.R;

import static android.view.View.GONE;

/**
 * Connects UI with different images in GroupFragment
 */

public class GroupOptionsDialogFragment extends AppCompatDialogFragment {

    //listener
    private GroupOptionsDialogFragment.GroupOptionsDialogListener groupOptionsDialogListener;

    //dialog
    private Dialog dialog;

    //ImageView
    private ImageView closeGroupImg;
    private ImageView addKickMembersImg;
    private ImageView groupEventsImg;
    private ImageView leaveGroupImg;
    private ImageView showCodeImg;
    private ImageView exitImg;

    //LinearLayout
    private LinearLayout closeGroupLinL;
    private LinearLayout kickMembersLinL;
    private LinearLayout groupEventsLinL;
    private LinearLayout leaveGroupLinL;
    private LinearLayout showCodeLinL;

    //helper variables
    private int mState;

    /**
     * Set listener
     */
    public void setListener(GroupOptionsDialogFragment.GroupOptionsDialogListener groupOptionsDialogListener, int state) {
        this.groupOptionsDialogListener = groupOptionsDialogListener;
        this.mState = state;
    }

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_group_options, null);

        //create new dialog with custom theme
        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        //init images
        closeGroupImg = view.findViewById(R.id.f_g_o_closeGroupImg);
        addKickMembersImg = view.findViewById(R.id.f_g_o_kickMembersImg);
        groupEventsImg = view.findViewById(R.id.f_g_o_groupEventsImg);
        leaveGroupImg = view.findViewById(R.id.f_g_o_leaveGroupImg);
        showCodeImg = view.findViewById(R.id.f_g_o_showCodeImg);
        exitImg = view.findViewById(R.id.f_g_o_exitImg);

        //init linearLayouts
        showCodeLinL = view.findViewById(R.id.f_g_o_lr5);
        closeGroupLinL = view.findViewById(R.id.f_g_o_lr4);
        kickMembersLinL = view.findViewById(R.id.f_g_o_lr3);
        groupEventsLinL = view.findViewById(R.id.f_g_o_lr2);
        leaveGroupLinL = view.findViewById(R.id.f_g_o_lr1);

        //set Click listeners to images
        closeGroupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupOptionsDialogListener.CloseGroup();
                dialog.dismiss();
            }
        });

        addKickMembersImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupOptionsDialogListener.KickMembers();
                dialog.dismiss();
            }
        });

        groupEventsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupOptionsDialogListener.GroupEvents();
                dialog.dismiss();
            }
        });

        leaveGroupLinL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupOptionsDialogListener.LeaveGroup();
                dialog.dismiss();
            }
        });

        showCodeLinL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupOptionsDialogListener.ShowCode();
                dialog.dismiss();
            }
        });

        exitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //set content view
        dialog.setContentView(view);
        //set Background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set visibility
        if (mState == 0) {
            //owner
            hideLeaveGroup();
        } else if (mState == 1) {
            //member
            hideCloseGroupImage();
            hideKickMembers();

        } else {
            Log.d("GODF", "Wrong mState!");
        }

        return dialog;
    }

    void hideCloseGroupImage() {
        closeGroupLinL.setVisibility(GONE);
    }

    void hideKickMembers() {
        kickMembersLinL.setVisibility(GONE);
    }

    void hideGroupEvents() {
        groupEventsLinL.setVisibility(GONE);
    }

    void hideLeaveGroup() {
        leaveGroupLinL.setVisibility(GONE);
    }

    void hideShowCode() {
        showCodeLinL.setVisibility(GONE);
    }

    /**
     * interface to launch option elements
     */
    public interface GroupOptionsDialogListener {
        void CloseGroup();

        void KickMembers();

        void GroupEvents();

        void LeaveGroup();

        void ShowCode();
    }
}
