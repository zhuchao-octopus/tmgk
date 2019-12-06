package com.wxs.scanner.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ztz on 2017/3/24 0024.
 */

public class DataHelper extends SQLiteOpenHelper{
    String createStr = "create table result(_id integer primary key autoincrement , decode_result varchar , " +
            "decode_time integer , usable integer)";
    public DataHelper(Context context) {
        super(context, "scan.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createStr);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
