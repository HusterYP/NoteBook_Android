package com.example.administrator.notebook_1;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewContent extends AppCompatActivity {
    private TextView inputTime;
    private TextView inputPriority;
    private EditText inputTitle;
    private EditText inputContent;
    private CheckBox checkBox;
    private String priority;

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
        }
        else {
            cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
        }
        i.setComponent(cn);
        startActivity(i);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout);
        Init();
    }

    //要在这里捕获返回键，发送一个空的Event事件，否则会报空指针异常
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("event", new Event("", "", -1, "", ""));
            setResult(0, intent);
            finish();
            return true;
        }
        return false;
    }

    private void Init() {
        inputTime = (TextView) findViewById(R.id.input_time);
        inputPriority = (TextView) findViewById(R.id.input_priority);
        inputTitle = (EditText) findViewById(R.id.input_title);
        inputContent = (EditText) findViewById(R.id.input_content);
        checkBox = (CheckBox) findViewById(R.id.input_checkbox);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String date = data.getStringExtra("date");
        if (!date.equals("-1"))
            inputTime.setText(date);
    }

    //按钮点击的事件处理
    public void btOnClick(View view) {
        switch (view.getId()) {
            case R.id.input_save:
                onSave();
                break;
            case R.id.input_cancel:
//                onTest();
                //注意这里也必须要传递一个回去，不然会报空指针异常
                Intent intent = new Intent();
                intent.putExtra("event", new Event("", "", -1, "", ""));
                setResult(0, intent);
                finish();
                break;
            case R.id.input_choseDate:
                Intent date = new Intent(this, Calendar.class);
                startActivityForResult(date, 0);
                break;
            default:
                break;
        }
    }

    //选择优先级
    public void btOnChosePriority(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = {"1", "2", "3", "4", "5"};
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                priority = items[i];
                inputPriority.setText(priority);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //保存事件
    public void onSave() {
        String time = inputTime.getText().toString();
        String priority = inputPriority.getText().toString();
        String title = inputTitle.getText().toString();
        String content = inputContent.getText().toString();
        if (time.isEmpty() && priority.isEmpty() && title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Empty Event ! No Saving", Toast.LENGTH_SHORT).show();
        }
        else {
            int flag = 0;
            if (checkBox.isChecked())
                flag = 1;
            ContentValues values = new ContentValues();
            values.put(GlobalName.dbTime, time);
            values.put(GlobalName.dbPriority, priority);
            values.put(GlobalName.dbComplete, flag);
            values.put(GlobalName.dbTitle, title);
            values.put(GlobalName.dbContent, content);
            MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, 1);
            SQLiteDatabase db = oh.getWritableDatabase();
            long l = db.insert(GlobalName.dbEvent, null, values);
            if (l == -1)
                Toast.makeText(this, "Save Error", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
            //刷新界面
           /*
            //为什么在这里通过传递过来的adapter来调用刷新不行呢？
            Intent intent = getIntent();
            MyAdapter adapter = (MyAdapter)intent.getSerializableExtra("adapter");
            adapter.addEvent(0,new Event(time,priority,flag,title,content));*/
            Intent intent = new Intent();
            intent.putExtra("event", new Event(time, priority, flag, title, content));
            setResult(0, intent);
            //这个必须在上面的数据intent完成之后才能finishing，不然的话会空指针异常
            finish();
        }
    }
}
