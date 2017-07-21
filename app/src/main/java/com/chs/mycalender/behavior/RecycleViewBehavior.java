package com.chs.mycalender.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chs.mycalender.util.DensityUtil;

/**
 * 作者：chs on 2017-06-27 17:54
 * 邮箱：657083984@qq.com
 */

public class RecycleViewBehavior extends CoordinatorLayout.Behavior<RecyclerView> {
    private int minHeight;
    private int maxHeight;
    private Context mContext;
    public RecycleViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        minHeight = DensityUtil.dip2px(context,30);
        maxHeight = DensityUtil.dip2px(context,180);
    }

    //view需要根据监听CoordinatorLayout中的子view的滚动行为来改变自己的状态，现在我们就需要重写下面的方法了：
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        Log.i("Ynum",dy+"----"+child.getTop()+"---"+coordinatorLayout.getScrollY()+"minHeight:"+minHeight+"maxHeight:"+maxHeight);
        //dy大于0是上滑 dy小于0是下滑
//        boolean stop = dy>0&&
        // 在这个方法里面只处理向上滑动
        if(dy < 0){
            return;
        }

        if(dy>0&&child.getTop()>minHeight){
            scroll(child,dy,minHeight,maxHeight);
        }else {
            coordinatorLayout.getChildAt(1).setVisibility(View.VISIBLE);
            coordinatorLayout.getChildAt(0).setVisibility(View.GONE);
        }

    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        // 在这个方法里只处理向下滑动
        if(dyUnconsumed >0){
            return;
        }
        if(dyUnconsumed<0&&child.getTop()<maxHeight){
            scroll(child,dyUnconsumed,minHeight,maxHeight);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
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
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
//        ViewPager viewPager = (ViewPager) parent.getChildAt(0);
//        int initOffset = viewPager.getBottom();
        if(parent.getChildAt(1).getVisibility() == View.GONE){
            maxHeight= DensityUtil.dip2px(mContext,180);
        }else {
            maxHeight= DensityUtil.dip2px(mContext,30);
        }
        ViewCompat.offsetTopAndBottom(child,maxHeight);
        return true;
    }
}
