package com.example.administrator.notebook_1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2017/7/20.
 */


public class GlobalName {
    //数据库中的名字
    public static String dbDataBaseName = "Event.db";
    public static String dbEvent = "Event";
    public static String dbId = "_id";
    public static String dbTime = "time";
    public static String dbPriority = "priority";
    public static String dbComplete = "complete";
    public static String dbTitle = "title";
    public static String dbContent = "content";
    public static int dbVersion = 1;
    public static int defaultCount = 0;  //以默认方式排序的ID和数量计数
    public static int priorityCount = 0; //以优先级方式排序的ID和数量计数
    public static int timeCount = 0;     //以截止时间排序的ID和数量计数
    public static boolean deleteFlag = false;
    public static int sortFlag = 0;//排序方式，0为默认排序,1为按优先级排序，2为按时间排序


    //测试数据库
    public static void onTest(Context context) {
        MyDataBase oh = new MyDataBase(context, dbDataBaseName, null, dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int Id = cursor.getInt(cursor.getColumnIndex(GlobalName.dbId));
            String Complete = cursor.getString(cursor.getColumnIndex(GlobalName.dbComplete));
            String Content = cursor.getString(cursor.getColumnIndex(GlobalName.dbContent));
            String Time = cursor.getString(cursor.getColumnIndex(GlobalName.dbTime));
            String Title = cursor.getString(cursor.getColumnIndex(GlobalName.dbTitle));
            int Priority = cursor.getInt(cursor.getColumnIndex(GlobalName.dbPriority));
            Log.d("@HusterYP", String.valueOf(Id + " " + Complete + " "
                    + Content + " " + Time + " " + Title + " " + Priority));
        }
        cursor.close();
    }
}
