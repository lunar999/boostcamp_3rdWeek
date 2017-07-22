package kyi.boost3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kyu on 2017-07-20.
 */

public class MarkerData extends SQLiteOpenHelper {

    public MarkerData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Marker (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, latitude DOUBLE, longitude DOUBLE, tel TEXT, contents TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
