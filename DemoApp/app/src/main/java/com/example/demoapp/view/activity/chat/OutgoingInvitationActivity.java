//package com.example.demoapp.view.activity.chat;
//
//import static com.example.demoapp.Utils.Cons.CURRENT_USER;
//import static com.example.demoapp.Utils.Cons.INTENT_ARE_MULTIPLE_SELECTED_USERS;
//import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE;
//import static com.example.demoapp.Utils.Cons.INTENT_SELECTED_USERS;
//import static com.example.demoapp.Utils.Cons.KEY_IMAGE_URL;
//import static com.example.demoapp.Utils.Cons.KEY_USERNAME;
//import static com.example.demoapp.Utils.Cons.KEY_USER_ID;
//import static com.example.demoapp.Utils.Cons.REMOTE_MSG_CALL_TYPE;
//import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.example.demoapp.R;
//import com.example.demoapp.Utils.Constants;
//import com.example.demoapp.Utils.PreferenceManager;
//import com.example.demoapp.model.Users;
//import com.example.demoapp.services.ApiCallVideo.ApiClient;
//import com.example.demoapp.services.ApiCallVideo.ApiService;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.squareup.picasso.Picasso;
//
//import org.jitsi.meet.sdk.JitsiMeetActivity;
//import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.lang.reflect.Type;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class OutgoingInvitationActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//    private FirebaseDatabase firebaseDatabase;
//    private final DatabaseReference callsRef =
//            FirebaseDatabase.getInstance().getReference("Users");
//    private DatabaseReference usersDBRef;
//    ValueEventListener seenEventListener;
//    private PreferenceManager preferenceManager;
//    private String inviterToken = null;
//    private String meetingRoom  = null;
//    private String meetingType  = null;
//    private List<String> idsList;
//    private boolean isGroup = false;
//    private MediaPlayer mediaPlayer;
//    private TextView defaultProfileTV, usernameTV;
//
//
//    private Users remoteUser;
//    private String timestamp;
//    private TextView  textUsername, textEmail;
//private CircleImageView textFirstChar;
//    private int rejectionCount = 0;
//    private int totalReceivers = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_outgoing_invitation);
//
//        preferenceManager = new PreferenceManager(getApplicationContext());
//        setupMediaPlayer();
//        setCallType();
//        bindUserData();
//        setupRejectIV();
//        setupCall();
//
//
//        Users user = (Users) getIntent().getSerializableExtra("Users");
//        textFirstChar  = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.textFirstChar);
//        textUsername   = findViewById(R.id.textUserName);
//        textEmail      = findViewById(R.id.textEmail);
//
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                Users user = snapshot.getValue(Users.class);
//                if(user!=null){
//                    // textFirstChar.setText(user.getName().substring(0, 1));
//
//                    Picasso.get().load(R.drawable.ic_account).into(textFirstChar);
//            textUsername.setText(String.format("%s",user.getName()));
//            textEmail.setText(user.getEmail());
//                }
//
//        }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
////        if (mDatabase != null) {
////            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
////            // textFirstChar.setText(user.getName().substring(0, 1));
//////            textFirstChar.setImageURI(Uri.parse(user.getImage()));
//////            textUsername.setText(String.format("%s",user.getName()));
//////            textEmail.setText(user.getEmail());
//////        }
////        }
//
//
//
//        //To get inviter token
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                    inviterToken = task.getResult();
//                //If meeting is cancelled from sender side
//                if (meetingType != null) {
//                    if (getIntent().getBooleanExtra("isMultiple", false)) {
//                        Type type = new TypeToken<ArrayList<Users>>() {
//
//                        }.getType();
//                        ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra("Users"), type);
//                        if (receivers != null) {
//                            totalReceivers = receivers.size();
//                        }
//                        initiateMeeting(meetingType, null);
//                    } else {
//
//                            totalReceivers = 1;
//
//                        initiateMeeting(meetingType, null);
//                        }
//
//                }
//            }
//        });
//
//    }
//
//    private void bindUserData() {
//        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
//        imageStopInvitation.setOnClickListener(view -> {
//            mediaPlayer.pause();
//            if (getIntent().getBooleanExtra("isMultiple", false)) {
//                Type type = new TypeToken<ArrayList<Users>>(){}.getType();
//                ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
//                cancelInvitation(null, receivers);
//            } else if (remoteUser != null) {
//                cancelInvitation(remoteUser.getUid(), null);
//            }
//        });
//
//    }
//
////        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
////            if (task.isSuccessful() && task.getResult() != null) {
////                inviterToken = task.getResult().getToken();
////                if (meetingType != null && user != null) {
////                    initiateMeeting(meetingType, user.getUid());
////                }
////
////            }
////        });
//
//
//    private void setupRejectIV() {
//        ImageView rejectIV = findViewById(R.id.rejectIV);
//        rejectIV.setOnClickListener(v -> {
//            mediaPlayer.pause();
//            if (getIntent().getBooleanExtra(INTENT_ARE_MULTIPLE_SELECTED_USERS, false)) {
//                Type type = new TypeToken<ArrayList<Users>>(){}.getType();
//                ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra(INTENT_SELECTED_USERS), type);
//                cancelInvitation(null, receivers);
//            } else if (remoteUser != null) {
//                cancelInvitation(remoteUser.getUid(), null);
//            }
//        });
//    }
//
//    /**
//     * Thiết lập dữ liệu của cuộc gọi trước khi bắt đầu cuộc gọi
//     */
//    private void setupCall() {
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                inviterToken = task.getResult();
//                if (meetingType != null) {
//                    if (getIntent().getBooleanExtra(INTENT_ARE_MULTIPLE_SELECTED_USERS, false)) {
//                        Type type = new TypeToken<ArrayList<Users>>(){}.getType();
//                        ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra(INTENT_SELECTED_USERS), type);
//                        if (receivers != null) {
//                            totalReceivers = receivers.size();
//                        }
//                        initiateCall(meetingType, null, receivers);
//                    } else if (remoteUser != null) {
//                        totalReceivers = 1;
//                        initiateCall(meetingType, remoteUser.getUid(), null);
//                    }
//                }
//            }
//        });
//    }
//
//    private void initiateCall(String callType, String receiverToken, ArrayList<Users> receivers) {
//        try {
//            JSONArray tokens = new JSONArray();
//
//            if (receiverToken != null) {
//                tokens.put(receiverToken);
//            }
//
//            idsList = new ArrayList<>();
//            StringBuilder usernames = new StringBuilder();
//            if (receivers != null && receivers.size() > 0) {
//                int i;
//                for (i = 0; i < receivers.size(); i++) {
//                    tokens.put(receivers.get(i).getUid());
//                    idsList.add(receivers.get(i).getUid());
//                    usernames.append(receivers.get(i).getName()).append("\n");
//                }
//                isGroup = true;
//                defaultProfileTV.setVisibility(View.GONE);
//                usernameTV.setText(usernames.toString());
//            }
//
//            tokens.put(receiverToken);
//
//            JSONObject body = new JSONObject();
//            JSONObject data = new JSONObject();
//
//            data.put(INTENT_CALL_TYPE, REMOTE_MSG_INVITATION);
//            data.put(REMOTE_MSG_CALL_TYPE, callType);
//            data.put(KEY_USER_ID, CURRENT_USER.getUid());
//            if (isGroup) {
//                String usernamesString = usernames.append(CURRENT_USER.getName()).toString();
//                data.put(KEY_USERNAME, usernamesString);
//            } else {
//                data.put(KEY_USERNAME, CURRENT_USER.getName());
//            }
//            data.put(KEY_IMAGE_URL, CURRENT_USER.getImage());
//            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken);
//
//            group =
//                    prefManager.getString(KEY_USER_ID) + "_" +
//                            UUID.randomUUID().toString() .substring(0, 5);
//            data.put(REMOTE_MSG_GROUP, group);
//
//            body.put(REMOTE_MSG_DATA, data);
//            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);
//
//            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION);
//
//            timestamp = String.valueOf(System.currentTimeMillis());
//            Call call;
//            if (isGroup) {
//                String usernamesString = usernames.append(CURRENT_USER.getUserName()).toString();
//                call = new Call(
//                        usernamesString,
//                        usernamesString,
//                        false,
//                        callType
//                );
//                call.setTimestamp(Long.parseLong(timestamp));
//
//                callsRef.child(FIREBASE_USER.getUid()).child(timestamp).setValue(call);
//                for (String id : idsList) {
//                    callsRef.child(id).child(timestamp).setValue(call);
//                }
//            } else {
//                call = new Call(
//                        FIREBASE_USER.getUid(),
//                        remoteUser.getId(),
//                        false,
//                        callType
//                );
//                call.setTimestamp(Long.parseLong(timestamp));
//                callsRef.child(FIREBASE_USER.getUid()).child(timestamp).setValue(call);
//                callsRef.child(remoteUser.getId()).child(timestamp).setValue(call);
//            }
//        } catch (Exception e) {
//            mediaPlayer.pause();
//            Toast.makeText(InvitationOutgoingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
//
//
//    private void sendInvitationResponse(String type, String receiverToken) {
//        try {
//
//            JSONArray tokens = new JSONArray();
//            tokens.put(receiverToken);
//
//            JSONObject body = new JSONObject();
//            JSONObject data = new JSONObject();
//
//            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
//            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);
//
//            body.put(Constants.REMOTE_MSG_DATA, data);
//            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
//
//            sendRemoteMessage(body.toString(), type);
//
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private void initiateMeeting(String meetingType, String receiverToken) {
//        try {
//
//            JSONArray tokens = new JSONArray();
//            tokens.put(receiverToken);
////            if (receiverToken != null) {
////                tokens.put(receiverToken);
////            }
////            if (receivers != null && receivers.size() > 0) {
////                StringBuilder userNames = new StringBuilder();
////                for (int i = 0; i < receivers.size(); i++) {
////                    tokens.put(receivers.get(i).token);
////                    userNames.append(receivers.get(i).firstName).append(" ").append(receivers.get(i).lastName).append("\n");
////                }
////                textFirstChar.setVisibility(View.GONE);
////                textEmail.setVisibility(View.GONE);
////                textUsername.setText(userNames.toString());
////            }
//
////            if (receiverToken != null) {
////                tokens.put(receiverToken);
////            }
//
////            if (receivers != null && receivers.size() > 0) {
////                StringBuilder userNames = new StringBuilder();
////                for (int i=0; i < receivers.size(); i++) {
////                    tokens.put(receivers.get(i).token);
////                    userNames.append(receivers.get(i).firstName).append(" ").append(receivers.get(i).lastName).append("\n");
////                }
////
////                textFirstChar.setVisibility(View.GONE);
////                textEmail.setVisibility(View.GONE);
////                textUsername.setText(userNames.toString());
////            }
//
//            JSONObject body = new JSONObject();
//            JSONObject data = new JSONObject();
//
//            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
//            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
//            data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
//            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
//            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
//
//            meetingRoom =
//                    preferenceManager.getString(Constants.KEY_USER_ID) + "_" +
//                            UUID.randomUUID().toString().substring(0, 5);
//            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);
//
//            body.put(Constants.REMOTE_MSG_DATA, data);
//            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
//
//            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
//
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//
//
//    private void sendRemoteMessage(String remoteMessageBody, String type) {
//        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
//                Constants.getRemoteMessageHeaders(), remoteMessageBody
//        ).enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
//                if (response.isSuccessful()) {
//                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
//                        Toast.makeText(OutgoingInvitationActivity.this, "Đang thực hiện cuộc gọi!", Toast.LENGTH_SHORT).show();
//                    }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
//                        Toast.makeText(OutgoingInvitationActivity.this, "Cuộc gọi đã bị hủy", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                } else {
//                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
//
//
//    private void cancelInvitation(String receiverToken, Users receivers) {
//        try {
//
//            JSONArray tokens = new JSONArray();
//            tokens.put(receiverToken);
//
//            if (receiverToken != null) {
//                tokens.put(receiverToken);
//            }
//
////            if (receivers != null ) {
////
////                for (Users user : receivers) {
////                    tokens.put(user.getUid());
////                }
////            }
//
//            JSONObject body = new JSONObject();
//            JSONObject data = new JSONObject();
//
//            data.put(Constants.REMOTE_MSG_DATA, Constants.REMOTE_MSG_INVITATION_RESPONSE);
//            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);
//            Log.e("B","đã gửi qua incommingacti");
//            //data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_denied);
//            body.put(Constants.REMOTE_MSG_DATA, data);
//            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
//
//            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);
//
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
//            if (type != null) {
//                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
//                    try {
//                        URL serverURL = new URL("https://meet.jit.si");
//
//                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
//                        builder.setServerURL(serverURL);
////                        builder.setWelcomePageEnabled(false);
//                        builder.setRoom(meetingRoom);
//
//                        if (meetingType.equals("audio")) {
//                            builder.setVideoMuted(true);
//
//                        }
//                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
//                        finish();
//
//                    } catch (Exception e) {
//                        Toast.makeText(OutgoingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        finish();
//                    }                }
//
//                else if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
//                    Toast.makeText(OutgoingInvitationActivity.this, "Đối phương đã kết thúc cuộc gọi", Toast.LENGTH_SHORT).show();
//                    finish();
//
//                }
//            }
//        }
//    };
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
//                invitationResponseReceiver,
//                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
//        );
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
//                invitationResponseReceiver
//        );
//    }
//    private void setupMediaPlayer() {
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ocsentruyentin);
//        mediaPlayer.start();
//
//        mediaPlayer.setOnCompletionListener(mp -> {
//            mediaPlayer.pause();
//            Toast.makeText(getApplicationContext(), "RENG RENG", Toast.LENGTH_SHORT).show();
//            if (remoteUser != null) {
//                cancelInvitation(remoteUser.getUid(), null);
//            }
//        });
//    }
//    private void setCallType() {
//        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
//        meetingType = getIntent().getStringExtra("type");
//
//        if (meetingType != null) {
//            if (meetingType.equals("video")) {
//                imageMeetingType.setImageResource(R.drawable.ic_video);
//            } else {
//                imageMeetingType.setImageResource(R.drawable.ic_audio);
//            }
//        }}
//}