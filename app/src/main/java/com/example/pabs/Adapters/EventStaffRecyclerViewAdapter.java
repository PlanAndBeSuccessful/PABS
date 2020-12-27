package com.example.pabs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

public class EventStaffRecyclerViewAdapter extends RecyclerView.Adapter<EventStaffRecyclerViewAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<String> mData;

    /**
     * Constructor of EventRecyclerViewAdapter
     */
    public EventStaffRecyclerViewAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    /**
     * OnCreateViewHolder - layout initializer
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Init view and set view of viewHolder
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_event_staff, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Set data for every individual card
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        //set data
        final DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("USER");
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getKey().equals(mData.get(position))) {
                        holder.tv_name.setText(user.child("user_name").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, mData.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Returns size of cards
     */
    @Override
    public int getItemCount() {
        if(mData == null){
            return 0;
        }
        else{
            return mData.size();
        }

    }

    /**
     * Creates card item
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //UI
        TextView tv_name;
        CardView cardView;

        /**
         * Creates view for the item
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //get UI as objects
            tv_name = (TextView) itemView.findViewById(R.id.c_e_s_tv);
            cardView = (CardView) itemView.findViewById(R.id.card_event_staff);

        }
    }
}