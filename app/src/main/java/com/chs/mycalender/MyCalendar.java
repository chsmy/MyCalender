package com.chs.mycalender;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chs.mycalender.behavior.RecycleViewBehavior;

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
    private ViewPager mViewPagerMonth;
    private ViewPager mViewPagerWeek;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    public static Calendar mCalendar = Calendar.getInstance();//全局的mCalendar 改变的时候所有地方都改变
    private Context mContext;
    private List<Cell> MonthCells = new ArrayList<>();
    private List<Cell> Weekcells = new ArrayList<>();
    private ArrayList<RecyclerView> mMonthCalendars;
    private ArrayList<RecyclerView> mWeekCalendars;
    private CalendarAdapter mMonthCalendarAdapter;
    private CalendarAdapter mWeekCalendarAdapter;
    private int mMonthCurrentPosition;//viewpager 当前的position
    private int mWeekCurrentPosition;//viewpager 当前的position
    public static int styleType = 1;//1是月类型的 2是星期类型的
    private OnCellClickListener mOnCellClickListener;

    private List<String> recycleData = new ArrayList<>();

    public void setOnCellClickListener(OnCellClickListener onCellClickListener) {
        mOnCellClickListener = onCellClickListener;
    }

    interface OnCellClickListener {
        void onCellClick(int position, Cell cell);
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
        initRecycleData();
        initView(context);
        initEvent();
        renderView();
        refresh();
//        setType(1);
    }

    private void initRecycleData() {
        for (int i = 0; i < 20; i++) {
            recycleData.add("items-----------" + i);
        }
    }

    public void setType(int type) {
        this.styleType = type;
        if (type == 1) {
            tv_last.setText("上个月");
            tv_next.setText("下个月");
        } else {
            tv_last.setText("上星期");
            tv_next.setText("下星期");
        }
        RecycleViewBehavior behavior = (RecycleViewBehavior) ((CoordinatorLayout.LayoutParams)mRecyclerView.getLayoutParams()).getBehavior();
        if (behavior != null){
            if(mViewPagerWeek.getVisibility() == GONE){
                mViewPagerWeek.setVisibility(VISIBLE);
            }else {
                mViewPagerWeek.setVisibility(GONE);
            }
            behavior.onLayoutChild(mCoordinatorLayout,mRecyclerView,200);
        }

//        renderView();

    }

    public int getType() {
        return styleType;
    }

    public void jumpToToday() {
        mCalendar = Calendar.getInstance();
        renderView();
        if (mMonthCalendars.get(mMonthCurrentPosition % mMonthCalendars.size()) != null) {
            mMonthCalendars.get(mMonthCurrentPosition % mMonthCalendars.size()).getAdapter().notifyDataSetChanged();
        }
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_view, null);
        tv_last = (TextView) view.findViewById(R.id.tv_last);
        tv_current_date = (TextView) view.findViewById(R.id.tv_current_date);
        tv_next = (TextView) view.findViewById(R.id.tv_next);
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.content);
        //月份
        mViewPagerMonth = (ViewPager) view.findViewById(R.id.viewpager_month);
        mMonthCalendarAdapter = new CalendarAdapter(1);
        mViewPagerMonth.setAdapter(mMonthCalendarAdapter);
        mViewPagerMonth.setCurrentItem(Integer.MAX_VALUE / 2);
        mMonthCurrentPosition = Integer.MAX_VALUE / 2;
