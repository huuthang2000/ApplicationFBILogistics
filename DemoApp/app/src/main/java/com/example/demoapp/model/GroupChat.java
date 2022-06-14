package com.example.demoapp.model;

public class GroupChat {
    private String message, sender, timestamp, type, timemessage;

    public GroupChat(){
    }

    public GroupChat(String message, String sender, String timestamp, String type, String timemessage) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.timemessage = timemessage;
    }

    public String getTimemessage() {
        return timemessage;
    }

    public void setTimemessage(String timemessage) {
        this.timemessage = timemessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
