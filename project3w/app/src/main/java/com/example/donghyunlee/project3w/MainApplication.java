package com.example.donghyunlee.project3w;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by DONGHYUNLEE on 2017-07-20.
 */

public class MainApplication extends Application {

    private  Realm mRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        // Realm 환경 셋팅
        Realm.init(this);
        // Realm 객체 생성
        mRealm = Realm.getDefaultInstance();
    }

    // Getter
    public Realm getmRealm() {
        return mRealm;
    }
}
