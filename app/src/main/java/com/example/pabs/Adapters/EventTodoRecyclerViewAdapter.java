package com.example.pabs.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Models.TaskList;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Creates an array of card elements
 */

public class EventTodoRecyclerViewAdapter extends RecyclerView.Adapter<EventTodoRecyclerViewAdapter.ViewHolder> {

    //Store a member variable for the events
    private final List<TaskList> tasks;
    private final String eventID;

    //Pass-in the contact array into the constructor
    public EventTodoRecyclerViewAdapter(List<TaskList> lstTask, String eventid) {
        tasks = lstTask;
        eventID = eventid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate custom layout
        View eventtodo_listview = inflater.inflate(R.layout.fragment_event_todo_listrow, parent, false);
        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventtodo_listview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //reference to database
        final DatabaseReference referenceDB = FirebaseDatabase.getInstance().getReference().child("TODO").child(eventID);

        //Get the data model based on position
        final TaskList task = tasks.get(position);

        //set item views based on views and data model
        TextView textView = holder.task_textview;
        textView.setText(task.getTaskTitle());
        //Waits for delete button to be clicked
        Button delete = holder.delete_btn;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot todo : snapshot.getChildren()){
                            if(!todo.getKey().equals("Type")){
                                todo.getRef().child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot tk) {
                                        for(DataSnapshot t : tk.getChildren()){
                                            if(t.child("taskTitle").getValue(String.class).equals(task.getTaskTitle())){
                                                t.getRef().removeValue();
                                                tasks.remove(task);
                                                notifyDataSetChanged();
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //Waits for pick task button to be clicked
        Button pick = holder.pick_btn;
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Taskrow", "PickTask button clikced");
            }
        });
        //Waits for checkbox to be checked or unchecked
        CheckBox cb = holder.ev_todo_cb;
        holder.ev_todo_cb.setChecked(task.getTaskCB());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                task.setTaskCB(isChecked);
                referenceDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot todo : snapshot.getChildren()) {
                            if (!todo.getKey().equals("Type")) {
                                todo.getRef().child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot tasks : snapshot.getChildren()) {
                                            if (tasks.child("taskTitle").getValue().toString().equals(task.getTaskTitle())) {
                                                tasks.child("taskCB").getRef().setValue(isChecked);
                                                notifyDataSetChanged();
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView task_textview;
        public Button delete_btn;
        public Button pick_btn;
        public CheckBox ev_todo_cb;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            task_textview = (TextView) itemView.findViewById(R.id.ev_rec_task);
            delete_btn = (Button) itemView.findViewById(R.id.ev_todo_delete_btn);
            pick_btn = (Button) itemView.findViewById(R.id.ev_todo_import_btn);
            ev_todo_cb = (CheckBox) itemView.findViewById(R.id.ev_todo_cb);
        }
    }
}
