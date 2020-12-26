package com.example.pabs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pabs.Fragments.GroupFragment.GroupFragment;
import com.example.pabs.Models.Group;
import com.example.pabs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.MyViewHolder> {

    //UI
    private final Context mContext;
    private final FragmentManager mFragment;

    //Array
    private final ArrayList<Group> mData;
    private final ArrayList<Group> mDataCopy;

    //data
    private final String mUID;

    /**
     * Constructor of GroupRecyclerViewAdapter
     */
    public GroupRecyclerViewAdapter(Context mContext, ArrayList<Group> mData, FragmentManager fragment, String uID) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = fragment;
        this.mUID = uID;

        //list copy
        mDataCopy = new ArrayList<>();
        Collections.copy(mData, mDataCopy);
        mDataCopy.addAll(mData);

    }

    /**
     * filter for searching through adapter elements
     */
    public void filter(String text) {

        mData.clear();
        if (text.isEmpty()) {
            mData.addAll(mDataCopy);
        } else {
            text = text.toLowerCase();
            for (Group item : mDataCopy) {
                if (item.getGroup_name().toLowerCase().contains(text)) {
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
    public GroupRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Init view and set view of viewHolder
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_group, parent, false);
        return new GroupRecyclerViewAdapter.MyViewHolder(view);
    }

    /**
     * Set data for every individual card
     */
    @Override
    public void onBindViewHolder(@NonNull final GroupRecyclerViewAdapter.MyViewHolder holder, final int position) {

        //set data
        holder.tv_title.setText(mData.get(position).getGroup_name());

        //set click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, mData.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                //Getting events from database and setting them to recyclerview
                DatabaseReference databaseGroupRef;
                databaseGroupRef = FirebaseDatabase.getInstance().getReference().child("GROUP");
                databaseGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (final DataSnapshot group : snapshot.getChildren()) {
                            //Loop to go through all child nodes of event

                            //temp for storing data from database
                            final Group temp = new Group();
                            //setting data to temp from database
                            temp.setGroup_owner(group.child("group_owner").getValue().toString());
                            temp.setGroup_name(group.child("group_name").getValue().toString());
                            temp.setGroup_id(group.child("group_id").getValue().toString());
                            temp.setInvite_code(group.child("invite_code").getValue().toString());

                            final ArrayList<String> joined_members = new ArrayList<>();
                            group.getRef().child("joined_members").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot member : snapshot.getChildren()) {
                                        //Loop to go through all child nodes of joined members
                                        joined_members.add(member.getValue().toString());
                                    }

                                    //add joined members to event
                                    temp.setMember_list(joined_members);


                                    //open Group which matches with the name from the Database Group
                                    if (mData.get(position).getGroup_name().equals(temp.getGroup_name())) {
                                        mFragment
                                                .beginTransaction()
                                                .replace(R.id.fragment_group_container, new GroupFragment(temp, mUID))
                                                .addToBackStack("GroupFragment")
                                                .commit();
                                    }

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
        CardView cardView;

        /**
         * Creates view for the item
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //get UI as objects
            tv_title = (TextView) itemView.findViewById(R.id.c_e_group_title);
            cardView = (CardView) itemView.findViewById(R.id.card_group);

        }
    }
}
