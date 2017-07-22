package com.project3.mini.yunkyun.miniproject3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YunKyun on 2017-07-21.
 */

public class RestaurantDAO extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "restaurantManager";
    // Table name
    private static final String TABLE_NAME = "restaurant";
    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_MEMO = "memo";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public RestaurantDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PHONE_NUMBER + " TEXT,"
                + KEY_MEMO + " TEXT,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    List<Restaurant> findAll(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Restaurant> tupleList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Restaurant tuple = new Restaurant();
                tuple.setId(cursor.getInt(0));
                tuple.setName(cursor.getString(1));
                tuple.setAddress(cursor.getString(2));
                tuple.setPhoneNumber(cursor.getString(3));
                tuple.setMemo(cursor.getString(4));
                tuple.setLatitude(cursor.getDouble(5));
                tuple.setLongitude(cursor.getDouble(6));

                tupleList.add(tuple);
            } while (cursor.moveToNext());
        }

        db.close();

        return tupleList;
    }

    boolean insertRestaurant(Restaurant tuple){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tuple.getName());
        values.put(KEY_ADDRESS, tuple.getAddress());
        values.put(KEY_PHONE_NUMBER, tuple.getPhoneNumber());
        values.put(KEY_MEMO, tuple.getMemo());
        values.put(KEY_LATITUDE, tuple.getLatitude());
        values.put(KEY_LONGITUDE, tuple.getLongitude());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id != -1;
    }
}
