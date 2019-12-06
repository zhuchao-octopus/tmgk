package com.wxs.scanner.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztz on 2017/3/24 0024.
 */

public class DbUtils {

    private String table="result";
    private DataHelper mDataHelper;
    private SQLiteDatabase db;

    public DbUtils(Context context) {

        mDataHelper = new DataHelper(context);
    }

    public DbUtils(Context context, String table) {
        mDataHelper = new DataHelper(context);
        this.table=table;
    }
    public void saveScanResult(String result, long time,boolean usable) {
        db = mDataHelper.getWritableDatabase();
        db.beginTransaction();
        try {
//            ContentValues values = new ContentValues();
//            values.put("decode_result", result);
//            values.put("decode_time", time);
//            values.put("usable", usable);
//            db.insert(table, null, values);
        db.execSQL("insert into "+table+"(decode_result,decode_time,usable)values(?,?,?)", new Object[]{result, time, usable});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteScanResult(String name) {
        db = mDataHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("delete from "+table+" where decode_result=?" ,new Object[]{name});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Long> queryScanResult(String name) {
        db = mDataHelper.getReadableDatabase();
        db.beginTransaction();
        List<Long> times;
        try {
            times = new ArrayList<>();
            Cursor cursor = db.query(table, new String[]{"decode_time"},"decode_result=?",new String[]{name}, null, null, null);
            while (cursor.moveToNext()) {

                long time = cursor.getLong(0);
                times.add(time);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return times;
    }
}
