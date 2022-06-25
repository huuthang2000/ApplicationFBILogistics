package com.example.demoapp.adapter.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.GroupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.HolderGroupChat> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<GroupChat> groupChatArrayList;

    private FirebaseAuth firebaseAuth;

    public GroupChatAdapter(Context context, ArrayList<GroupChat> groupChatArrayList) {
        this.context = context;
        this.groupChatArrayList = groupChatArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
            return new HolderGroupChat(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {

        // get data
        GroupChat model = groupChatArrayList.get(position);
        // if text message then contain message, if image message the contain url of the image stored
        String message = model.getMessage();
        String timestamp = model.getTimestamp();
        String sendenUid = model.getSender();
        String timemessage = model.getTimemessage();
        String messageType = model.getType();

        //set data
        if(messageType.equals("text")){
            //text message, hide ivMessage, show tvMessage
            holder.ivMessage.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(message);
        }else {
            //image message, hide ivMessage, show tvMessage
            holder.ivMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            try{
                Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.ivMessage);
            }catch (Exception e){
                holder.ivMessage.setImageResource(R.drawable.ic_image_black);
            }
        }
        holder.tvTime.setText(timemessage);

        setUsername(model, holder);

    }

    private void setUsername(GroupChat model, HolderGroupChat holder) {
        //get sender info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
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

    @Override
    public int getItemCount() {
        return groupChatArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(groupChatArrayList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {

        private TextView tvName, tvMessage, tvTime;
        private ImageView ivMessage;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_Name);
            tvMessage = itemView.findViewById(R.id.tv_Message);
            tvTime =  itemView.findViewById(R.id.tv_Time);
            ivMessage = itemView.findViewById(R.id.iv_message);

        }
    }


}
