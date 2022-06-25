package com.example.demoapp.model;

import com.example.demoapp.Models.objectFirebase.account.fb_Gps;
import com.example.demoapp.Models.objectFirebase.account.fb_Location;
import com.example.demoapp.Utils.keyUtils;

import java.io.Serializable;
import java.util.Calendar;

public class Users implements Serializable {

    public Users(String name, String image, String fcmToken) {
        this.name = name;
        this.image = image;
        this.fcmToken = fcmToken;
    }

    private String   search, cover, uid;
    String fcmToken;


     String image;

    private String email;

    private String phone;

     String name;

    private com.example.demoapp.Models.objectFirebase.account.fb_Gps gps;

    private String keyToken;

    private fb_Location location;

    private String batteryPercent;

    private String gender;

    private String status;


    private String network;


    private long timeCreate;

    private boolean bought;


    private String type;

    public Users() {
    }


    public fb_Gps getGps() {
        return gps;
    }

    public void setGps(fb_Gps gps) {
        this.gps = gps;
    }

    public String getKeyToken() {
        return keyToken;
    }

    public void setKeyToken(String keyToken) {
        this.keyToken = keyToken;
    }

    public fb_Location getLocation() {
        return location;
    }

    public void setLocation(fb_Location location) {
        this.location = location;
    }

    public String getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(String batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Users(String email, String type) {
        this.email = email;
        this.name = email;
        this.status = keyUtils.ONLINE;
        this.gender = keyUtils.OTHER;
        this.keyToken = keyUtils.NULL;
        this.phone = keyUtils.NULL;
        this.batteryPercent = "100%";
        this.image = keyUtils.NULL;
        this.gps = new fb_Gps(true, Calendar.getInstance().getTimeInMillis());
        this.location = new fb_Location(0.0, 0.0, Calendar.getInstance().getTimeInMillis());
        this.network = System.currentTimeMillis()+"";
        this.type = type;
        this.bought = false;
        this.timeCreate = Calendar.getInstance().getTimeInMillis();
    }
    public Users (String email, String name, String type,String image) {
        this.image = keyUtils.NULL;
        this.email = email;
        this.name = name;
        this.type = type;
        this.gps = new com.example.demoapp.Models.objectFirebase.account.fb_Gps(true, Calendar.getInstance().getTimeInMillis());
        this.location = new fb_Location(0.0, 0.0, Calendar.getInstance().getTimeInMillis());
        this.status = keyUtils.ONLINE;
        this.gender = keyUtils.OTHER;
        this.keyToken = keyUtils.NULL;
        this.phone = keyUtils.NULL;
        this.network = System.currentTimeMillis()+"";
        this.batteryPercent = "100%";
        this.bought = false;
        this.timeCreate = Calendar.getInstance().getTimeInMillis();
    }
    public Users(String image, String email, com.example.demoapp.Models.objectFirebase.account.fb_Gps gps, String keyToken, fb_Location location, String name, String phone, String batteryPercent, String gender, String status, String network, long timeCreate, boolean bought, String type) {
        this.image = image;
        this.email = email;
        this.gps = gps;
        this.keyToken = keyToken;
        this.location = location;
        this.name = name;
        this.phone = phone;
        this.batteryPercent = batteryPercent;
        this.gender = gender;
        this.status = status;
        this.network = network;
        this.timeCreate = timeCreate;
        this.bought = bought;
        this.type = type;
    }

    public Users (String image, String email, com.example.demoapp.Models.objectFirebase.account.fb_Gps gps, String keyToken, fb_Location location, String name, String phone, String batteryPercent, String gender, String status, String network, String type) {
        this.image = image;
        this.email = email;
        this.gps = gps;
        this.keyToken = keyToken;
        this.location = location;
        this.name = name;
        this.phone = phone;
        this.batteryPercent = batteryPercent;
        this.gender = gender;
        this.status = status;
        this.network = network;
        this.type = type;
        this.bought = false;
        this.timeCreate = Calendar.getInstance().getTimeInMillis();
    }

    public Users(String name, String email, String search, String phone, String image, String cover, String uid) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
        this.type = type;
        this.gps = new com.example.demoapp.Models.objectFirebase.account.fb_Gps(true, Calendar.getInstance().getTimeInMillis());
        this.location = new fb_Location(0.0, 0.0, Calendar.getInstance().getTimeInMillis());
        this.status = keyUtils.ONLINE;
        this.gender = keyUtils.OTHER;
        this.keyToken = keyUtils.NULL;
        this.network = System.currentTimeMillis()+"";
        this.batteryPercent = "100%";
        this.bought = false;
        this.timeCreate = Calendar.getInstance().getTimeInMillis();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }


}
