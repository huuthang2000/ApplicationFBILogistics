package com.example.demoapp.adapter.chat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demoapp.R;
import com.example.demoapp.Utils.Cons;
import com.example.demoapp.firebase.CallsListener;
import com.example.demoapp.listeners.OnMultipleCallsListener;
import com.example.demoapp.model.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersListCallsAdapter extends RecyclerView.Adapter<UsersListCallsAdapter.UsersCallViewHolder> {

    private final CallsListener callsListener = new CallsListener();
    private final Context context;
    public final List<Users> userList;
    private final OnMultipleCallsListener onMultipleCallsListener;
    private final List<Users> selectedUsers;
    private Dialog profileDG;

    public UsersListCallsAdapter(Context context, List<Users> userList, OnMultipleCallsListener onMultipleCallsListener) {
        this.context = context;
        this.userList = userList;
        this.onMultipleCallsListener = onMultipleCallsListener;
        selectedUsers = new ArrayList<>();
    }

    public List<Users> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UsersCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersCallViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_user_list_call,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UsersCallViewHolder holder, int position) {
        Users user = userList.get(position);
        holder.setCallData(user);
        profileDG = new Dialog(context, R.style.Theme_FullScreenDialog);
    }

    private void showProfileCard(Users user) {
       // profileDG.setContentView(R.layout.dialog_profile);
        profileDG.setCancelable(true);

        ImageView profileIV = profileDG.findViewById(R.id.profileIV);
        TextView defaultProfileTV = profileDG.findViewById(R.id.defaultProfileTV);

        if (user.getImage().equals(Cons.KEY_IMAGE_URL_DEFAULT)) {
            defaultProfileTV.setText(user.getName().substring(0,1));
        } else {
            defaultProfileTV.setVisibility(View.GONE);
            profileIV.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(user.getImage())
                    .centerCrop()
                    .into(profileIV);
        }

        TextView usernameTV = profileDG.findViewById(R.id.usernameTV);
        usernameTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        usernameTV.setSelected(true);
        usernameTV.setSingleLine(true);
        usernameTV.setText(user.getName());

        TextView aboutTV = profileDG.findViewById(R.id.aboutTV);





        profileDG.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDG.show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UsersCallViewHolder extends RecyclerView.ViewHolder {
        TextView defaultProfileTV, usernameTV, aboutTV;
        ImageView profileIV,
                callIV, videoCallIV,
                selectedUserIV,
                missedCallIV, missedVideoCallIV,
                outgoingCallIV, incomingCallIV;
        ConstraintLayout itemCallLY;

        public UsersCallViewHolder(@NonNull View itemView) {
            super(itemView);

            defaultProfileTV = itemView.findViewById(R.id.defaultProfileTV);
            profileIV = itemView.findViewById(R.id.profileIV);
            aboutTV = itemView.findViewById(R.id.aboutTV);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            callIV = itemView.findViewById(R.id.callIV);
            videoCallIV = itemView.findViewById(R.id.videocallIV);
//            missedCallIV = itemView.findViewById(R.id.missedCallIV);
//            missedVideoCallIV = itemView.findViewById(R.id.missedVideoCallIV);
//            outgoingCallIV = itemView.findViewById(R.id.outgoingCallIV);
//            incomingCallIV = itemView.findViewById(R.id.incomingCallIV);
            selectedUserIV = itemView.findViewById(R.id.selectedUserIV);
            itemCallLY = itemView.findViewById(R.id.itemCallLY);
        }

        void setCallData(Users user) {
            if (user.getImage().equals(Cons.KEY_IMAGE_URL_DEFAULT)) {
                defaultProfileTV.setText(user.getName().substring(0, 1));
                defaultProfileTV.setVisibility(View.VISIBLE);
            } else {
                profileIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(user.getImage())
                        .circleCrop()
                        .into(profileIV);
            }

            defaultProfileTV.setText(user.getName().substring(0,1));
            usernameTV.setText(user.getName());
            aboutTV.setText(user.getName());

            callIV.setOnClickListener(v -> callsListener.initiateCall(user, context));
            videoCallIV.setOnClickListener(v -> callsListener.initiateVideoCall(user, context));

            profileIV.setOnClickListener(v -> showProfileCard(user));
            defaultProfileTV.setOnClickListener(v -> showProfileCard(user));

            //Group call
            itemCallLY.setOnLongClickListener(v -> {
                if (selectedUserIV.getVisibility() != View.VISIBLE) {
                    selectedUsers.add(user);
                    selectedUserIV.setVisibility(View.VISIBLE);
                    callIV.setVisibility(View.GONE);
                    videoCallIV.setVisibility(View.GONE);
                    onMultipleCallsListener.onMultipleUsersAction(true);
                }
                return true;
            });

            itemCallLY.setOnClickListener(v -> {
                if (selectedUserIV.getVisibility() == View.VISIBLE) {
                    selectedUsers.remove(user);
                    selectedUserIV.setVisibility(View.GONE);
                    callIV.setVisibility(View.VISIBLE);
                    videoCallIV.setVisibility(View.VISIBLE);
                    if (selectedUsers.size() == 0) {
                        onMultipleCallsListener.onMultipleUsersAction(false);
                    }
                } else {
                    if (selectedUsers.size() > 0) {
                        selectedUsers.add(user);
                        selectedUserIV.setVisibility(View.VISIBLE);
                        callIV.setVisibility(View.GONE);
                        videoCallIV.setVisibility(View.GONE);
                    }
                }
            });//
        }
    }
}
