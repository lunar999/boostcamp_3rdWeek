package com.miniproject.a3rd.a3rdminiproject;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by jh on 17. 7. 19.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
