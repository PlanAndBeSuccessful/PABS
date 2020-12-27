package com.example.pabs.Fragments.EventFragment;

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

import com.example.pabs.Adapters.EventTodoRecyclerViewAdapter;
import com.example.pabs.Fragments.AddTaskDialogFragment;
import com.example.pabs.Models.TaskList;
import com.example.pabs.Models.ToDoList;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventToDoFragment extends Fragment implements AddTaskDialogFragment.AddTaskDialogListener {

    private String uID;
    private View listView;
    //firebase
    private DatabaseReference reference = null;
    private String EventID;

    //dialog fragment
    private String task_text;

    //Event object
    DatabaseEvent event_obj;

    ToDoList tD;

    EventTodoRecyclerViewAdapter parentItemAdapter;
    public void setEventID(String id){
        EventID = id;
    }

    public EventToDoFragment(DatabaseEvent event) {
        this.event_obj = event;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uID = getActivity().getIntent().getStringExtra("USER");
        Log.d("EventId", "onCreateView: " + EventID);
        // Inflate the layout for this fragment
        View myToDoview = inflater.inflate(R.layout.fragment_event_todo, container, false);

        listView = getActivity().findViewById(R.id.activity_event_layout);
        List<TaskList> tasks = new ArrayList<>();

        final RecyclerView RecyclerViewItem = myToDoview.findViewById(R.id.ev_todo_rec_view);

        // Initialise the Linear layout manager
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set data for events example

        DatabaseReference referenceEvent = FirebaseDatabase.getInstance().getReference().child("EVENT");
        referenceEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot event : snapshot.getChildren()){
                    if(event.child("event_name").getValue() != null) {
                        if (event.child("event_name").getValue().toString().equals(event_obj.getEvent_name())){
                           //setEventID(event.getKey());
                            //firebase database -> get reference to TODO table
                            EventID = event.getKey();
                            reference = FirebaseDatabase.getInstance().getReference().child("TODO").child(EventID);
                            //databaseEvents.addValueEventListener(new ValueEventListener() {
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (final DataSnapshot todo : snapshot.getChildren()) {
                                        if (!todo.getKey().equals("Type")) {
                                            final ToDoList tempTD = new ToDoList();

                                            //Loop 1 to go through all child nodes of events
                                            final List<TaskList> tasks = new ArrayList<>();
                                            todo.getRef().child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot task : snapshot.getChildren()) {
                                                        //Loop 1 to go through all child nodes of joined members
                                                            TaskList task_temp = new TaskList();
                                                            task_temp.setBelongTo(task.child("belongTo").getValue().toString());
                                                            task_temp.setTaskTitle(task.child("taskTitle").getValue().toString());
                                                            String CB = task.child("taskCB").getValue().toString();
                                                            boolean cb = false;
                                                            if (CB.equals("true")) {
                                                                cb = true;
                                                                Log.d("Espania", "onDataChange: Ifben vagyok! " + cb);
                                                            }
                                                            task_temp.setTaskCB(cb);
                                                            tasks.add(task_temp);
                                                    }


                                                    final Handler handler = new Handler();
                                                    final int delay = 1000; //milliseconds

                                                    Log.d("WTF", "run: Here is Jimmi!" + tasks.size());
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {
                                                            if (!tasks.isEmpty())//checking if the data is loaded or not
                                                            {
                                                                Log.d("WTF", "run: Here is Johnny!");
                                                                String td_title = todo.child("toDoListTitle").getValue().toString();
                                                                tempTD.setToDoListTitle(td_title);
                                                                String td_owner = todo.child("owner").getValue().toString();
                                                                tempTD.setOwner(td_owner);

                                                                tempTD.setTaskList(tasks);

                                                                tD = tempTD;

                                                                // Pass the arguments
                                                                // to the parentItemAdapter.
                                                                // These arguments are passed
                                                                // using a method ParentItemList()
                                                                parentItemAdapter = new EventTodoRecyclerViewAdapter(tasks,EventID);
                                                                // Set the layout manager
                                                                // and adapter for items
                                                                // of the parent recyclerview
                                                                RecyclerViewItem.setAdapter(parentItemAdapter);
                                                                RecyclerViewItem.setLayoutManager(layoutManager);
                                                            } else
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
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button todo_btn = myToDoview.findViewById(R.id.ev_todo_button);
        todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialogFragment();
                parentItemAdapter.notifyDataSetChanged();
                Log.d("Espania", "onClick: Heyho");
            }
        });
        return myToDoview;
    }

    private void pushInMyToDoList(final DatabaseReference reference){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot todo: snapshot.getChildren()) {
                    if(!todo.getKey().equals("Type")) {
                        todo.child("taskList").getRef().push().setValue(new TaskList(task_text, uID, todo.getKey()));
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
        addTaskDialogFragment.setListener(EventToDoFragment.this);
        addTaskDialogFragment.setCancelable(true);
        addTaskDialogFragment.show(getActivity().getSupportFragmentManager(),"AddTaskDialogFragment");
    }
}