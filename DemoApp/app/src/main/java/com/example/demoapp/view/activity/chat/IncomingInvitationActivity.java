//package com.example.demoapp.view.activity.chat;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.example.demoapp.R;
//import com.example.demoapp.Utils.Constants;
//import com.example.demoapp.model.Users;
//import com.example.demoapp.services.ApiCallVideo.ApiClient;
//import com.example.demoapp.services.ApiCallVideo.ApiService;
//
//import org.jitsi.meet.sdk.JitsiMeetActivity;
//import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.net.URL;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class IncomingInvitationActivity extends AppCompatActivity  {
//    private CircleImageView textFirstChar;
//    private String meetingType = null;
//MediaPlayer player;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_incoming_invitation);
//
//        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
//        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
//
//        if (meetingType != null) {
//            if (meetingType.equals("video")) {
//                imageMeetingType.setImageResource(R.drawable.ic_video);
//            } else {
//                imageMeetingType.setImageResource(R.drawable.ic_audio);
//            }
//        }
//        Users user = (Users) getIntent().getSerializableExtra("Users");
//        textFirstChar  = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.textFirstChar);
//        TextView textUserName = findViewById(R.id.textUserName);
//        TextView textEmail = findViewById(R.id.textEmail);
//        textFirstChar.setImageURI(Uri.parse(user.getImage()));
//        textUserName.setText(String.format("%s",user.getName()));
//        String firstName = getIntent().getStringExtra(Constants.KEY_NAME);
//        if (firstName != null) {
//            textFirstChar.setImageDrawable(getDrawable(R.drawable.ic_face));
//        }
//
//
//
//
//        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
//        imageAcceptInvitation.setOnClickListener(view -> sendInvitationResponse(
//                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
//                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
//        ));
//
//        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);
//        imageRejectInvitation.setOnClickListener(view -> sendInvitationResponse(
//                Constants.REMOTE_MSG_INVITATION_REJECTED,
//                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
//        ));
//
//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                play();
//            }
//
//            public void onFinish() {
//                stop();
//                sendInvitationResponse(
//                        Constants.REMOTE_MSG_INVITATION_REJECTED,
//                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
//
//                finish();
//                Toast.makeText(IncomingInvitationActivity.this,"Kết thúc", Toast.LENGTH_SHORT).show();
//            }
//
//        }.start();
//
//
//    }
//public  void play(){
//if(player == null){
//    player = MediaPlayer.create(this,R.raw.ocsentruyentin);
//}
//player.start();
//
//}
//    public  void stop(){
//if(player!= null){
//    player.release();
//    player = null;
//    Toast.makeText(IncomingInvitationActivity.this,"Tắt âm thanh", Toast.LENGTH_SHORT).show();
//}
//    }
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
//
//    private void sendRemoteMessage(String remoteMessageBody, String type) {
//        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
//                Constants.getRemoteMessageHeaders(), remoteMessageBody
//        ).enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful()) {
//                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
//
//                        try {
//                            URL serverURL = new URL("https://meet.jit.si");
//
//                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
//                            builder.setServerURL(serverURL);
//                           builder.setSubject("Call thả ga >_");
//                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
//
//                            if (meetingType.equals("audio")) {
//                                builder.setVideoMuted(true);
//
//                            }
//                            JitsiMeetActivity.launch(IncomingInvitationActivity.this, builder.build());
//                            finish();
//
//                        } catch (Exception exception) {
//                            Toast.makeText(IncomingInvitationActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    } else {
//                        Toast.makeText(IncomingInvitationActivity.this, "Bạn đã từ chối cuộc gọi", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                } else {
//                    Toast.makeText(IncomingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Toast.makeText(IncomingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
//
//    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//           String  type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_CANCELLED);
//            Log.e("c","đã nhận của outcommingacti");
//            if (type != null) {
//
//                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
//                    Toast.makeText(IncomingInvitationActivity.this, "Đối phương đã kết thúc cuộc gọi", Toast.LENGTH_SHORT).show();
//                    finish();
//
//                }
//            }
//
//        }
//    };
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
//
//}