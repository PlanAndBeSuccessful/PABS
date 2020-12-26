package com.example.pabs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Fragments.EventFragment.EventFragment;
import com.example.pabs.Models.DatabaseEvent;
import com.example.pabs.Models.Event;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates an array of card elements
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<Event> mData;
    private final List<Event> mDataCopy;
    private final FragmentManager mFragment;
    private final String mUID;

    /**
     * Constructor of EventRecyclerViewAdapter
     */
    public EventRecyclerViewAdapter(Context mContext, List<Event> mData, FragmentManager fragment, String uID) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = fragment;
        this.mUID = uID;

        //list copy
        mDataCopy = new ArrayList<>();
        Collections.copy(mData, mDataCopy);
        mDataCopy.addAll(mData);

    }

    public void filter(String text) {
        mData.clear();
        if (text.isEmpty()) {
            mData.addAll(mDataCopy);
        } else {
            text = text.toLowerCase();
            for (Event item : mDataCopy) {
                if (item.getTitle().toLowerCase().contains(text)) {
                    mData.add(item);
                }
            }
        }
        notifyDataSetChanged();
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
        view = mInflater.inflate(R.layout.cardview_event, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Set data for every individual card
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        //set data
        holder.tv_title.setText(mData.get(position).getTitle());

        Picasso.get().load(mData.get(position).getThumbnail()).resize(400, 400).centerCrop().into(holder.img_thumbnail);

        //set click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, mData.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                if (holder.tv_title.getVisibility() == View.VISIBLE) {
                    //Getting events from database and setting them to recyclerview
                    DatabaseReference databaseEvents;
                    databaseEvents = FirebaseDatabase.getInstance().getReference().child("EVENT");

                    databaseEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (final DataSnapshot event : snapshot.getChildren()) {
                                //Loop to go through all child nodes of event

                                //temp for storing data from database
                                final DatabaseEvent temp = new DatabaseEvent();
                                //setting data to temp from database
                                temp.setOwner_id(event.child("owner_id").getValue().toString());
                                if (event.child("description").getValue() != null) {
                                    temp.setDescription(event.child("description").getValue().toString());
                                }
                                if (event.child("reminder").getValue() != null) {
                                    temp.setReminder(event.child("reminder").getValue().toString());
                                }
                                if (event.child("repetition").getValue() != null) {
                                    temp.setRepetition(event.child("repetition").getValue().toString());
                                }
                                temp.setEvent_name(event.child("event_name").getValue().toString());
                                temp.setLocation_name(event.child("location_name").getValue().toString());
                                String tempx = event.child("location_x").getValue().toString();
                                temp.setLocation_x(Double.parseDouble(tempx));
                                String tempy = event.child("location_y").getValue().toString();
                                temp.setLocation_y(Double.parseDouble(tempy));
                                temp.setStart_date(event.child("start_date").getValue().toString());
                                temp.setEnd_date(event.child("end_date").getValue().toString());
                                temp.setPriv_pub(event.child("priv_pub").getValue().toString());

                                final List<String> staff_members = new ArrayList<>();
                                event.getRef().child("staff_members").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot staff : snapshot.getChildren()) {
                                            //Loop 1 to go through all child nodes of staff members
                                            staff_members.add(staff.getValue().toString());
                                        }

                                        //add staff members to event
                                        temp.setStaff_members(staff_members);

                                        //if event has a thumbnail add it to temp
                                        if (event.child("thumbnail").getValue() != null) {
                                            temp.setThumbnail(event.child("thumbnail").getValue().toString());
                                        }


                                        final List<String> joined_members = new ArrayList<>();
                                        event.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for (DataSnapshot staff : snapshot.getChildren()) {
                                                    //Loop 1 to go through all child nodes of staff members
                                                    joined_members.add(staff.getValue().toString());
                                                }

                                                //add staff members to event
                                                temp.setJoined_members(joined_members);

                                                //open Event which matches with the title from the Database Event
                                                if (mData.get(position).getTitle().equals(temp.getEvent_name())) {

                                                    holder.tv_title.postDelayed(new Runnable() {
                                                        public void run() {
                                                            holder.tv_title.setVisibility(View.GONE);
                                                        }
                                                    }, 1000);

                                                    mFragment
                                                            .beginTransaction()
                                                            .replace(R.id.fragment_event_container, new EventFragment(temp, mUID))
                                                            .addToBackStack("EventFragment")
                                                            .commit();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //if database failed
                        }
                    });
                } else {
                    holder.tv_title.setVisibility(View.VISIBLE);

                    holder.tv_title.postDelayed(new Runnable() {
                        public void run() {
                            holder.tv_title.setVisibility(View.GONE);
                        }
                    }, 3000);
                }


            }
        });

    }

    /**
     * Returns size of cards
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Creates card item
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //UI
        TextView tv_title;
        ImageView img_thumbnail;
        CardView cardView;

        /**
         * Creates view for the item
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //get UI as objects
            tv_title = (TextView) itemView.findViewById(R.id.c_e_event_title);
            img_thumbnail = (ImageView) itemView.findViewById(R.id.c_e_thumbnail);
            cardView = (CardView) itemView.findViewById(R.id.card_event);

        }
    }
}
