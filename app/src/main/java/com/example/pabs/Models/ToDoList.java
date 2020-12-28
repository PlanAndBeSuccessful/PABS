package com.example.pabs.Models;

import java.util.List;

/**
 * Class to store to do list from Firebase Database
 */

public class ToDoList {
    private String ID;
    private String owner;
    private String toDoListTitle;
    private List<TaskList> taskList;

    public ToDoList() {
    }

    public ToDoList(String toDoListTitle, List<TaskList> taskList, String owner, String id) {
        this.toDoListTitle = toDoListTitle;
        this.taskList = taskList;
        this.owner = owner;
        this.ID = id;
    }

    public String getToDoListTitle() {
        return toDoListTitle;
    }

    public void setToDoListTitle(String toDoListTitle) {
        this.toDoListTitle = toDoListTitle;
    }

    public List<TaskList> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskList> taskList) {
        this.taskList = taskList;
    }

    public void addToTaskList(TaskList task) {
        this.taskList.add(task);
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
