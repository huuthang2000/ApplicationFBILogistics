package com.example.demoapp.view.activity.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoapp.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    // use shared preferrences to save the state of switch
    SharedPreferences sp;
    // to edit value of shares pref
    SharedPreferences.Editor editor;

    //constant for topic
    //assign any value but use same for this kind of notifications
    private static final String  TOPIC_POST_NOTIFICATION = "POST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //init sp
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+TOPIC_POST_NOTIFICATION, false);
        //if enabled check swich, otherwise uncheck switch- by default unchecked/false
        if(isPostEnabled){
            binding.postSwitch.setChecked(true);
        }else{
            binding.postSwitch.setChecked(false);
        }

        //implement switch change listener
        binding.postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //edit switch state
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();
                if(isChecked){
                    // call to subscribe
                    subscribePostNotification();
                }else{
                    // call to unsubscribe
                    unsubscribePostNotification();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void unsubscribePostNotification() {
        //unsubscribe to a topic (POST) to disable it's notification
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Unsubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotification() {
        //subscribe to a topic (POST) to enable it's notification
            FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "You will receive post notifications";
                            if(!task.isSuccessful()){
                                msg = "Subscription failed";
                            }
                            Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
    }
}