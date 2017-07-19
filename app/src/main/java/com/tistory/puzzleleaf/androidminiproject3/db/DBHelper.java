package com.tistory.puzzleleaf.androidminiproject3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.tistory.puzzleleaf.androidminiproject3.item.MarkerData;

import java.util.ArrayList;

/**
 * Created by cmtyx on 2017-07-16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL("CREATE TABLE MARKER (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, number TEXT, description TEXT, latitude TEXT, longitude TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String name, String address, String number, String description, String latitude, String longitude) {
        // 읽고 쓰기가 가능하게
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO MARKER VALUES(null, '" + name + "', '" + address + "', '"+ number +"', '" + description + "', '" + latitude + "', '"+ longitude +"');");
        db.close();
    }

    public String selectName(String address){
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from MARKER where address = '"+address+"';";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            String info = cursor.getString(1) + " " + cursor.getString(3) + " " + cursor.getString(4);
            return info;
        }
        else {
            return "정보가 없습니다.";
        }
    }

    public ArrayList<MarkerData> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<MarkerData> markers = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM MARKER", null);
        while (cursor.moveToNext()) {
            markers.add(new MarkerData(cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4),
                    Double.parseDouble(cursor.getString(5)),Double.parseDouble(cursor.getString(6))));
        }

        return markers;
    }
}


