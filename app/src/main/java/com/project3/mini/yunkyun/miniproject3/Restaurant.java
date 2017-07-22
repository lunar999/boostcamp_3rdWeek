package com.project3.mini.yunkyun.miniproject3;

/**
 * Created by YunKyun on 2017-07-21.
 */

public class Restaurant {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String memo;
    private double latitude;
    private double longitude;

    public Restaurant(){}

    public Restaurant(String name, String address, String phoneNumber, String memo, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.memo = memo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.memo = memo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