//        mViewPagerMonth.setPageTransformer(false, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View page, float position) {
//                position = (float) Math.sqrt(1 - Math.abs(position));
//                page.setAlpha(position);
//            }
//        });
        mViewPagerMonth.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < mMonthCurrentPosition) {//左滑
                    mCalendar.add(Calendar.MONTH, -1);
                } else {//右滑
                    mCalendar.add(Calendar.MONTH, 1);
                }
                renderView();
                mMonthCurrentPosition = position;
                if (mMonthCalendars.get(position % mMonthCalendars.size()) != null) {
                    mMonthCalendars.get(position % mMonthCalendars.size()).getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //星期
        mViewPagerWeek = (ViewPager) view.findViewById(R.id.viewpager_week);
        mWeekCalendarAdapter = new CalendarAdapter(2);
        mViewPagerWeek.setAdapter(mWeekCalendarAdapter);
        mViewPagerWeek.setCurrentItem(Integer.MAX_VALUE / 2);
        mWeekCurrentPosition = Integer.MAX_VALUE / 2;
//        mViewPagerWeek.setPageTransformer(false, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View page, float position) {
//                position = (float) Math.sqrt(1 - Math.abs(position));
//                page.setAlpha(position);
//            }
//        });
        mViewPagerWeek.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < mWeekCurrentPosition) {//左滑
                    mCalendar.add(Calendar.DAY_OF_MONTH, -7);
                } else {//右滑
                    mCalendar.add(Calendar.DAY_OF_MONTH, 7);
                }
                renderView();
                mWeekCurrentPosition = position;
                if (mWeekCalendars.get(position % mWeekCalendars.size()) != null) {
                    mWeekCalendars.get(position % mWeekCalendars.size()).getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        RecycleListAdapter adapter = new RecycleListAdapter(mContext, recycleData);
        mRecyclerView.setAdapter(adapter);
        addView(view);
    }

    private void initEvent() {
        tv_last.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerMonth.setCurrentItem(mMonthCurrentPosition - 1);
            }
        });
        tv_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerMonth.setCurrentItem(mMonthCurrentPosition + 1);
            }
        });
    }
    private void refresh(){
        if (mMonthCalendars.get(mMonthCurrentPosition % mMonthCalendars.size()) != null) {
            RecyclerView recyclerView = mMonthCalendars.get(mMonthCurrentPosition % mMonthCalendars.size());
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        if (mWeekCalendars.get(mWeekCurrentPosition % mWeekCalendars.size()) != null) {
            RecyclerView recyclerView = mWeekCalendars.get(mWeekCurrentPosition % mWeekCalendars.size());
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    //渲染recycleview
    private void renderView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM");
        tv_current_date.setText(dateFormat.format(mCalendar.getTime()));
        /*-------------月份----------*/
        Calendar monthCalendar = (Calendar) mCalendar.clone();
        //月类型的一个日历最多6行7列
        int maxMonthCount = 6 * 7;
        //置为当月的第一天
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        //前面几天 不属于这个月的天数
        int previousDays = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -previousDays);
        MonthCells.clear();
        Log.i("date",monthCalendar.get(Calendar.YEAR)+"年"+(monthCalendar.get(Calendar.MONTH) + 1)+"月"+ monthCalendar.get(Calendar.DAY_OF_MONTH)+"日");
        while (MonthCells.size() < maxMonthCount) {
            MonthCells.add(new Cell(monthCalendar.get(Calendar.DAY_OF_MONTH), monthCalendar.get(Calendar.MONTH) + 1, monthCalendar.get(Calendar.YEAR)));
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        mMonthCalendars = mMonthCalendarAdapter.getCalendars();

        /*-------------星期----------*/
        Calendar weekCalendar = (Calendar) mCalendar.clone();
        //星期类型的就7个
        int maxWeekCount = 7;
        //前面几天 不属于这个月的天数
        int previousDays_ = weekCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        weekCalendar.add(Calendar.DAY_OF_MONTH, -previousDays_);
        Weekcells.clear();
        while (Weekcells.size() < maxWeekCount) {
            Weekcells.add(new Cell(weekCalendar.get(Calendar.DAY_OF_MONTH), weekCalendar.get(Calendar.MONTH) + 1, weekCalendar.get(Calendar.YEAR)));
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        mWeekCalendars = mWeekCalendarAdapter.getCalendars();
    }

    public class CalendarAdapter extends PagerAdapter {
        private ArrayList<RecyclerView> calendars = new ArrayList<>();
        private int mFrom;

        CalendarAdapter(int from) {
            mFrom = from;
            calendars.clear();
            //new 3个RecyclerView实现viewpager的无限循环
            for (int i = 0; i < 3; i++) {
                RecyclerView rv = new RecyclerView(mContext);
                GridLayoutManager manager = new GridLayoutManager(mContext, 7);
                rv.setLayoutManager(manager);
                rv.setHasFixedSize(true);
                calendars.add(rv);
            }
        }

        public void changeStyle(int type) {
            setType(type);
        }

        ArrayList<RecyclerView> getCalendars() {
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
            RecyclerView recyclerView = calendars.get(position % calendars.size());
            MyAdapter myAdapter = null;
            if (mFrom == 1) {
                myAdapter = new MyAdapter(mContext, MonthCells,1);
            } else {
                myAdapter = new MyAdapter(mContext, Weekcells,2);
            }
            myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position) {
                    if (mOnCellClickListener != null) {
                        mOnCellClickListener.onCellClick(position, MonthCells.get(position));
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
