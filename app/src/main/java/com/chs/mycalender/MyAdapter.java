package com.chs.mycalender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chs.mycalender.util.ScrollUtil;

import java.util.Calendar;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    private List<Cell> mCells;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private Paint mPaint;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClicked(int position);
    }

    MyAdapter(Context context, List<Cell> cells) {
        mCells = cells;
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(ContextCompat.getColor(context,R.color.colorAccent));
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.calendar_content, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final Cell cell = mCells.get(position);
        holder.tv_content.setText(String.valueOf(cell.getDay()));
        if(cell.isClicked()){
            holder.tv_content.setBackground(new BitmapDrawable(null,createBitmap()));
        }else {
            holder.tv_content.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white));
        }
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        int month = MyCalendar.mCalendar.get(Calendar.MONTH) + 1;
        //月份不一样的变灰
        if (MyCalendar.styleType == 1) {
            if (cell.getMonth() != month) {
                holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
            } else {
                holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        } else {
            holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        //年月日都相等才是同一天圈红
        if (currentDay == cell.getDay() && currentMonth == cell.getMonth() && currentYear == cell.getYear()) {
            ScrollUtil.currentPos = position;
            holder.tv_content.setDrawCircle(true);
            if(cell.isClicked()){
                holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }else {
                holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        }else {
            holder.tv_content.setDrawCircle(false);
        }

        holder.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mCells.size(); i++) {
                    mCells.get(i).setClicked(i==position);
                }
                notifyDataSetChanged();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCells.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        private MyTextView tv_content;
        public MyHolder(View itemView) {
            super(itemView);
            tv_content = (MyTextView) itemView.findViewById(R.id.tv_content);
        }
    }
    private Bitmap createBitmap(){
        int width = dip2px(mContext,30);
        //创建一个画布，宽为width,高为height
        Bitmap tmpBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        //创建一个工具，这个工具的操作，都是在这个画布上进行的
        Canvas canvas = new Canvas(tmpBitmap);
        canvas.translate(width/2,width/2);
        canvas.drawCircle(0,0,width/2,mPaint);
        return tmpBitmap;
    }
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}