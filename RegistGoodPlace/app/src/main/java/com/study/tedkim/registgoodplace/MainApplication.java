package com.study.tedkim.registgoodplace;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by tedkim on 2017. 7. 20..
 */

public class MainApplication extends Application {

    private Realm mRealm;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

    }

    public Realm getRealmInstatnce(){
        return mRealm;
    }
}
