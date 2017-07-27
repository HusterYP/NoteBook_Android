package com.example.administrator.notebook_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/7/20.
 */

public class MyDataBase extends SQLiteOpenHelper {
    //记住各项之间打括号，否则出错咯-_-!
    private static final String CREATE_EVENT = "create table Event (" +
            "_id integer primary key autoincrement," +
            "time text," +
            "priority text," +
            "complete integer," +
            "title text," +
            "content text)";

    public MyDataBase(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
