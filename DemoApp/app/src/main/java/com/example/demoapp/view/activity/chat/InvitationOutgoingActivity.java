package com.example.demoapp.view.activity.chat;

import static com.example.demoapp.Utils.Cons.CURRENT_USER;
import static com.example.demoapp.Utils.Cons.FIREBASE_USER;
import static com.example.demoapp.Utils.Cons.INTENT_ARE_MULTIPLE_SELECTED_USERS;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_AUDIO;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_VIDEO;
import static com.example.demoapp.Utils.Cons.INTENT_SELECTED_USERS;
import static com.example.demoapp.Utils.Cons.INTENT_USER;
import static com.example.demoapp.Utils.Cons.KEY_CALL_MISSED;
import static com.example.demoapp.Utils.Cons.KEY_IMAGE_URL;
import static com.example.demoapp.Utils.Cons.KEY_IMAGE_URL_DEFAULT;
import static com.example.demoapp.Utils.Cons.KEY_USERNAME;
import static com.example.demoapp.Utils.Cons.KEY_USER_ID;
import static com.example.demoapp.Utils.Cons.REMOTE_BASE_SERVER_URL;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_CALL_TYPE;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_DATA;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_GROUP;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION_ACCEPTED;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION_CANCELLED;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION_REJECTED;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITATION_RESPONSE;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_INVITER_TOKEN;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_REGISTRATION_IDS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.demoapp.R;
import com.example.demoapp.Utils.PreferenceManager;
import com.example.demoapp.model.Call;
import com.example.demoapp.model.Users;
import com.example.demoapp.services.ApiCallVideo.ApiClient;
import com.example.demoapp.services.ApiCallVideo.ApiService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Callback;
import retrofit2.Response;

public class InvitationOutgoingActivity extends AppCompatActivity {

    private final DatabaseReference callsRef =
            FirebaseDatabase.getInstance().getReference("Calls");

//    private final DatabaseReference callew =
//            FirebaseDatabase.getInstance().getReference("Chats")
//                    .child(FIREBASE_USER.getUid())
//                    .child("receiver");

    private PreferenceManager prefManager;
    private String inviterToken = null;
    private String callType = null;
    private String group = null;

    private boolean isGroup = false;
    private List<String> idsList;

    private MediaPlayer mediaPlayer;
    private TextView defaultProfileTV, usernameTV;

