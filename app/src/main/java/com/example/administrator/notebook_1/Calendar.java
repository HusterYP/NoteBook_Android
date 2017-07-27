package com.example.administrator.notebook_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Calendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
    }

    public void calClick(View view) {
        switch (view.getId()) {
            case R.id.cal_cancle:
                //即使是取消，也要返回一个值，不然的话会空指针异常，因为有两个活动可以启动它
                Intent intent1 = new Intent();
                intent1.putExtra("date", "-1");
                setResult(0, intent1);
                finish();
                break;
            case R.id.cal_sure:
                CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
                long time = calendarView.getDate();
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                Date d1 = new Date(time);
                String t1 = format.format(d1);
                Intent intent = new Intent();
                intent.putExtra("date", t1);
                setResult(0, intent);
                finish();
                break;
        }
    }
}
