package com.example.demoapp.model;

public class Notification {
    private String pId, timestamp, pUid, notification, sUid, sName, sImage, date, sEmail;

    public Notification(){}

    public Notification(String pId, String timestamp, String pUid, String notification, String sUid,
                        String sName, String sImage, String date, String sEmail) {
        this.pId = pId;
        this.timestamp = timestamp;
        this.pUid = pUid;
        this.notification = notification;
        this.sUid = sUid;
        this.sName = sName;
        this.sImage = sImage;
        this.date = date;
        this.sEmail = sEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getsEmail() {
        return sEmail;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }


    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }
}
