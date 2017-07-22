package com.example.donghyunlee.project3w;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DONGHYUNLEE on 2017-07-20.
 */

public class StoreVO extends RealmObject {

    /**
        칼럼 DATA
     */
    @PrimaryKey
    private long id;
    private String StoreName;
    private String StoreAddress;
    private String StorePhone;
    private String StoreContent;
    private double MakerLat;
    private double MakerLon;

    /*
        Setter
     */
    public void setId(long id) {
        this.id = id;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public void setStoreAddress(String storeAddress) {
        StoreAddress = storeAddress;
    }

    public void setStorePhone(String storePhone) {
        StorePhone = storePhone;
    }

    public void setStoreContent(String storeContent) {
        StoreContent = storeContent;
    }

    public void setMakerLon(double makerLon) {
        MakerLon = makerLon;
    }

    public void setMakerLat(double makerLat) {

        MakerLat = makerLat;
    }
/*
            Getter
         */

    public long getId() {
        return id;
    }

    public String getStoreName() {
        return StoreName;
    }

    public String getStoreAddress() {
        return StoreAddress;
    }

    public String getStorePhone() {
        return StorePhone;
    }

    public String getStoreContent() {
        return StoreContent;
    }

    public double getMakerLat() {
        return MakerLat;
    }

    public double getMakerLon() {
        return MakerLon;
    }
}
