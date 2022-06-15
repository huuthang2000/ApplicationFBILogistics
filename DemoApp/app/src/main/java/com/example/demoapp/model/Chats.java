package com.example.demoapp.model;

public class Chats {
    private String message, receiver, sender, timestamp,  type, isSeen, timemessage;



    public Chats() {
    }

    public Chats(String message, String receiver, String sender, String timestamp, String type, String isSeen, String timemessage) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.isSeen = isSeen;
        this.timemessage = timemessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getTimemessage() {
        return timemessage;
    }

    public void setTimemessage(String timemessage) {
        this.timemessage = timemessage;
    }
}
