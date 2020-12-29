package com.example.pabs.Models;

/**
 * Class to store task list from Firebase Database
 */

public class TaskList {
    private String isTakenBy;
    private String taskTitle;
    private boolean taskCB;
    private String belongTo;
    private String ToDoID;

    public TaskList() {
    }

    public TaskList(String taskTitle, String belongTO, String reference) {
        this.taskTitle = taskTitle;
        this.taskCB = false;
        this.belongTo = belongTO;
        this.ToDoID = reference;
        this.isTakenBy = null;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public boolean getTaskCB() {
        return taskCB;
    }

    public void setTaskCB(boolean taskcb) {
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

    public String getIsTakenBy() {
        return isTakenBy;
    }

    public void setIsTakenBy(String isTakenBy) {
        this.isTakenBy = isTakenBy;
    }
}
