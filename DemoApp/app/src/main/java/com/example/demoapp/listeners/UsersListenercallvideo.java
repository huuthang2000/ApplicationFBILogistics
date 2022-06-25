package com.example.demoapp.listeners;

import android.view.View;

import com.example.demoapp.model.Users;
import com.google.firebase.auth.FirebaseUser;

public interface UsersListenercallvideo {

    void initiateVideoMeeting(Users user);


    View.OnClickListener initiateAudioMeeting(FirebaseUser user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);

}
