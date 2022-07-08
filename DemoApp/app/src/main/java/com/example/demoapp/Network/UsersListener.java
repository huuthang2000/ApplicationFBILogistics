package com.example.demoapp.Network;


import com.example.demoapp.model.Users;

public interface
UsersListener {
    void initiateVideoMeeting(Users user);

    void initiateAudioMeeting(Users user);
}
