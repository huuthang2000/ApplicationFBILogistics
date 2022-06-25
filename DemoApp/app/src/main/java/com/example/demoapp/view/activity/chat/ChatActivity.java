package com.example.demoapp.view.activity.chat;

import static com.example.demoapp.Utils.Cons.CURRENT_USER;
import static com.example.demoapp.Utils.Cons.FIREBASE_USER;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_AUDIO;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_VIDEO;
import static com.example.demoapp.Utils.Cons.INTENT_USER;
import static com.example.demoapp.Utils.Cons.KEY_COLLECTION_USERS;
import static com.example.demoapp.Utils.Cons.KEY_FCM_TOKEN;
import static com.example.demoapp.Utils.Cons.KEY_IMAGE_URL;
import static com.example.demoapp.Utils.Cons.KEY_USER_ID;
import static com.example.demoapp.Utils.Cons.NOTIFICATION_BODY;
import static com.example.demoapp.Utils.Cons.NOTIFICATION_TITLE;
import static com.example.demoapp.Utils.Cons.NOTIFICATION_TO;
import static com.example.demoapp.Utils.Cons.NOTIF_IS_GLOBAL;
import static com.example.demoapp.Utils.Cons.PREF_IS_DARK_THEME_ON;
import static com.example.demoapp.Utils.Cons.REMOTE_MSG_DATA;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demoapp.R;
import com.example.demoapp.Utils.PreferenceManager;
import com.example.demoapp.adapter.chat.ChatAdapter;
import com.example.demoapp.databinding.ActivityChatBinding;
import com.example.demoapp.listeners.UsersListenercallvideo;
import com.example.demoapp.model.Chats;
import com.example.demoapp.model.Users;
import com.example.demoapp.notifications.APIService;
import com.example.demoapp.notifications.Client;
import com.example.demoapp.notifications.Data;
import com.example.demoapp.notifications.Response;
import com.example.demoapp.notifications.Sender;
import com.example.demoapp.notifications.Token;
import com.example.demoapp.services.ApiCallVideo.ApiClient;
import com.example.demoapp.services.ApiCallVideo.ApiService;
import com.example.demoapp.view.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private UsersListenercallvideo usersListener;
    private ActivityChatBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDBRef;
    private String hisUid;
    private String myUid;
    private Users   remoteUser;
    private PreferenceManager preferenceManager;
    private String hisImage;
    String namecall = "Anh";
    private APIService apiService;
    boolean notify = false;

    // for checking if use has seen message or not
    ValueEventListener seenEventListener;
    DatabaseReference useRefForSeen;
    Users users;
    private List<Users> listusers;
    List<Chats> chatsList;
    ChatAdapter chatAdapter;

   ImageView imageAudioMeeting, imageVideoMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPreferences();
        setCurrentUserID();
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        // init firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDBRef = firebaseDatabase.getReference("Users");

        // layout for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        // recycleview properties
        binding.rcvChat.setHasFixedSize(true);
        binding.rcvChat.setLayoutManager(linearLayoutManager);

        // create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);


        // search user to get that user's info
        Query userQuery = usersDBRef.orderByChild("uid").equalTo(hisUid);
        // get user picture and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until required info is received
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // get data
                    String name = "" + ds.child("name").getValue();
                    namecall = name;
                    hisImage = "" + ds.child("image").getValue();
                    String typingStatus = "" + ds.child("typingTo").getValue();

                    // check typing  status
                    if (typingStatus.equals(myUid)) {
                        binding.tvUserStatus.setText("typing....");
                    } else {
                        // get value of omlinestatus
                        String onlineStatus = "" + ds.child("onlineStatus").getValue();
                        if (onlineStatus.equals("online")) {
                            binding.tvUserStatus.setText(onlineStatus);
                        } else {
                            // convent timestamp to proper time dat
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa");
                            String date = df.format(Calendar.getInstance().getTime());
                            binding.tvUserStatus.setText("Last seen at: " + date);
                        }
                    }
                    // set data
                    binding.tvUserNameChat.setText(name);
                    try {
                        // image received, set it to imageview intoolbar
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_account).into(binding.profileImageChat);
                    } catch (Exception e) {
                        // there is exception getting picture, set default picture
                        Picasso.get().load(R.drawable.ic_account).into(binding.profileImageChat);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // gọi audio
        binding.imageAudioMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage(getString(R.string.cuocgoithoai));
                initiateCall(  remoteUser);
            }
        });

        // gọi video call
        binding.imageVideoMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // sendMessage("1 Cuộc gọi video đến");

                initiateVideoCall(  remoteUser);}
        });
        // click button to send message
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                // get text from edit text
                String message = binding.etMessage.getText().toString().trim();
                // check if text is empty or not
                if (TextUtils.isEmpty(message)) {
                    // text empty
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message", Toast.LENGTH_SHORT).show();
                } else {
                    // text not empty
                    sendMessage(message);
                }
                //reset edittext after sending message
                binding.etMessage.setText("");

            }
        });
        // click button back
        binding.btnBack.setOnClickListener(v ->{onBackPressed();});

        // check edit text change listener
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    checkTypingStatus("noOne");
                } else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readMessage();
        seenMessage();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                String refreshToken = task.getResult().toString();
