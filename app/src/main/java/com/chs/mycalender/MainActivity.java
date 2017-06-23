package com.chs.mycalender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MyCalendar mMyCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyCalendar = (MyCalendar) findViewById(R.id.calendar);
        mMyCalendar.setOnCellClickListener(new MyCalendar.OnCellClickListener() {
            @Override
            public void onCellClick(int position, Cell cell) {
                Toast.makeText(getApplicationContext(), cell.getYear() + "年" + cell.getMonth() + "月" + cell.getDay() + "日", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void change(View view) {
        mMyCalendar.setType(mMyCalendar.getType() == 1 ? 2 : 1);
    }

    public void jump(View view) {
        mMyCalendar.jumpToToday();
    }
}
