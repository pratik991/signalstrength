package com.example.pratikdabhi.signalstrengthsdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pratikdabhi on 10/17/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SignalStrength.db";
    public static final String TABLE_NAME = "signalstrength";
    public static final String COLUMN_ID = "id";
    public static final String COLOMN_TimeStamp = "time";
    public static final String COLUMN_SignalStrength_VALUE = "signalstrengthValue";
    public static final String COLOMN_GPS_LOCATION = "gpslocation";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table signalstrength " +
                        "(id integer primary key,time text, signalstrengthValue text,gpslocation text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME +"");
        onCreate(db);
    }

    public boolean insertData(String time, String signalstrengthValue, String gpsLocation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLOMN_TimeStamp, time);
        contentValues.put(COLUMN_SignalStrength_VALUE, signalstrengthValue);
        contentValues.put(COLOMN_GPS_LOCATION, gpsLocation);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }


    public ArrayList<String> getAllData()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME + "", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLOMN_TimeStamp)));
            array_list.add(res.getString(res.getColumnIndex(COLUMN_SignalStrength_VALUE)));
            array_list.add(res.getString(res.getColumnIndex(COLOMN_GPS_LOCATION)));
            res.moveToNext();
        }
        return array_list;
    }
}

