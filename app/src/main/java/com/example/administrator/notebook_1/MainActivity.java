package com.example.administrator.notebook_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.notebook_1.GlobalName.deleteFlag;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter defaAdapter;  //默认适配器
    private MyAdapter prioAdapter; //按优先级排序的适配器
    private MyAdapter timeAdapter; //按截止时间排序的适配器
    private int position = -1; //删除的哪一项

    //删除操作
    @Override
    protected void onRestart() {
        super.onRestart();
        if (deleteFlag) {
            if (position != -1) {
                Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show();
                MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
                SQLiteDatabase db = oh.getWritableDatabase();
                //分情况，看是哪种排序方式
                Cursor cursor = null;
                if (GlobalName.sortFlag == 0) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null, null);
                }
                else if (GlobalName.sortFlag == 1) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbPriority, null);
                }
                else if (GlobalName.sortFlag == 2) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbTime, null);
                }
                cursor.moveToPosition(position);
                int mId = cursor.getInt(cursor.getColumnIndex(GlobalName.dbId));
                cursor.close();
                db.delete(GlobalName.dbEvent, "_id=?", new String[] {mId + ""});
            }
            deleteFlag = false;
        }
        if (GlobalName.sortFlag == 0)
            sortDefault();
        else if (GlobalName.sortFlag == 1)
            sortPriority();
        else
            sortTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //菜单响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortBy_priority:
                sortPriority();
                break;
            case R.id.sortBy_time:
                sortTime();
                break;
            case R.id.sortBy_default:
                sortDefault();
                break;
            case R.id.data_analysis:
                Intent intent = new Intent(this, DataAnalysis.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sortDefault();
    }

    //新建事件
    public void btNewEvent(View view) {
        Intent intent = new Intent(this, NewContent.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Event e = (Event) data.getSerializableExtra("event");
        if (e.getComplete() != -1) {
            if (GlobalName.sortFlag == 0) {
                defaAdapter.addEvent(GlobalName.defaultCount, e);
//                sortDefault();
                recyclerView.setAdapter(defaAdapter);
            }
            else if (GlobalName.sortFlag == 1) {
                prioAdapter.addEvent(GlobalName.priorityCount, e);
                sortPriority();
            }
            else if (GlobalName.sortFlag == 2) {
                timeAdapter.addEvent(GlobalName.timeCount, e);
                sortTime();
            }
        }
    }

    //得到数据库数据
    public void sortDefault() {
        List<Event> events = new ArrayList<>();
        MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String time = cursor.getString(cursor.getColumnIndex(GlobalName.dbTime));
            String priority = cursor.getString(cursor.getColumnIndex(GlobalName.dbPriority));
            int complete = cursor.getInt(cursor.getColumnIndex(GlobalName.dbComplete));
            String title = cursor.getString(cursor.getColumnIndex(GlobalName.dbTitle));
            String content = cursor.getString(cursor.getColumnIndex(GlobalName.dbContent));
            events.add(new Event(time, priority, complete, title, content));
        }
        cursor.close();
        //在这里将其置0，否则的话没刷新一次界面它就会自增n次，导致List下标越界
        GlobalName.defaultCount = 0;
        //RecyclerView处理
        GlobalName.sortFlag = 0;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        defaAdapter = new MyAdapter(events);
        recyclerView.setAdapter(defaAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //Item点击事件
        defaAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int pos, int ID) {
                //为什么这里不能用this，而要用全称？在什么位置可以只用this就行？
                Intent intent = new Intent(MainActivity.this, ShowContent.class);
                position = pos;
                intent.putExtra("pos", pos);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int pos) {
                deleteLongTouch(pos);
            }
        });

    }

    //按优先级排序
    public void sortPriority() {
        List<Event> e = new ArrayList<>();
        MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbPriority, null);
        while (cursor.moveToNext()) {
            String time = cursor.getString(cursor.getColumnIndex(GlobalName.dbTime));
            String priority = cursor.getString(cursor.getColumnIndex(GlobalName.dbPriority));
            int complete = cursor.getInt(cursor.getColumnIndex(GlobalName.dbComplete));
            String title = cursor.getString(cursor.getColumnIndex(GlobalName.dbTitle));
            String content = cursor.getString(cursor.getColumnIndex(GlobalName.dbContent));
            e.add(new Event(time, priority, complete, title, content));
        }
        cursor.close();
        //置位，否则溢出
        GlobalName.priorityCount = 0;
        prioAdapter = new MyAdapter(e);
        recyclerView.setAdapter(prioAdapter);
        //更改完适配器后还要重新设置item的点击事件，否则无法响应
        prioAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int pos, int ID) {
                //为什么这里不能用this，而要用全称？在什么位置可以只用this就行？
                Intent intent = new Intent(MainActivity.this, ShowContent.class);
                position = pos;
                intent.putExtra("pos", pos);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int pos) {
                deleteLongTouch( pos);
            }
        });

        GlobalName.sortFlag = 1;//将排序标志为置为1
    }

    //按截止时间排序
    public void sortTime() {
        List<Event> e = new ArrayList<>();
        MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbTime, null);
        while (cursor.moveToNext()) {
            String time = cursor.getString(cursor.getColumnIndex(GlobalName.dbTime));
            String priority = cursor.getString(cursor.getColumnIndex(GlobalName.dbPriority));
            int complete = cursor.getInt(cursor.getColumnIndex(GlobalName.dbComplete));
            String title = cursor.getString(cursor.getColumnIndex(GlobalName.dbTitle));
            String content = cursor.getString(cursor.getColumnIndex(GlobalName.dbContent));
            e.add(new Event(time, priority, complete, title, content));
        }
        cursor.close();
        //置位，否则溢出
        GlobalName.timeCount = 0;
        timeAdapter = new MyAdapter(e);
        recyclerView.setAdapter(timeAdapter);
        //更改完适配器后还要重新设置item的点击事件，否则无法响应
        timeAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int pos, int ID) {
                //为什么这里不能用this，而要用全称？在什么位置可以只用this就行？
                Intent intent = new Intent(MainActivity.this, ShowContent.class);
                position = pos;
                intent.putExtra("pos", pos);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int pos) {
                deleteLongTouch( pos);
            }
        });
        GlobalName.sortFlag = 2;//将排序标志为置为2
        //数据测试
        Event m;
        for (int i = 0; i < e.size(); i++) {
            m = e.get(i);
            Log.d("@HusterYP", String.valueOf(m.getTime()));
        }
    }

    //长按删除操作
    public void deleteLongTouch(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除吗？");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "删除成功 !", Toast.LENGTH_SHORT).show();
                MyDataBase oh = new MyDataBase(MainActivity.this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
                SQLiteDatabase db = oh.getWritableDatabase();
                //分情况，看是哪种排序方式
                Cursor cursor = null;
                if (GlobalName.sortFlag == 0) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null, null);
                }
                else if (GlobalName.sortFlag == 1) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbPriority, null);
                }
                else if (GlobalName.sortFlag == 2) {
                    cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbTime, null);
                }
                cursor.moveToPosition(pos);
                int mId = cursor.getInt(cursor.getColumnIndex(GlobalName.dbId));
                cursor.close();
                db.delete(GlobalName.dbEvent, "_id=?", new String[] {mId + ""});
                if (GlobalName.sortFlag == 0)
                    sortDefault();
                else if (GlobalName.sortFlag == 1)
                    sortPriority();
                else
                    sortTime();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
