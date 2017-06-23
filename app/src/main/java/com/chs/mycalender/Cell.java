package com.chs.mycalender;

/**
 * 作者：chs on 2017-06-20 11:09
 * 邮箱：657083984@qq.com
 */

public class Cell {
    private int day;
    private int month;
    private int year;
    private boolean isClicked;
    public int getDay() {
        return day;
    }

    public Cell(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
