package com.example.pabs.Fragments.EventFragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pabs.R;

import static android.view.View.GONE;

/**
 * Sets Nickname for new Users
 */

public class EventOptionsDialogFragment extends AppCompatDialogFragment {

    //edittext
    private  EventOptionsDialogListener eventOptionsDialogListener;
    //dialog
    private Dialog dialog;
    //ImageView
    private ImageView upChImg;
    private ImageView repetitionImg;
    private ImageView addKickStaffImg;
    private ImageView reminderImg;
    private ImageView descriptionImg;
    private ImageView closeEventImg;
    private ImageView toDoImg;
    private ImageView joinLeaveEventImg;
    private ImageView exitImg;

    private LinearLayout upChLinL;
    private LinearLayout repetitionLinL;
    private LinearLayout addKickStaffLinL;
    private LinearLayout reminderLinL;
    private LinearLayout descriptionLinL;
    private LinearLayout closeEventLinL;
    private LinearLayout toDoLinL;
    private LinearLayout joinLeaveEventLinL;
    private TextView joinLeaveEventTv;

    private int mState;

    public void setListener(EventOptionsDialogListener eventOptionsDialogListener, int state) {
        this.eventOptionsDialogListener = eventOptionsDialogListener;
        this.mState = state;
    }

    /**
     * When the dialog is created
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //setting view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_event_options, null);

        dialog = new Dialog(getActivity(), R.style.MyDialogTheme);

        upChImg = view.findViewById(R.id.f_e_o_UpChImg);
        repetitionImg = view.findViewById(R.id.f_e_o_RepetitionImg);
        addKickStaffImg = view.findViewById(R.id.f_e_o_AddKickStaffImg);
        reminderImg = view.findViewById(R.id.f_e_o_ReminderImg);
        descriptionImg = view.findViewById(R.id.f_e_o_DescriptionImg);
        closeEventImg = view.findViewById(R.id.f_e_o_CloseEventImg);
        toDoImg = view.findViewById(R.id.f_e_o_ToDoImg);
        joinLeaveEventImg = view.findViewById(R.id.f_e_o_JoinLeaveEventImg);
        exitImg = view.findViewById(R.id.f_e_o_exitImg);

        upChLinL = view.findViewById(R.id.f_e_o_lr8);
        repetitionLinL = view.findViewById(R.id.f_e_o_lr7);
        addKickStaffLinL = view.findViewById(R.id.f_e_o_lr6);
        reminderLinL = view.findViewById(R.id.f_e_o_lr5);
        descriptionLinL = view.findViewById(R.id.f_e_o_lr4);
        closeEventLinL = view.findViewById(R.id.f_e_o_lr3);
        toDoLinL = view.findViewById(R.id.f_e_o_lr2);
        joinLeaveEventLinL = view.findViewById(R.id.f_e_o_lr1);

        joinLeaveEventTv = view.findViewById(R.id.f_e_o_lr1_tv);

        upChImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.UpCh();
                dialog.dismiss();
            }
        });

        repetitionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.Repetition();
                dialog.dismiss();
            }
        });

        addKickStaffImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.AddKickStaff();
                dialog.dismiss();
            }
        });

        reminderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.Reminder();
                dialog.dismiss();
            }
        });

        descriptionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.Description();
                dialog.dismiss();
            }
        });

        closeEventImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.CloseEvent();
                dialog.dismiss();
            }
        });

        toDoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.ToDo();
                dialog.dismiss();
            }
        });

        joinLeaveEventImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOptionsDialogListener.JoinLeaveEvent();
                dialog.dismiss();
            }
        });

        exitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if(mState == 0){
            //owner
            hideJoinLeaveEvent();
        }
        else if(mState == 1){
            //member
            hideAddKickStaff();
            hideCloseEvent();
            hideCreateViewTodo();
            hideDescription();
            hideUploadChangeImage();
            joinLeaveEventTv.setText("Leave Event");

        }else if(mState == 2){
            //staff
            hideAddKickStaff();
            hideCloseEvent();
            hideDescription();
            hideUploadChangeImage();
            joinLeaveEventTv.setText("Leave Event");

        }else if(mState == 3){
            //no joined
            hideAddKickStaff();
            hideCloseEvent();
            hideCreateViewTodo();
            hideDescription();
            hideUploadChangeImage();
            hideReminder();
            hideRepetition();
            joinLeaveEventTv.setText("Join Event");
        }
        else{
            Log.d("EODF", "Wrong mState!");
        }

        return dialog;
    }

    void hideUploadChangeImage(){
        upChLinL.setVisibility(GONE);
    }
    void hideRepetition(){
        repetitionLinL.setVisibility(GONE);
    }
    void hideAddKickStaff(){
        addKickStaffLinL.setVisibility(GONE);
    }
    void hideReminder(){
        reminderLinL.setVisibility(GONE);
    }
    void hideDescription(){
        descriptionLinL.setVisibility(GONE);
    }
    void hideCloseEvent(){
        closeEventLinL.setVisibility(GONE);
    }
    void hideCreateViewTodo(){
        toDoLinL.setVisibility(GONE);
    }
    void hideJoinLeaveEvent(){
        joinLeaveEventLinL.setVisibility(GONE);
    }

    /**
     * interface to launch option elements
     */
    public interface EventOptionsDialogListener{
        void UpCh();
        void Repetition();
        void AddKickStaff();
        void Reminder();
        void Description();
        void CloseEvent();
        void ToDo();
        void JoinLeaveEvent();
    }
}