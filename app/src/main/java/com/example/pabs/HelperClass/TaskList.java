package com.example.pabs.HelperClass;

import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;

public class TaskList {
    private String taskTitle;
    private boolean taskCB;
    private String belongTo;
    private String ToDoID;

    public TaskList(){}

    public TaskList(String taskTitle,String belongTO,String reference) {
        this.taskTitle = taskTitle;
        this.taskCB = false;
        this.belongTo = belongTO;
        this.ToDoID = reference;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public boolean getTaskCB(){
        return taskCB;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setTaskCB(boolean taskcb){
        this.taskCB = taskcb;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getReferenceTo() {
        return ToDoID;
    }

    public void setReferenceTo(String referenceto) {
        this.ToDoID = referenceto;
    }
}
