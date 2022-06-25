package com.example.demoapp.model;

public class Participants {
    private String role, timemessage, timestamp, uid;

    public Participants(){}

    public Participants(String role, String timemessage, String timestamp, String uid) {
        this.role = role;
        this.timemessage = timemessage;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTimemessage() {
        return timemessage;
    }

    public void setTimemessage(String timemessage) {
        this.timemessage = timemessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
