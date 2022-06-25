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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.demoapp.databinding.ActivityGroupCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupCreateActivity extends AppCompatActivity {

    //actionbar
    private ActionBar actionBar;

    private FirebaseAuth firebaseAuth;
    private ActivityGroupCreateBinding binding;

    // permission constanst
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_CODE = 400;

    private Uri image_uri = null;

    // permission array
    String[] cameraPermissions;
    String[] storagePermissions;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Create Group");

        // init  permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //pick image
        binding.ivGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        // handle click event
        binding.createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });

    }

    private void startCreatingGroup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");

        //input title, description
        String groupTitle = binding.etGroupTitle.getText().toString().trim();
        String groupDescription = binding.etGroupDescription.getText().toString().trim();
        //validation
        if(TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Please enter group title...", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        //timestamp: for groupicon image, groupId, timeCreate etc
        String g_timestamp = ""+System.currentTimeMillis();
        if(image_uri == null){
            //creating group without icon image
            createGroup(
                    ""+g_timestamp,
                    ""+groupTitle,
                    ""+groupDescription,
                    ""
            );
            finish();
        }else{
            //creating group without icon image
            //upload image
            //image name and path
            String fileNameAndPath = "Group_Imgs/" + "image" + g_timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded, get url
                            Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!p_uriTask.isSuccessful());
                            Uri p_downloadUri = p_uriTask.getResult();
                            if(p_uriTask.isSuccessful()){
                                createGroup(
                                        ""+g_timestamp,
                                        ""+groupTitle,
                                        ""+groupDescription,
                                        ""+p_downloadUri
                                );
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            progressDialog.dismiss();
                            Toast.makeText(GroupCreateActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createGroup(String timestamps, String groupTitle, String groupDescription, String groupIcon) {
        // convert time stamp to dd//mm/YYYY hh:mm am/pm
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa");
        String date = df.format(Calendar.getInstance().getTime());
        //setup info of group
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", timestamps);
        hashMap.put("groupTitle",groupTitle);
        hashMap.put("groupDescription",groupDescription);
        hashMap.put("groupIcon", groupIcon);
        hashMap.put("timestamp", timestamps);
        hashMap.put("timemessage", date);
        hashMap.put("createdBy", firebaseAuth.getUid());

        //create group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timestamps).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // created successfully

                        //setup member inf (add current user in group's participants list)
                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", firebaseAuth.getUid());
                        hashMap1.put("role", "creator");
                        hashMap1.put("timestamp", timestamps);
                        hashMap1.put("timemessage", date);

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(timestamps).child("Participants").child(firebaseAuth.getUid())
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Participants added
                                        progressDialog.dismiss();
                                        Toast.makeText(GroupCreateActivity.this, "Group created...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed adding participant
                                        progressDialog.dismiss();
                                        Toast.makeText(GroupCreateActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(GroupCreateActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // xử lý ảnh
    private void showImagePickDialog() {
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

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    // xử lý ảnh
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");

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

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
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

                // set to imageview
                binding.ivGroupIcon.setImageURI(image_uri);

            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // was picked from camera
                // set to imageview
                binding.ivGroupIcon.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            actionBar.setSubtitle(user.getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}