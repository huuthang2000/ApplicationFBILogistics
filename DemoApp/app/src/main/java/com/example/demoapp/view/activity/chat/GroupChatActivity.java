package com.example.demoapp.view.activity.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.demoapp.R;
import com.example.demoapp.adapter.chat.GroupChatAdapter;
import com.example.demoapp.databinding.ActivityGroupChatBinding;
import com.example.demoapp.model.GroupChat;
import com.example.demoapp.model.Users;
import com.example.demoapp.notifications.Data;
import com.example.demoapp.notifications.Sender;
import com.example.demoapp.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private String groupId, myGroupRole = "";
    private ActivityGroupChatBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<GroupChat> groupChatList;
    private GroupChatAdapter groupChatAdapter;

    //permission request contants
    public static final int CAMERA_REQUEST_CODE = 200;
    public static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    public static final int IMAGE_PICK_GALLERY_CODE = 100;
    public static final int IMAGE_PICK_CAMERA_CODE = 400;

    //Permissions to be requested
    private String[] cameraPermission;
    private String[] storagePermission;
    // uri of picked image
    private Uri image_uri = null;
    private String myUid;

    // volley request queue for notification
    RequestQueue requestQueue;
    boolean notify = false;
    private List<Users> listUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // get id of the group
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        //init required permissions
        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myUid = firebaseUser.getUid();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        listUsers = new ArrayList<>();

        loadGroupInfo();
        loadGroupMessage();
        loadMyGroupRole();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input data
                String message = binding.etMessage.getText().toString().trim();
                //validate
                if (TextUtils.isEmpty(message)) {
                    //empty, don't send
                    Toast.makeText(GroupChatActivity.this, "Can't send empty message...", Toast.LENGTH_SHORT).show();
                } else {
                    // send message
                    sendMessage(message);
                }
            }
        });

        binding.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image from camera/gallery
                showImageImportDialog();
            }
        });

    }

    private void showImageImportDialog() {
        // option (Camera, gallery) to show in dialog
        String[] option = {"Camera", "Gallery"};

        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        // set option to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // item click handle
                if (which == 0) {
                    // camera clicked
                    if (!checkCameraPermissions()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    // gallery clicked
                    if (!checkStoragePermissions()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        // create and show dialog
        builder.create().show();
    }

    private void sendImageMessage(Uri image_uri) {
        //progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Sending Image...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //file name and path in firebase storage
        String filenamepath = "ChatImages/" + "" + System.currentTimeMillis();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filenamepath);
        //upload image
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image uploaded, get url
                        Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!p_uriTask.isSuccessful()) ;
                        String p_downloadUri = p_uriTask.getResult().toString();
                        if (p_uriTask.isSuccessful()) {
                            //image url received, save in database
                            //timestamp
                            String timestamp = "" + System.currentTimeMillis();

                            // convert time stamp to dd//mm/YYYY hh:mm am/pm
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa");
                            String date = df.format(Calendar.getInstance().getTime());

                            //setup message data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", "" + firebaseAuth.getUid());
                            hashMap.put("message", "" + p_downloadUri);
                            hashMap.put("timestamp", "" + timestamp);
                            hashMap.put("timemessage", "" + date);
                            hashMap.put("type", "" + "image"); //text/image/file

                            //add in database
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                            ref.child(groupId).child("Messages").child(timestamp).
                                    setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // message sent
                                            // clear messageET
                                            binding.etMessage.setText("");
                                            pd.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            // message sending failed
                                            Toast.makeText(GroupChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed uploading image
                        Toast.makeText(GroupChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    // xử lý ảnh
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    // xử lý ảnh
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "GroupImageTitle");
        values.put(MediaStore.Images.Media.DESCRIPTION, "GroupImageDescription");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermissions() {
        // check if storage permission is enabled or not
        // return true if enabled
        // return fales if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        // check if camera permission is enabled or not
        // return true if enabled
        // return fales if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean isCameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean isWriteStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (isCameraAccepted && isWriteStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please enable camera & storage permisstion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean isWriteStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (isWriteStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable storage permisstion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                sendImageMessage(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImageMessage(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants")
                .orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            myGroupRole = "" + ds.child("role").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupMessage() {
        //init list
        groupChatList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChatList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            GroupChat model = ds.getValue(GroupChat.class);
                            groupChatList.add(model);
                        }
                        //adapter
                        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, groupChatList);
                        // set to recycleview
                        binding.rvGroupChat.setAdapter(groupChatAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {
        //timestamp
        String timestamp = "" + System.currentTimeMillis();

        // convert time stamp to dd//mm/YYYY hh:mm am/pm
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa");
        String date = df.format(Calendar.getInstance().getTime());

        //setup message data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", "" + firebaseAuth.getUid());
        hashMap.put("message", "" + message);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("timemessage", "" + date);
        hashMap.put("type", "" + "text"); //text/image/file


        //add in database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp).
                setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // message sent
                        // clear messageET
                        binding.etMessage.setText("");
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(groupId).child("Participants")
                                .orderByChild("uid").equalTo(firebaseAuth.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            myGroupRole = "" + ds.child("role").getValue();
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                            ref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    listUsers.clear();
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        //get uid from Group > Participants
                                                        String uid = "" + ds.child("uid").getValue();

                                                        //get info of user using uid we got above
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                                        ref.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                                    Users users = ds.getValue(Users.class);
                                                                    if (!users.getUid().equals(firebaseAuth.getUid())) {
                                                                        listUsers.add(users);
                                                                    }
                                                                }
                                                                for(Users users: listUsers){
                                                                    sendNotification(users.getUid(),users.getName(),""+message);
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
                                            });                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // message sending failed
                        Toast.makeText(GroupChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String groupTitle = "" + ds.child("groupTitle").getValue();
                            String groupDescription = "" + ds.child("groupDescription").getValue();
                            String groupIcon = "" + ds.child("groupIcon").getValue();
                            String groupTimestamp = "" + ds.child("timestamp").getValue();
                            String createdBy = "" + ds.child("createdBy").getValue();


                            binding.tvGroupTitle.setText(groupTitle);
                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_white).into(binding.ivGroupIcon);
                            } catch (Exception exception) {
                                binding.ivGroupIcon.setImageResource(R.drawable.ic_group_white);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendNotification(String hisUid, String name, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data("" + myUid,
                            "" + name + ": " + message,
                            "New Message",
                            "" + hisUid,
                            "ChatNotification",
                            R.drawable.ic_notifications_black);

                    Sender sender = new Sender(data, token.getToken());
                    //fcm json object request
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //response of the request
                                        Log.d("JSON_RESPONSE", "onResponse:" + response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse:" + error.toString());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                // put params
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAnDxU0iU:APA91bGl1M-g_K_E43PrHhOo7Am4lu6gvqNN_NcmiAbR55gryt67ABv2KwCzNK9oKOxkpsgSm-RCmiaDTrKDehmVwM576cL15pJ5pX0s5QWf-RlIP8HVdO01BkVMQw9oua2RDQ_Sxw8B");
                                return headers;
                            }
                        };
                        // add this request to queue
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_create_group).setVisible(false);
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);

        if (myGroupRole.equals("creator") || myGroupRole.equals("admin")) {
            // in admin/creator, show add persor option
            menu.findItem(R.id.action_add_participant).setVisible(true);
        } else {
            menu.findItem(R.id.action_add_participant).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_participant) {
            Intent intent = new Intent(this, GroupParticipantAddActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        } else if (id == R.id.action_groupinfo) {
            Intent intent = new Intent(this, GroupInfoActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}