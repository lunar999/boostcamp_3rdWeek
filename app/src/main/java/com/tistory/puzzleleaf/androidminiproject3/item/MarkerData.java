package com.tistory.puzzleleaf.androidminiproject3.item;

/**
 * Created by cmtyx on 2017-07-17.
 */

public class MarkerData {
    private String name;
    private String address;
    private String number;
    private String description;
    private double latitude;
    private double longitude;

    public MarkerData(String name, String address, String number, String description, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
