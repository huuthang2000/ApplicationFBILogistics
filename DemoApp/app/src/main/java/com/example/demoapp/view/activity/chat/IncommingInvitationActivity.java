package com.example.demoapp.view.activity.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.demoapp.Network.ApiClient;
import com.example.demoapp.Network.ApiService;
import com.example.demoapp.R;
import com.example.demoapp.databinding.ActivityIncommingInvitationBinding;
import com.example.demoapp.utilities.Constants;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncommingInvitationActivity extends AppCompatActivity {

    ActivityIncommingInvitationBinding binding;
    private String meetingType = null;
    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncommingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_video);
            } else {
                binding.imageMeetingType.setImageResource(R.drawable.outline_call_24);
            }
        }
        String callerName = getIntent().getStringExtra("username");
        //  String phoneNo = getIntent().getStringExtra("phoneNo");
        if (callerName != null) {
            binding.textFirstChar.setText(callerName.substring(0, 1));
        }
        binding.textUserName.setText(callerName);
        // binding.IncommingphoneNumber.setText(phoneNo);

        //when User Accepted CALL (intents here are coming from Messaging service
        binding.acceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

            }
        });
        //when rejected
        binding.rejectInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

                finish();
            }
        });
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

                play();

            }

            public void onFinish() {
                stop();
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

                finish();
                Toast.makeText(IncommingInvitationActivity.this,"Kết thúc", Toast.LENGTH_SHORT).show();
            }

        }.start();
    }

    //for invitation Response
    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);


            sendRemoteMessage(body.toString(), type);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //sending Data using API
    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(Constants.getRemoteMessageHeaders()
                , remoteMessageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        //jitsi Setup
                        try {

                            URL serverURL = new URL("https://meet.jit.si");
                            //for audio calling
                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                            if(meetingType.equals("audio"))
                            {
                                builder.setAudioOnly(true);
                            }
                            JitsiMeetActivity.launch(IncommingInvitationActivity.this,builder.build());
                            finish();

                        } catch (Exception e) {
                            Toast.makeText(IncommingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(IncommingInvitationActivity.this, "Kết thúc cuộc gọi", Toast.LENGTH_SHORT).show();
                        stop();
                        finish();

                    }
                } else {
                    Toast.makeText(IncommingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, Throwable t) {
                Toast.makeText(IncommingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                stop();
            }
        });
    }
    public  void play(){
        if(player == null){
            player = MediaPlayer.create(this,R.raw.amthanhgoitoi);
        }
        player.start();

    }
    public  void stop(){
        if(player!= null){
           player.stop();
            Toast.makeText(IncommingInvitationActivity.this,"Tắt âm thanh", Toast.LENGTH_SHORT).show();
        }
    }
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "DP đã hủy cuộc gọi", Toast.LENGTH_SHORT).show();
                    player.release();
                    player.stop();
                    finish();

                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver, new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
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