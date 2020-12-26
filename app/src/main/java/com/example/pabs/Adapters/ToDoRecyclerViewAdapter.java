package com.example.pabs.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.HelperClass.ToDoList;
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

public class ToDoRecyclerViewAdapter extends RecyclerView.Adapter<ToDoRecyclerViewAdapter.MyToDoViewHolder>{

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ToDoList> toDoList;

    public ToDoRecyclerViewAdapter(List<ToDoList> toDoList) {
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public MyToDoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mytodo_list_row, viewGroup, false);

        return new MyToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyToDoViewHolder parentViewHolder, int position)
    {
        //firebase
        final DatabaseReference referenceDB = FirebaseDatabase.getInstance().getReference().child("TODO");

        // Create an instance of the ParentItem
        // class for the given position
        ToDoList todolist = toDoList.get(position);

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
        parentViewHolder.toDoListTitle.setText(todolist.getToDoListTitle());
        parentViewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i=0;
                        for(DataSnapshot todo : snapshot.getChildren()){
                            if(i == parentViewHolder.getAdapterPosition()){
                               todo.getRef().child("taskList").removeValue();
                            }
                            ++i;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // Create a layout manager
        // to assign a layout
        // to the RecyclerView.

        // Here we have assigned the layout
        // as LinearLayout with vertical orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.childRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        layoutManager.setInitialPrefetchItemCount(todolist.getTaskList().size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        TaskRecyclerViewAdapter childItemAdapter = new TaskRecyclerViewAdapter(todolist.getTaskList());
        parentViewHolder.childRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.childRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.childRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class MyToDoViewHolder extends RecyclerView.ViewHolder {

        private TextView toDoListTitle;
        private Button delete_btn;
        private RecyclerView childRecyclerView;

        MyToDoViewHolder(final View itemView)
        {
            super(itemView);

            toDoListTitle = itemView.findViewById(R.id.belong);
            delete_btn = itemView.findViewById(R.id.todo_delete_btn);
            childRecyclerView = itemView.findViewById(R.id.task_list);
        }
    }
}
