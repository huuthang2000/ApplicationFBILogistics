package com.example.demoapp.adapter.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.Notification;
import com.example.demoapp.view.activity.chat.PostDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.HolderNotification> {
    private Context context;
    private ArrayList<Notification> notificationList;
    private FirebaseAuth firebaseAuth;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view row_notification
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        //get data
        Notification model = notificationList.get(position);
        String name = model.getsName();
        String notification = model.getNotification();
        String timestamp = model.getTimestamp();
        String image = model.getsImage();
        String date = model.getDate();
        String senderUid = model.getpUid();
        String pId = model.getpId();

        //we will get the name, email, image of the user of notification from his uid
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String image = "" + ds.child("image").getValue();
                            String email = "" + ds.child("email").getValue();

                            //add to model
                            model.setsName(name);
                            model.setsEmail(email);
                            model.setsImage(image);

                            //set to views
                            holder.tvName.setText(name);

                            try {
                                Picasso.get().load(image).placeholder(R.drawable.ic_face).into(holder.ivAvatar);
                            } catch (Exception e) {
                                holder.ivAvatar.setImageResource(R.drawable.ic_face);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.tvNotification.setText(notification);
        holder.tvTime.setText(date);

        //click notification to open post
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start PostDetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId); // will get datail of post using this id, its id of the post clicked
                context.startActivity(intent);
            }
        });

        //long press to show delete notification option
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this notification?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete notification

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseAuth.getUid()).child("Notifications").child(timestamp)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //deeted
                                        Toast.makeText(context,"Notifications deleted...", Toast.LENGTH_SHORT).show();
                                     }
                                 })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });

    }

    public int getItemCount() {
        return notificationList.size();
    }

    class HolderNotification extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvNotification, tvTime;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNotification = itemView.findViewById(R.id.tv_notification);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
