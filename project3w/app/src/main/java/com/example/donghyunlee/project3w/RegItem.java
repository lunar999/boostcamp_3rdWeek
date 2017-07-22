package com.example.donghyunlee.project3w;

/**
 * Created by DONGHYUNLEE on 2017-07-11.
 */

/*
    컨텐츠에 들어간 Item
 */
public class RegItem {
    private String name;
    private String content;
    private String address;
    private String phoneNum;

    public RegItem() {

    }
    public RegItem(String name, String content, String address, String phoneNum) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.phoneNum = phoneNum;
    }
    /*
        Getter
     */

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getAddress() { return address; }

    public String getContent() { return content; }

    public String getName() {
        return name;
    }

    /*
             Setter
        */

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public void setContent(String content) { this.content = content; }

    public void setName(String name) { this.name = name; }
}
