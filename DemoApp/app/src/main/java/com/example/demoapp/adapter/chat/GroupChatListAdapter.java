package com.example.demoapp.adapter.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.GroupChatList;
import com.example.demoapp.view.activity.chat.GroupChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.HolderGroupChatList> {

    private Context context;
    private ArrayList<GroupChatList> groupChatLists;

    public GroupChatListAdapter(Context context, ArrayList<GroupChatList> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchats_list, parent, false);
        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
        // get data
        GroupChatList model = groupChatLists.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.tvName.setText("");
        holder.tvTime.setText("");
        holder.tvMessage.setText("");

        //load last message and message-time
        loadLastMessage(model, holder);

        //set data
        holder.tvGroupTitle.setText(groupTitle);
        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_groups_primary).into(holder.ivGroupIcon);
        } catch (Exception exception) {
            holder.ivGroupIcon.setImageResource(R.drawable.ic_groups_primary);
        }

        //handle group click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open group chat
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);
            }
        });
    }

    private void loadLastMessage(GroupChatList model, HolderGroupChatList holder) {
        // get last message from group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            //get data
                            String message = "" + ds.child("message").getValue();
                            String messageType = "" + ds.child("type").getValue();
                            String sender = "" + ds.child("sender").getValue();
                            String timemessage = "" + ds.child("timemessage").getValue();

                            if(messageType.equals("image")){
                                holder.tvMessage.setText("Sent Photo");
                            }else{
                                holder.tvMessage.setText(message);
                            }
                            holder.tvMessage.setText(message);
                            holder.tvTime.setText(timemessage);

                            //get info of sender of last mesage
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.orderByChild("uid").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+ds.child("name").getValue();
                                                holder.tvName.setText(name);
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

                    }
                });
    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    //view holder class
    class HolderGroupChatList extends RecyclerView.ViewHolder {

        private ImageView ivGroupIcon;
        private TextView tvGroupTitle, tvName, tvMessage, tvTime;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);

            ivGroupIcon = itemView.findViewById(R.id.iv_groupIcon);
            tvGroupTitle = itemView.findViewById(R.id.tv_groupTitle);
            tvName = itemView.findViewById(R.id.tv_name_group);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tv_time_group);

        }
    }
}
