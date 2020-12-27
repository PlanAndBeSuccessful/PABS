package com.example.pabs.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pabs.Adapters.ToDoRecyclerViewAdapter;
import com.example.pabs.Models.TaskList;
import com.example.pabs.Models.ToDoList;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyToDoFragment extends Fragment implements AddTaskDialogFragment.AddTaskDialogListener {

    private View listView;
    List<ToDoList> itemList;
    List<ToDoList> lstToDo;
    //firebase
    private DatabaseReference reference = null;
    private String uID;

    //dialog fragment
    private String task_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //get uid of logged in user
        uID = getActivity().getIntent().getStringExtra("USER");

        // Inflate the layout for this fragment
        View myToDoview = inflater.inflate(R.layout.fragment_my_to_do, container, false);

        //firebase database -> get reference to USER table
        reference = FirebaseDatabase.getInstance().getReference().child("TODO").child(uID);

        listView = getActivity().findViewById(R.id.activity_event_layout);
        itemList = new ArrayList<>();
        List<TaskList> tasks = new ArrayList<>();
        ToDoList mytodos = new ToDoList("My ToDos", tasks, uID, "");
        itemList.add(mytodos);
        final RecyclerView ParentRecyclerViewItem = myToDoview.findViewById(R.id.todo_rec_view);

        // Initialise the Linear layout manager
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set data for events example
        lstToDo = new ArrayList<>();

        //Getting events from database and setting them to recyclerview
        final DatabaseReference databaseTodoRef;
        databaseTodoRef= FirebaseDatabase.getInstance().getReference().child("TODO").child(uID);

        //databaseEvents.addValueEventListener(new ValueEventListener() {
        databaseTodoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstToDo.clear();
                for (final DataSnapshot todo : snapshot.getChildren()) {
                    final ToDoList tempTD = new ToDoList();

                    //Loop 1 to go through all child nodes of events
                    final List<TaskList> tasks =  new ArrayList<>();
                    todo.getRef().child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot task : snapshot.getChildren()) {
                                //Loop 1 to go through all child nodes of joined members
                                if(!task.getValue().equals("Type")){
                                    TaskList task_temp = new TaskList();
                                    task_temp.setBelongTo(task.child("belongTo").getValue().toString());
                                    task_temp.setTaskTitle(task.child("taskTitle").getValue().toString());
                                    String CB = task.child("taskCB").getValue().toString();
                                    boolean cb = false;
                                    if(CB.equals("true")){
                                        cb = true;
                                        Log.d("Espania", "onDataChange: Ifben vagyok! " + cb);
                                    }
                                    task_temp.setTaskCB(cb);
                                    tasks.add(task_temp);
                                }
                            }


                            final Handler handler = new Handler();
                            final int delay = 1000; //milliseconds

                            Log.d("WTF", "run: Here is Jimmi!" + tasks.size());
                            handler.postDelayed(new Runnable(){
                                public void run(){
                                    if(!tasks.isEmpty())//checking if the data is loaded or not
                                    {
                                        Log.d("WTF", "run: Here is Johnny!");
                                        String td_title = todo.child("toDoListTitle").getValue().toString();
                                        tempTD.setToDoListTitle(td_title);
                                        String td_owner = todo.child("owner").getValue().toString();
                                        tempTD.setOwner(td_owner);

                                        tempTD.setTaskList(tasks);


                                        //pushing the temporary event object into an arraylist
                                        lstToDo.add(tempTD);
                                        // Pass the arguments
                                        // to the parentItemAdapter.
                                        // These arguments are passed
                                        // using a method ParentItemList()
                                        ToDoRecyclerViewAdapter parentItemAdapter = new ToDoRecyclerViewAdapter(lstToDo,uID);

                                        // Set the layout manager
                                        // and adapter for items
                                        // of the parent recyclerview
                                        ParentRecyclerViewItem.setAdapter(parentItemAdapter);
                                        ParentRecyclerViewItem.setLayoutManager(layoutManager);
                                    }
                                    else
                                        handler.postDelayed(this, delay);
                                }
                            }, delay);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Button todo_btn = myToDoview.findViewById(R.id.todo_button);
        todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialogFragment();
                Log.d("Espania", "onClick: Heyho");
            }
        });
        return myToDoview;
    }

    private void pushInMyToDoList(final DatabaseReference reference){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            int ok = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot todo: snapshot.getChildren()) {
                    if (todo.child("toDoListTitle").getValue() != null) {
                        if (todo.child("toDoListTitle").getValue().toString().equals("My ToDos")) {
                            ok = 1;
                            if (todo.child("ID").getValue() == null) {
                                todo.child("ID").getRef().setValue(todo.getKey());
                            }
                            todo.child("taskList").getRef().push().setValue(new TaskList(task_text, uID, todo.getKey()));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Hiding the activity layout
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void applyText(String taskname) {
        task_text = taskname;
        Log.d("Espania", "applyText: " + reference);
        pushInMyToDoList(reference);
    }

    public void inviteDialogFragment(){
        AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
        addTaskDialogFragment.setListener(MyToDoFragment.this);
        addTaskDialogFragment.setCancelable(true);
        addTaskDialogFragment.show(getActivity().getSupportFragmentManager(),"AddTaskDialogFragment");
    }
}