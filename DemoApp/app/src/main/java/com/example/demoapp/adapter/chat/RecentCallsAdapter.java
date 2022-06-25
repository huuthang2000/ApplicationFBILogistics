//package com.example.demoapp.adapter.chat;
//
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.demoapp.R;
//import com.example.demoapp.Utils.Cons;
//import com.example.demoapp.firebase.CallsListener;
//import com.example.demoapp.model.Call;
//import com.example.demoapp.model.Users;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//public class RecentCallsAdapter extends RecyclerView.Adapter<RecentCallsAdapter.CallViewHolder> {
//
//    private final CallsListener callsListener = new CallsListener();
//    private final Context context;
//    private final List<Users> userList;
//    private final Map<String, Call> callMap;
//
//    private Dialog profileDG;
//
//    public RecentCallsAdapter(Context context, List<Users> userList, Map<String, Call> callMap) {
//        this.context = context;
//        this.userList = userList;
//        this.callMap = callMap;
//    }
//
//    @NonNull
//    @Override
//    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new CallViewHolder(
//                LayoutInflater.from(parent.getContext()).inflate(
//                        R.layout.item_recent_call,
//                        parent,
//                        false
//                )
//        );
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
//        String keyMessage = (String) callMap.keySet().toArray()[position];
//        holder.setIsRecyclable(false);
//        holder.bind(userList.get(position), callMap.get(keyMessage));
//
//    }
//
//    private void showProfileCard(Users user) {
//       //profileDG.setContentView(R.layout.dialog_profile);
//        profileDG.setCancelable(true);
//
//        ImageView profileIV = profileDG.findViewById(R.id.profileIV);
//        TextView defaultProfileTV = profileDG.findViewById(R.id.defaultProfileTV);
//
//        if (user.getImage().equals(Cons.KEY_IMAGE_URL_DEFAULT)) {
//            defaultProfileTV.setText(user.getName().substring(0,1));
//        } else {
//            defaultProfileTV.setVisibility(View.GONE);
//            profileIV.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(user.getImage())
//                    .centerCrop()
//                    .into(profileIV);
//        }
//
//        TextView usernameTV = profileDG.findViewById(R.id.usernameTV);
//        usernameTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        usernameTV.setSelected(true);
//        usernameTV.setSingleLine(true);
//        usernameTV.setText(user.getUserName());
//
//        TextView aboutTV = profileDG.findViewById(R.id.aboutTV);
//        if (user.getAbout() != null) aboutTV.setText(user.getAbout());
//        else aboutTV.setVisibility(View.GONE);
//
//        profileDG.findViewById(R.id.chatLY).setOnClickListener(v -> {
//            Intent i = new Intent(context, ChatActivity.class);
//            i.putExtra(Constants.INTENT_USER, user);
//            context.startActivity(i);
//        });
//
//        profileDG.findViewById(R.id.callLY).setOnClickListener(v -> callsListener.initiateCall(user, context));
//        profileDG.findViewById(R.id.videocallLY).setOnClickListener(v -> callsListener.initiateVideoCall(user, context));
//
//        profileDG.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        profileDG.show();
//    }
//
//    @Override
//    public int getItemCount() {
//        return callMap.size();
//    }
//
//    class CallViewHolder extends RecyclerView.ViewHolder {
//        TextView defaultProfileTV, usernameTV, dateTV;
//        ImageView profileIV,
//                callIV, videoCallIV,
//                selectedUserIV, groupIV,
//                missedCallIV, missedVideoCallIV,
//                outgoingCallIV, incomingCallIV,
//                onlineIV, offlineIV;
//        CardView statusCV;
//        ConstraintLayout itemCallLY;
//
//        public CallViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            defaultProfileTV = itemView.findViewById(R.id.defaultProfileTV);
//            profileIV = itemView.findViewById(R.id.profileIV);
//            dateTV = itemView.findViewById(R.id.dateTV);
//            usernameTV = itemView.findViewById(R.id.usernameTV);
//            callIV = itemView.findViewById(R.id.callIV);
//            videoCallIV = itemView.findViewById(R.id.videocallIV);
//            missedCallIV = itemView.findViewById(R.id.missedCallIV);
//            missedVideoCallIV = itemView.findViewById(R.id.missedVideoCallIV);
//            outgoingCallIV = itemView.findViewById(R.id.outgoingCallIV);
//            incomingCallIV = itemView.findViewById(R.id.incomingCallIV);
//            selectedUserIV = itemView.findViewById(R.id.selectedUserIV);
//            groupIV = itemView.findViewById(R.id.groupIV);
//            itemCallLY = itemView.findViewById(R.id.itemCallLY);
//            statusCV = itemView.findViewById(R.id.statusCV);
//            onlineIV = itemView.findViewById(R.id.onlineIV);
//            offlineIV = itemView.findViewById(R.id.offlineIV);
//        }
//
//        void bind(Users user, Call call) {
//            if (call.getReceiverID().contains("\n")) {
//                groupIV.setVisibility(View.VISIBLE);
//                statusCV.setVisibility(View.GONE);
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy, HH:mm", Locale.getDefault());
//                dateTV.setText(dateFormat.format(call.getTimestamp()));
//
//                usernameTV.setText(user.getUserName().replace("\n", ", "));
//                usernameTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                usernameTV.setMarqueeRepeatLimit(-1);
//                usernameTV.setSelected(true);
//            } else {
//                if (user.getImage().equals(Cons.KEY_IMAGE_URL_DEFAULT)) {
//                    defaultProfileTV.setText(user.getUserName().substring(0, 1));
//                    defaultProfileTV.setVisibility(View.VISIBLE);
//                } else {
//                    profileIV.setVisibility(View.VISIBLE);
//                    Glide.with(context)
//                            .load(user.getImage())
//                            .circleCrop()
//                            .into(profileIV);
//                }
//
//                if (user.getLastSeen().equals(Constants.KEY_LAST_SEEN_ONLINE)) {
//                    onlineIV.setVisibility(View.VISIBLE);
//                    offlineIV.setVisibility(View.GONE);
//                } else {
//                    onlineIV.setVisibility(View.GONE);
//                    offlineIV.setVisibility(View.VISIBLE);
//                }
//
//                defaultProfileTV.setText(user.getUserName().substring(0, 1));
//                usernameTV.setText(user.getUserName());
//
//                callIV.setOnClickListener(v -> callsListener.initiateCall(user, context));
//                videoCallIV.setOnClickListener(v -> callsListener.initiateVideoCall(user, context));
//
//                profileIV.setOnClickListener(v -> showProfileCard(user));
//                defaultProfileTV.setOnClickListener(v -> showProfileCard(user));
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy, HH:mm", Locale.getDefault());
//                dateTV.setText(dateFormat.format(call.getTimestamp()));
//
//                if (call.getCallType().equals(INTENT_CALL_TYPE_AUDIO)) {
//                    callIV.setVisibility(View.VISIBLE);
//                } else {
//                    videoCallIV.setVisibility(View.VISIBLE);
//                }
//
//                if (call.getCallerID().equals(FIREBASE_USER.getUid())) { //Outgoing call
//                    outgoingCallIV.setVisibility(View.VISIBLE);
//                    if (call.getMissed()) {
//                        outgoingCallIV.setColorFilter(context.getResources().getColor(android.R.color.holo_red_light));
//                    } else {
//                        outgoingCallIV.setColorFilter(context.getResources().getColor(android.R.color.holo_green_light));
//                    }
//                } else { //Incoming call
//                    if (call.getMissed()) {
//                        if (call.getCallType().equals(INTENT_CALL_TYPE_VIDEO)) {
//                            missedVideoCallIV.setVisibility(View.VISIBLE);
//                        } else {
//                            missedCallIV.setVisibility(View.VISIBLE);
//                        }
//                    } else {
//                        incomingCallIV.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        }
//    }
//}