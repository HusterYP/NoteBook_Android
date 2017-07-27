package com.example.administrator.notebook_1;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowContent extends AppCompatActivity {
    private int position;
    private TextView cnTime;
    private EditText cnTitle;
    private EditText cnContent;
    private TextView cnPriority;
    private CheckBox cnCheckBox;
    private String priority;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String time = data.getStringExtra("date");
        if (!time.equals("-1"))
            cnTime.setText(time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    //跳转到日历，添加闹钟
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent();
        ComponentName cn = null;
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            cn = new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity");
        } else {
            cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
        }
        i.setComponent(cn);
        startActivity(i);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_content);
        Intent intent = getIntent();
        position = intent.getIntExtra("pos", -1);
        Init();
    }

    //找到ID，init()
    private void Init() {
        cnTime = (TextView) findViewById(R.id.content_time);
        cnTitle = (EditText) findViewById(R.id.content_title);
        cnContent = (EditText) findViewById(R.id.content_content);
        cnPriority = (TextView) findViewById(R.id.content_priority);
        cnCheckBox = (CheckBox) findViewById(R.id.content_checkbox);
        MyDataBase oh = new MyDataBase
                (ShowContent.this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        //如何设置过滤器读取指定的数据？
        Cursor cursor = null;
        if (GlobalName.sortFlag == 0) {
            cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null, null);
        } else if (GlobalName.sortFlag == 1) {
            cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbPriority, null);
        } else if (GlobalName.sortFlag == 2) {
            cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, GlobalName.dbTime, null);
        }
        cursor.moveToPosition(position);

        cnTime.setText(cursor.getString(cursor.getColumnIndex(GlobalName.dbTime)));
        cnTitle.setText(cursor.getString(cursor.getColumnIndex(GlobalName.dbTitle)));
        cnContent.setText(cursor.getString(cursor.getColumnIndex(GlobalName.dbContent)));
        cnPriority.setText(cursor.getInt(cursor.getColumnIndex(GlobalName.dbPriority)) + "");
        if (cursor.getInt(cursor.getColumnIndex(GlobalName.dbComplete)) != 0) {
            cnCheckBox.setChecked(true);
        }
        cursor.close();
    }

    public void shOnClick(View view) {
        switch (view.getId()) {
            case R.id.content_cancel:
                finish();
                break;
            case R.id.content_delete:   //删除操作还有待优化，不能用全局变量做标志位涩—_—！
                GlobalName.deleteFlag = true;
                finish();
                break;
            case R.id.content_save:
                cnOnSave();
                break;
            case R.id.content_choseDate:
                Intent date = new Intent(this, Calendar.class);
                startActivityForResult(date, 0);
                break;
        }
    }

    //更改优先级
    public void shOnChosePriority(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    final String[] items = {"1","2","3","4","5"};
        builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            priority = items[i];
            cnPriority.setText(priority);
            dialogInterface.dismiss();
        }
    });
        builder.show();
}

    //保存改动事件响应
    public void cnOnSave() {
        String time = cnTime.getText().toString();
        String priority = cnPriority.getText().toString();
        String title = cnTitle.getText().toString();
        String content = cnContent.getText().toString();

        if (time.isEmpty() && priority.isEmpty() && title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Empty Event ! No Saving", Toast.LENGTH_SHORT).show();
        } else {
            int flag = 0;
            if (cnCheckBox.isChecked())
                flag = 1;
            //修改对应项
            MyDataBase oh = new MyDataBase
                    (ShowContent.this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
            SQLiteDatabase db = oh.getWritableDatabase();
            Cursor cursor = db.query(GlobalName.dbEvent, null, null, null, null, null, null, null);
            cursor.moveToPosition(position);
            int Id = cursor.getInt(cursor.getColumnIndex(GlobalName.dbId));
            cursor.close();
            //更新数据
            ContentValues cv = new ContentValues();
            cv.put(GlobalName.dbContent, content);
            cv.put(GlobalName.dbTime, time);
            cv.put(GlobalName.dbTitle, title);
            cv.put(GlobalName.dbComplete, flag);
            cv.put(GlobalName.dbPriority, priority);
            db.update(GlobalName.dbEvent, cv, "_id=?", new String[]{Id + ""});
            Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
