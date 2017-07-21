package com.chs.mycalender.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chs.mycalender.MyCalendar;
import com.chs.mycalender.util.DensityUtil;
import com.chs.mycalender.util.ScrollUtil;

/**
 * 作者：chs on 2017-06-27 17:38
 * 邮箱：657083984@qq.com
 */

public class ViewPagerBehavior extends CoordinatorLayout.Behavior<ViewPager> {
    private int mTop;
    private Context mContext;
    private int minHeight;
    public ViewPagerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        minHeight = DensityUtil.dip2px(context,30);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewPager child, View dependency) {
        //告知监听的dependency是RecyclerView
        return dependency instanceof RecyclerView;
    }

    //当 dependency(RecyclerView)变化的时候，可以对child(ViewPager)进行操作
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewPager child, View dependency) {
        MyCalendar.CalendarAdapter calendarAdapter = (MyCalendar.CalendarAdapter) child.getAdapter();
        int maxHeight = ScrollUtil.getCurrentHeight(mContext);
        Log.i("offSet", child.getTop() + "---" + maxHeight + "---*" + dependency.getTop());
        if (Math.abs(child.getTop()) >= maxHeight && dependency.getTop() <= 90) {
//            if (MyCalendar.styleType == 1) {
//                calendarAdapter.changeStyle(2);
//            }
        }
        if (child.getTop() >= 0 && dependency.getTop() > mTop) {
//            if (MyCalendar.styleType == 2) {
//                calendarAdapter.changeStyle(1);
//            }
        }
        int dy = mTop - dependency.getTop();
        Log.i("offset","dy:"+dy);
        //上滑
        if (mTop > dependency.getTop() && Math.abs(child.getTop()) <= maxHeight) {
//            ViewCompat.offsetTopAndBottom(child, -(mTop - dependency.getTop()));
            scroll(child,dy,-maxHeight,maxHeight);
        }
        //下滑
        if (mTop < dependency.getTop() && child.getTop() < 0) {
            scroll(child,dy,-maxHeight,maxHeight);
//            ViewCompat.offsetTopAndBottom(child, -(mTop - dependency.getTop()));
        }
        mTop = dependency.getTop();
        return true;
    }
    private void scroll(View child,int dy, int minOffset, int maxOffset){
        final int initOffset = child.getTop();
        int delta = offsetMaxAndMin(initOffset - dy, minOffset, maxOffset) - initOffset;
        ViewCompat.offsetTopAndBottom(child,delta);
    }
    //检查边界
    private static int offsetMaxAndMin(int value, int min, int max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }
}
