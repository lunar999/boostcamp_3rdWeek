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

    private final String DB_INSERT_SERVICE = "dbInsert";
    private final String DB_REFERESH_SERVICE_BROADCAST = "dbRefresh";
    private final String DB_SELECT_SERVICE_BROADCAST = "dbSelect";

    public DbService() {
        super("DbService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 갱신 작업내용
        if(intent.getAction().equals(DB_REFERESH_SERVICE_BROADCAST)) {
            Db.markerDatas.clear();
            Db.markerDatas = Db.dbHelper.getResult();
            sendBroadcast(new Intent(DB_REFERESH_SERVICE_BROADCAST));
        }
        // 삽입 작업내용
        else if(intent.getAction().equals(DB_INSERT_SERVICE)){
            Gson gson = new Gson();
            String markerObj = intent.getStringExtra("obj");
            MarkerData markerData = gson.fromJson(markerObj,MarkerData.class);
            Db.dbHelper.insert(markerData.getName(), markerData.getAddress(), markerData.getNumber(),
                    markerData.getDescription(), String.valueOf(markerData.getLatitude()), String.valueOf(markerData.getLongitude()));
        }
        //select 작업내용
        else if(intent.getAction().equals(DB_SELECT_SERVICE_BROADCAST)){
            String select = intent.getStringExtra("select");
            String result = Db.dbHelper.selectName(select);
            sendBroadcast(new Intent(DB_SELECT_SERVICE_BROADCAST).putExtra("select",result));
        }
    }
}