    private int rejectionCount = 0;
    private int totalReceivers = 0;
    private Users remoteUser;
    private String timestamp;
private String hisUid,hisname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_outgoing);
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("uidhis");
        hisname = intent.getStringExtra("hisname");
        prefManager = new PreferenceManager(getApplicationContext());
        setupMediaPlayer();
        setCallType();
        bindUserData();
        setupRejectIV();
        setupCall();
      //  usernameTV.setText(hisname.toString());
    }

    /**
     * Setups the ringtone when the call is incoming
     */
    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(InvitationOutgoingActivity.this, R.raw.ocsentruyentin);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.pause();
            Toast.makeText(InvitationOutgoingActivity.this, "RENG", Toast.LENGTH_SHORT).show();
            if (remoteUser != null) {
                cancelInvitation(remoteUser.getFcmToken(), null);
            }
        });
    }

    /**
     * Setups the type of the invitation into the views
     */
    private void setCallType() {
        TextView outgoingTV = findViewById(R.id.outgoingTV);
        ImageView callTypeIV = findViewById(R.id.callTypeIV);
        callType = getIntent().getStringExtra(INTENT_CALL_TYPE);

        if (callType != null) {
            if (callType.equals(INTENT_CALL_TYPE_VIDEO)) {
                callTypeIV.setImageResource(R.drawable.ic_video);
                outgoingTV.setText(getString(R.string.outgoing_videocall));
            } else {
                callTypeIV.setImageResource(R.drawable.ic_audio);
                outgoingTV.setText(getString(R.string.outgoing_call));
            }
        }
    }

    /**
     * Setups the user data into the views
     */
    private void bindUserData() {
        defaultProfileTV = findViewById(R.id.defaultProfileTV);
        usernameTV = findViewById(R.id.usernameTV);

        remoteUser = (Users) getIntent().getSerializableExtra(INTENT_USER);
        if (remoteUser != null) {
            if (remoteUser.getImage().equals(KEY_IMAGE_URL_DEFAULT)) {
                defaultProfileTV.setText(remoteUser.getName().substring(0,1));
            } else {
                ImageView profileIV = findViewById(R.id.profileIV);
                defaultProfileTV.setVisibility(View.GONE);
                profileIV.setVisibility(View.VISIBLE);
                Glide.with(InvitationOutgoingActivity.this)
                        .load(remoteUser.getImage())
                        .circleCrop()
                        .into(profileIV);
            }
            usernameTV.setText(remoteUser.getName());
        }
    }

    /**
     * Thiết lập hành vi của ImageView từ chối goi dien
     */
    private void setupRejectIV() {
        ImageView rejectIV = findViewById(R.id.rejectIV);
        rejectIV.setOnClickListener(v -> {
            mediaPlayer.pause();
            if (getIntent().getBooleanExtra(INTENT_ARE_MULTIPLE_SELECTED_USERS, false)) {
                Type type = new TypeToken<ArrayList<Users>>(){}.getType();
                ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra(INTENT_SELECTED_USERS), type);
                cancelInvitation(null, receivers);
            } else if (remoteUser != null) {
                cancelInvitation(remoteUser.getFcmToken(), null);
            }
        });
    }

    /**
     * Thiết lập dữ liệu của cuộc gọi trước khi bắt đầu cuộc gọi
     */
    private void setupCall() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult();

                if (callType != null) {
                    if (getIntent().getBooleanExtra(INTENT_ARE_MULTIPLE_SELECTED_USERS, false)) {
                        Type type = new TypeToken<ArrayList<Users>>(){}.getType();
                        ArrayList<Users> receivers = new Gson().fromJson(getIntent().getStringExtra(INTENT_SELECTED_USERS), type);
                        if (receivers != null) {
                            totalReceivers = receivers.size();
                        }
                        initiateCall(callType, null, receivers);
                    } else if (remoteUser != null) {
                        totalReceivers = 1;
                        initiateCall(callType, remoteUser.getFcmToken(), null);
                    }
                }
            }
        });
    }

    /**
     * Starts the call
     * @param callType Indicates the type of invitation
     * @param receiverToken Contains the tokens of the receivers
     * @param receivers Contains the User objects that will receive the invitation
     */
    private void initiateCall(String callType, String receiverToken, ArrayList<Users> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            idsList = new ArrayList<>();
            StringBuilder usernames = new StringBuilder();
            if (receivers != null && receivers.size() > 0) {
                int i;
                for (i = 0; i < receivers.size(); i++) {
                    tokens.put(receivers.get(i).getFcmToken());
                    idsList.add(receivers.get(i).getUid());
                    usernames.append(receivers.get(i).getName()).append("\n");
                }
                isGroup = true;
                defaultProfileTV.setVisibility(View.GONE);
                Intent intent = getIntent();
              String namehis =   intent.getStringExtra("namehis");
                usernameTV.setText(namehis.toString());
            }

            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(INTENT_CALL_TYPE, REMOTE_MSG_INVITATION);
            data.put(REMOTE_MSG_CALL_TYPE, callType);
            data.put(KEY_USER_ID, CURRENT_USER.getUid());
            if (isGroup) {
                String usernamesString = usernames.append(CURRENT_USER.getName()).toString();
                data.put(KEY_USERNAME, usernamesString);
            } else {
                data.put(KEY_USERNAME, CURRENT_USER.getName());
            }
            data.put(KEY_IMAGE_URL, CURRENT_USER.getImage());
            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken);

            group =
                    prefManager.getString(KEY_USER_ID) + "_" +
                            UUID.randomUUID().toString() .substring(0, 5);
            data.put(REMOTE_MSG_GROUP, group);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION);

            timestamp = String.valueOf(System.currentTimeMillis());
            Call call;
            if (isGroup) {
                String usernamesString = usernames.append(CURRENT_USER.getName()).toString();
                call = new Call(
                        usernamesString,
                        usernamesString,
                        false,
                        callType
                );
                call.setTimestamp(Long.parseLong(timestamp));

                callsRef.child(FIREBASE_USER.getUid()).child(timestamp).setValue(call);
                for (String id : idsList) {
                    callsRef.child(id).child(timestamp).setValue(call);
                }
            } else {
                call = new Call(
                        FIREBASE_USER.getUid(),
                        remoteUser.getUid(),
                        false,
                        callType
                );
                call.setTimestamp(Long.parseLong(timestamp));
                callsRef.child(FIREBASE_USER.getUid()).child(timestamp).setValue(call);
                callsRef.child(remoteUser.getUid()).child(timestamp).setValue(call);
            }
        } catch (Exception e) {
            mediaPlayer.pause();
            Toast.makeText(InvitationOutgoingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Sends the FCM notification to the receivers
     * @param remoteMessageBody Contains the information of the invitation
     * @param type Type of invitation
     */
    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (!type.equals(REMOTE_MSG_INVITATION)) {
                        mediaPlayer.pause();
                        Toast.makeText(InvitationOutgoingActivity.this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                        setCallMissedTrue();
                        finish();
                    }
                } else {
                    mediaPlayer.pause();
                    Toast.makeText(InvitationOutgoingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call call, @NonNull Throwable t) {
                mediaPlayer.pause();
                Toast.makeText(InvitationOutgoingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Cancels the invitation sending a FCM notification indicating that it has been cancelled
     * @param receiverToken Contains the tokens of the receivers
     * @param receivers Contains the User objects that will receive the invitation
     */
    private void cancelInvitation(String receiverToken, ArrayList<Users> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }
            if (receivers != null && receivers.size() > 0) {
                for (Users user : receivers) {
                    tokens.put(user.getFcmToken());
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(INTENT_CALL_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELLED);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION_RESPONSE);

            mediaPlayer.pause();
            setCallMissedTrue();
        } catch (Exception e) {
            mediaPlayer.pause();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Gets the response of the receivers of the invitation
     */
    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)) {
                    try {
                        URL serverURL = new URL(REMOTE_BASE_SERVER_URL);

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(group);
                        if (callType.equals(INTENT_CALL_TYPE_AUDIO)) {
                            builder.setVideoMuted(true);
                        }
                        mediaPlayer.pause();
                        JitsiMeetActivity.launch(InvitationOutgoingActivity.this, builder.build());
                        finish();
                    } catch (Exception e) {
                        mediaPlayer.pause();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(REMOTE_MSG_INVITATION_REJECTED)) {
                    rejectionCount++;
                    if (rejectionCount == totalReceivers) {
                        mediaPlayer.pause();
                        Toast.makeText(context, getString(R.string.rejected), Toast.LENGTH_SHORT).show();
                        setCallMissedTrue();
                        finish();
                    }
                }
            }
        }
    };

    /**
     * If the call has been missed, we store the changes into Firebase
     */
    private void setCallMissedTrue() {
        callsRef.child(FIREBASE_USER.getUid())
                .child(timestamp)
                .child(KEY_CALL_MISSED)
                .setValue(true);
        if (isGroup) {
            for (String id : idsList) {
                callsRef.child(id)
                        .child(timestamp)
                        .child(KEY_CALL_MISSED)
                        .setValue(true);
            }
        } else {
            callsRef.child(remoteUser.getUid())
                    .child(timestamp)
                    .child(KEY_CALL_MISSED)
                    .setValue(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}