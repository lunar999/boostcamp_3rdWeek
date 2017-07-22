package com.miniproject.a3rd.a3rdminiproject.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jh on 17. 7. 19.
 */

public class Restaurant extends RealmObject {
    // intent title
    public static final String TITLE_NAME       = "TITLE";
    public static final String ADDRESS_NAME     = "ADDRESS";
    public static final String PHONE_NAME       = "PHONE";
    public static final String CONTENT_NAME     = "CONTENT";


    @PrimaryKey
    private long id;            // id ( For db )
    private String title;       // 맛집 이름
    private String address;     // 주소
    private String phone;       // 전화번호
    private String content;     // 추가 내용

    public Restaurant() {
        this.id = new Date().getTime();
    }

    public Restaurant(String name, String address, String phone, String content) {
        this();
        this.title = name;
        this.address = address;
        this.phone = phone;
        this.content = content;
    }

    // Realm 저장 시 사용
    public void setAll(Restaurant restaurant) {
        this.title = restaurant.getTitle();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.content = restaurant.getContent();

    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getContent() {
        return content;
    }
}
