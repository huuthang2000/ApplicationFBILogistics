package com.example.demoapp.view.activity.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoapp.adapter.chat.NotificationAdapter;
import com.example.demoapp.databinding.ActivityNotificationsBinding;
import com.example.demoapp.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Notification> notificationList;
    private NotificationAdapter notificationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotifications();
    }

    private void getAllNotifications() {
        notificationList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            Notification model = ds.getValue(Notification.class);
                            //add to list
                            notificationList.add(model);
                        }
                        //adapter
                        notificationAdapter = new NotificationAdapter(NotificationsActivity.this, notificationList);
                        //set to recyclerview
                        binding.rvNotification.setAdapter(notificationAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}