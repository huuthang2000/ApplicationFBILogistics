package com.example.demoapp.model;

import java.io.Serializable;

public class DomImport implements Serializable {
    private String stt;
    private String productName;
    private String weight;
    private String quantity;
    private String temp;
    private String address;
    private String portReceive;
    private String length;
    private String height;
    private String width;
    private String type;
    private String month;
    private String continent;
    private String createdDate;
    private String pTime;

    public DomImport(){}

    public DomImport(String stt, String productName, String weight, String quantity, String temp,
                     String address, String portReceive, String length, String height, String width,
                     String type, String month, String continent, String createdDate, String pTime) {
        this.stt = stt;
        this.productName = productName;
        this.weight = weight;
        this.quantity = quantity;
        this.temp = temp;
        this.address = address;
        this.portReceive = portReceive;
        this.length = length;
        this.height = height;
        this.width = width;
        this.type = type;
        this.month = month;
        this.continent = continent;
        this.createdDate = createdDate;
        this.pTime = pTime;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPortReceive() {
        return portReceive;
    }

    public void setPortReceive(String portReceive) {
        this.portReceive = portReceive;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
