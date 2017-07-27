package com.example.administrator.notebook_1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAnalysis extends AppCompatActivity {

    private TextView data_Title;
    private ImageView data_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);
        data_Title = (TextView) findViewById(R.id.data_title);
        data_Image = (ImageView) findViewById(R.id.data_image);
        //不能直接在onCreate方法中绘制初始的默认界面，要在这里监听，因为onCreate中获得
        //的data_Image的尺寸为0
        data_Image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            //当组件加载完成之后会监听到
            public void onGlobalLayout() {
                int[] num2 = new int[5];
                getPriority(num2);
                Piechart(num2, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_analysis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.data_priorityHistogram:
                int[] num1 = new int[5];
                getPriority(num1);
                Histogram(num1, 0);   //flag=0表示优先级柱状图
                break;
            case R.id.data_priorityPiechart:
                int[] num2 = new int[5];
                getPriority(num2);
                Piechart(num2, 0);
                break;
            case R.id.data_completeHistogram:
                int[] count1 = new int[2];
                getComplete(count1);
                Histogram(count1, 1);
                break;
            case R.id.data_completePiechart:
                int[] count2 = new int[2];
                getComplete(count2);
                Piechart(count2, 1);
                break;
        }
        return true;
    }

    //获取数据库数据，得到各优先级数量
    public void getPriority(int[] num) {
        MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, new String[] {GlobalName.dbPriority}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String temp = cursor.getString(cursor.getColumnIndex(GlobalName.dbPriority));
            num[Integer.parseInt(temp) - 1]++;
        }
    }


    //柱状图,flag = 0表示优先级，否则为完成情况
    private void Histogram(int[] num, int flag) {
        if (flag == 0) {
            data_Title.setText("优先级柱状图");
        }
        else {
            data_Title.setText("完成情况柱状图");
        }
        int width = data_Image.getWidth();
        int height = data_Image.getHeight();
        final int titleHeight = 100; //这个是上面的标题占用的宽度

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.RED);
        Canvas canvas = new Canvas(bitmap);
        float startX = 40f;
        float startY = titleHeight + height * 2 / 3;
        canvas.drawLine(startX, startY, (float) (width * 7 / 8.0), startY, paint);  //X轴
        canvas.drawLine(startX, startY, startX, titleHeight, paint);            //Y轴
        //设置字体
        paint.setTextSize(50);
        if (flag == 0)
            canvas.drawText("优先级", (float) (width * 7 / 8.0) - 50, startY + 50, paint);
        else
            canvas.drawText("完成情况", (float) (width * 7 / 8.0) - 50, startY + 50, paint);

        canvas.drawText("份数", startX, titleHeight, paint);
        //画每一个矩形
        int sum = 0;
        for (int i = 0; i < num.length; i++) {
            sum += num[i];
        }
        //每一份的长度
        int averageY = (int) ((startY - titleHeight) / sum);
        int averageX = (int) (width * 7 / 8 - startX) / num.length;
        int skeep;
        if (flag == 0)
            skeep = 20;    //间隔
        else
            skeep = 50;

        paint.setColor(Color.BLUE);
        canvas.translate(startX, startY);   //移动坐标原点
        for (int i = 0; i < num.length; i++) {
            canvas.drawRect(averageX * i + skeep, -averageY * num[i], averageX * (i + 1) - skeep, 0, paint);
            canvas.drawText("" + num[i], (float) (averageX * (i + 1 / 2.0)), -averageY * num[i] - 30, paint);
            if (flag == 0)
                canvas.drawText("" + (i + 1), (float) (averageX * (i + 1 / 2.0)), 60, paint);
            else {
                if (i == 0) {
                    canvas.drawText("完成", (float) (averageX * (i + 1 / 3.0)), 60, paint);
                }
                else
                    canvas.drawText("未完成", (float) (averageX * (i + 1 / 3.0)), 60, paint);
            }
        }
        data_Image.setImageBitmap(bitmap);
    }

    //获取数据库数据，完成情况数量
    public void getComplete(int[] num) {
        MyDataBase oh = new MyDataBase(this, GlobalName.dbDataBaseName, null, GlobalName.dbVersion);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor = db.query(GlobalName.dbEvent, new String[] {GlobalName.dbComplete}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int temp = cursor.getInt(cursor.getColumnIndex(GlobalName.dbComplete));
            if (temp == 0)
                num[0]++;
            else
                num[1]++;
        }
    }

    //绘制饼状图 ,flag = 0表示的是优先级,否则为完成情况
    private void Piechart(int[] num, int flag) {
        int width = data_Image.getWidth();
        int height = data_Image.getHeight();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        final int sideLength = 800;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.RED);
        Canvas canvas = new Canvas(bitmap);
        if (flag == 0)
            data_Title.setText("优先级饼状图");
        else
            data_Title.setText("完成情况饼状图");

        float sum = 0f;
        for (int i = 0; i < num.length; i++)
            sum += num[i];

        canvas.translate(screenWidth / 2, screenHeight / 2); //移动原点
        RectF rectF = new RectF(-sideLength / 2, -600, sideLength / 2, 200);
        float startAngle = 0f;  //起始角度位置
        float endAngle;     //终止角度位置
        float textX = sideLength / 2 - 50;
        float textY = -700f;
        int[] colors = {Color.GRAY, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.GREEN};
        for (int i = 0; i < num.length; i++) {
            endAngle = (num[i] / sum) * 360;
            paint.setColor(colors[i]);
            canvas.drawArc(rectF, startAngle, endAngle, true, paint);
            startAngle += endAngle;
            paint.setTextSize(40);
            canvas.drawRect(textX - 40, textY + 40 * i, textX + 40, textY + 40 * (i + 1), paint);
            paint.setColor(Color.BLACK);
            if (flag == 0)
                canvas.drawText((i + 1) + "", textX + 60, textY + 40 * (i + 1) - 5, paint);
            else {
                if (i == 0)
                    canvas.drawText("未完成", textX + 60, textY + 40 * (i + 1) - 5, paint);
                else
                    canvas.drawText("完成", textX + 60, textY + 40 * (i + 1) - 5, paint);
            }
        }

        data_Image.setImageBitmap(bitmap);
    }

}