//                HashMap<String, Object> token = new HashMap<>();
//                token.put(Cons.KEY_FCM_TOKEN, refreshToken);
                sendFCMTokenToDB(refreshToken);
            }
        });

    }

    private void seenMessage() {
        useRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenEventListener = useRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getReceiver().equals(myUid) && chats.getSender().equals(hisUid)) {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", "1");
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initiateVideoCall(Users user) {

            Intent intent = new Intent(this, InvitationOutgoingActivity.class);
            intent.putExtra(INTENT_USER, user);
        intent.putExtra("namehis",user.getName());
        intent.putExtra("uidhis",hisUid );

           intent.putExtra(INTENT_CALL_TYPE, INTENT_CALL_TYPE_VIDEO);
            startActivity(intent);

    }
//goi thoai
public void initiateCall(Users user) {

        Intent intent = new Intent(this, InvitationOutgoingActivity.class);
        intent.putExtra(INTENT_USER, user);
    intent.putExtra("uidhis",hisUid );
    intent.putExtra("namehis", user.getName());
    intent.putExtra("imagehis",  hisImage);
        intent.putExtra(INTENT_CALL_TYPE, INTENT_CALL_TYPE_AUDIO);
        startActivity(intent);

}
    private void setCurrentUserID() {
        if (FIREBASE_USER == null || CURRENT_USER == null) {
            FIREBASE_USER = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference(KEY_COLLECTION_USERS)
                    .child(FIREBASE_USER.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CURRENT_USER = snapshot.getValue(Users.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }
    private void setNotificationData(String messageText) {
        JSONObject body = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            data.put(NOTIFICATION_TITLE, CURRENT_USER.getName());
            data.put(NOTIFICATION_BODY, messageText);
            data.put(KEY_IMAGE_URL, CURRENT_USER.getImage());
            data.put(KEY_USER_ID, FIREBASE_USER.getUid());
            data.put(KEY_FCM_TOKEN,CURRENT_USER.getFcmToken());
            if (  remoteUser.getFcmToken() != null) {
                body.put(NOTIFICATION_TO,   remoteUser.getFcmToken());
            } else {
                body.put(NOTIFICATION_TO, "");
            }
            body.put(REMOTE_MSG_DATA, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(body);
    }
    private void sendNotification(JSONObject body) {
        ApiClient.getClient().create(ApiService.class).sendNotification(
                body.toString()
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull retrofit2.Response<ResponseBody> response) { }
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {}
        });
    }
    private void readMessage() {
        chatsList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getReceiver().equals(myUid) && chats.getSender().equals(hisUid) ||
                            chats.getReceiver().equals(hisUid) && chats.getSender().equals(myUid)) {
                        chatsList.add(chats);
                    }
                    // adapter
                    chatAdapter = new ChatAdapter(ChatActivity.this, chatsList, hisImage);
                    chatAdapter.notifyDataSetChanged();
                    binding.rcvChat.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /**
     * Checks the preferences of the app at the start
     */
    private void checkPreferences() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(PREF_IS_DARK_THEME_ON)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if (!preferenceManager.getSharedPreferences().contains(NOTIF_IS_GLOBAL)) {
            preferenceManager.putBoolean(NOTIF_IS_GLOBAL, true);
        }

    }

//sendFCM TOKEN

    private void sendFCMTokenToDB(String token) {

        FirebaseDatabase.getInstance().getReference(KEY_COLLECTION_USERS)
                .child(CURRENT_USER.getUid())
                .child(KEY_FCM_TOKEN)
                .setValue(token)
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this,"Thực hiện cuộc gọi!", Toast.LENGTH_LONG).show()
                );
    }
    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", "2");
        databaseReference.child("Chats").push().setValue(hashMap);
        setNotificationData(message);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if(notify){
                    senNotification(hisUid, users.getName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void senNotification(String hisUid, String name, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("tokensNotification");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name+":"+message, "New Message", hisUid, R.drawable.ic_face);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this,""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid(); // currently signed is users uid
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        // update value of onlineStatus of current user
        databaseReference.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        // update value of onlineStatus of current user
        databaseReference.updateChildren(hashMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // hide searchview, as don't need it here
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add_post).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onResume() {
        //set online
        super.onResume();
        checkOnlineStatus("online");
        processExtraData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        // set offline with last seen time stamp
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        useRefForSeen.removeEventListener(seenEventListener);
    }
    private void processExtraData(){
        remoteUser = (Users) getIntent().getSerializableExtra(INTENT_USER);
    }
}