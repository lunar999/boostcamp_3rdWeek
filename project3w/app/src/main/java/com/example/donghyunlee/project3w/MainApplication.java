package com.example.donghyunlee.project3w;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;

/**
 * Created by DONGHYUNLEE on 2017-07-20.
 */

public class MainApplication extends Application {

    private  Realm mRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("??","ddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
         Realm.init(this);
        mRealm = Realm.getDefaultInstance();
    }

    public Realm getmRealm() {
        return mRealm;
    }
}
