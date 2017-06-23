package com.chs.mycalender;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者：chs on 2017-06-20 09:59
 * 邮箱：657083984@qq.com
 */

public class MyCalendar extends LinearLayout {
    private TextView tv_last;
    private TextView tv_current_date;
    private TextView tv_next;
    private ViewPager mViewPager;
    public static Calendar mCalendar = Calendar.getInstance();//全局的mCalendar 改变的时候所有地方都改变
    private Context mContext;
    private List<Cell> cells = new ArrayList<>();
    private ArrayList<RecyclerView> calendars;
    private CalendarAdapter mCalendarAdapter;
    private int currentPosition;//viewpager 当前的position
    public static int styleType = 1;//1是月类型的 2是星期类型的
    private OnCellClickListener mOnCellClickListener;

    public void setOnCellClickListener(OnCellClickListener onCellClickListener) {
        mOnCellClickListener = onCellClickListener;
    }

    interface OnCellClickListener{
        void onCellClick(int position,Cell cell);
    }
    public MyCalendar(Context context) {
        super(context);
        init(context);
    }

    public MyCalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
         mContext = context;
         initView(context);
         initEvent();
         renderView();
    }

    public void setType(int type) {
        this.styleType = type;
        if(type==1){
            tv_last.setText("上个月");
            tv_next.setText("下个月");
        }else {
            tv_last.setText("上星期");
            tv_next.setText("下星期");
        }
        renderView();
        if(calendars.get(currentPosition % calendars.size()) != null){
            calendars.get(currentPosition % calendars.size()).getAdapter().notifyDataSetChanged();
        }
    }

    public int getType() {
        return styleType;
    }
    public void jumpToToday(){
        mCalendar = Calendar.getInstance();
        renderView();
        if(calendars.get(currentPosition % calendars.size()) != null){
            calendars.get(currentPosition % calendars.size()).getAdapter().notifyDataSetChanged();
        }
    }
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_view,null);
        tv_last = (TextView) view.findViewById(R.id.tv_last);
        tv_current_date = (TextView) view.findViewById(R.id.tv_current_date);
        tv_next = (TextView) view.findViewById(R.id.tv_next);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mCalendarAdapter = new CalendarAdapter();
        mViewPager.setAdapter(mCalendarAdapter);
        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        currentPosition = Integer.MAX_VALUE / 2;
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position<currentPosition){//左滑
                    if(styleType==1){
                        mCalendar.add(Calendar.MONTH, -1);
                    }else {
                        mCalendar.add(Calendar.DAY_OF_MONTH, -7);
                    }
                }else {//右滑
                    if(styleType==1){
                        mCalendar.add(Calendar.MONTH, 1);
                    }else {
                        mCalendar.add(Calendar.DAY_OF_MONTH, 7);
                    }
                }
                renderView();
                currentPosition = position;
                if(calendars.get(position % calendars.size()) != null){
                    calendars.get(position % calendars.size()).getAdapter().notifyDataSetChanged();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        addView(view);
    }
    private void initEvent() {
        tv_last.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(currentPosition-1);
            }
        });
        tv_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(currentPosition+1);
            }
        });
    }
    //渲染recycleview
    private void renderView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM");
        tv_current_date.setText(dateFormat.format(mCalendar.getTime()));

        Calendar calendar = (Calendar) mCalendar.clone();

        int maxCount;
        if(styleType==1){
            //月类型的一个日历最多6行7列
            maxCount = 6*7;
            //置为当月的第一天
            calendar.set(Calendar.DAY_OF_MONTH,1);
        }else {
            //星期类型的就7个
            maxCount = 7;
        }
        //前面几天 不属于这个月的天数
        int previousDays = calendar.get(Calendar.DAY_OF_WEEK)-1;
        calendar.add(Calendar.DAY_OF_MONTH,-previousDays);
        cells.clear();
        while (cells.size()<maxCount){
            cells.add(new Cell(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        calendars = mCalendarAdapter.getCalendars();
    }
    private class CalendarAdapter extends PagerAdapter {
        private ArrayList<RecyclerView> calendars = new ArrayList<>();
        CalendarAdapter() {
            calendars.clear();
            //new 3个RecyclerView实现viewpager的无限循环
            for (int i = 0; i < 3; i++) {
                RecyclerView rv = new RecyclerView(mContext);
                GridLayoutManager manager = new GridLayoutManager(mContext,7);
                rv.setLayoutManager(manager);
                rv.setHasFixedSize(true);
                calendars.add(rv);
            }
        }

        ArrayList<RecyclerView> getCalendars(){
            return calendars;
        }
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (container.getChildCount() == calendars.size()) {
                container.removeView(calendars.get(position % 3));
            }
            RecyclerView recyclerView = calendars.get(position%calendars.size());
            MyAdapter myAdapter = new MyAdapter(mContext, cells);
            myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position) {
                 if(mOnCellClickListener!=null){
                     mOnCellClickListener.onCellClick(position,cells.get(position));
                 }
                }
            });
            recyclerView.setAdapter(myAdapter);
            container.addView(recyclerView, 0);
            return recyclerView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }
    }
}
