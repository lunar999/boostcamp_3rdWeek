package com.study.tedkim.registgoodplace;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tedkim on 2017. 7. 20..
 */

public class ShopInfo extends RealmObject {

    @PrimaryKey
    private long article_id;

    private String name;
    private String address;
    private String tel;
    private String contents;

    public ShopInfo(){

    }

    public long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(long article_id) {
        this.article_id = article_id;
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
