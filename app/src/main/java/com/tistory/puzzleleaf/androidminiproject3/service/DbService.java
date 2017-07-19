package com.tistory.puzzleleaf.androidminiproject3.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.Gson;
import com.tistory.puzzleleaf.androidminiproject3.db.Db;
import com.tistory.puzzleleaf.androidminiproject3.item.MarkerData;

/**
 * Created by cmtyx on 2017-07-18.
 */

public class DbService extends IntentService {

    public DbService() {
        super("DbService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 갱신 작업내용
        if(intent.getAction().equals("dbRead")) {
            Db.markerDatas.clear();
            Db.markerDatas = Db.dbHelper.getResult();
            sendBroadcast(new Intent("dbRefresh"));
        }
        // 삽입 작업내용
        if(intent.getAction().equals("dbInsert")){
            Gson gson = new Gson();
            String markerObj = intent.getStringExtra("obj");
            MarkerData markerData = gson.fromJson(markerObj,MarkerData.class);
            Db.dbHelper.insert(markerData.getName(), markerData.getAddress(), markerData.getNumber(),
                    markerData.getDescription(), String.valueOf(markerData.getLatitude()), String.valueOf(markerData.getLongitude()));
        }
    }
}
