package com.chs.mycalender.util;

import android.content.Context;

/**
 * 作者：chs on 2017-06-30 10:17
 * 邮箱：657083984@qq.com
 */

public class ScrollUtil {
    /**
     * 当前位置
     */
    public static int currentPos ;
    public static int getCurrentHeight(Context context){
        int curPos = currentPos+1;
        int every = DensityUtil.dip2px(context,40);
        int row;
        if(curPos%7!=0){
            row = (int) Math.floor(curPos / 7);
        }else {
            row = (int) Math.floor(curPos / 7)-1;
        }
//        Log.i("offSet",curPos+"***"+every+"---"+row);
        return row * every;
    }
}
