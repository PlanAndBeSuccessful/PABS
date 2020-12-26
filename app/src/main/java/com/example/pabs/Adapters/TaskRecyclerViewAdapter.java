package com.example.pabs.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.HelperClass.TaskList;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.MyTaskViewHolder> {

    private List<TaskList> taskList;

    // Constuctor
    TaskRecyclerViewAdapter(List<TaskList> taskList)
    {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mytodo_task_row, viewGroup, false);

        return new MyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyTaskViewHolder childViewHolder, int position)
    {
        final DatabaseReference referenceDB = FirebaseDatabase.getInstance().getReference().child("TODO");
        // Create an instance of the ChildItem
        // class for the given position
        TaskList childItem = taskList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself
        childViewHolder.taskTitle.setText(childItem.getTaskTitle());

        Log.d("Espania", "onBindViewHolder: Ifen kivül vagyok! " + childViewHolder.taskCB);
        if((taskList.get(childViewHolder.getAdapterPosition()).getTaskCB())){
            childViewHolder.taskCB.setChecked(true);
            Log.d("Espania", "onBindViewHolder: Ifben vagyok! " + childViewHolder.taskCB);
        }
        else{
            childViewHolder.taskCB.setChecked(false);
        }

        childViewHolder.taskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                //set your object's last status
                taskList.get(childViewHolder.getAdapterPosition()).setTaskCB(isChecked);
                referenceDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot todo : snapshot.getChildren()){
                            if(todo.getKey().equals(taskList.get(childViewHolder.getAdapterPosition()).getReferenceTo())){
                                todo.getRef().child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot task : snapshot.getChildren()){
                                            if(task.child("taskTitle").getValue().toString().equals(taskList.get(childViewHolder.getAdapterPosition()).getTaskTitle())){
                                                task.child("taskCB").getRef().setValue(isChecked);
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
    public int getItemCount()
    {
        // This method returns the number
        // of items we have added
        // in the ChildItemList
        // i.e. the number of instances
        // of the ChildItemList
        // that have been created
        return taskList.size();
    }

    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class MyTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        CheckBox taskCB;

        MyTaskViewHolder(View itemView)
        {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_task);
            taskCB = itemView.findViewById(R.id.task_cb);
        }
    }
}
