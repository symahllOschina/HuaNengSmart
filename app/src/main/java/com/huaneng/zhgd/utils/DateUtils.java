package com.huaneng.zhgd.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/11.
 */

public class DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 判断time1是否为最新的时间
     * @param newTime
     * @param oldTime
     * @return
     */
    public static boolean isNewest(String newTime, String oldTime) {
        if (TextUtils.isEmpty(newTime)) {
            return false;
        }
        if (TextUtils.isEmpty(oldTime)) {
            return true;
        }
        Date oldDate = DateUtils.toDate(oldTime);
        if (oldDate == null) {
            return true;
        }
        Date newDate = DateUtils.toDate(newTime);
        if (newDate == null) {
            return false;
        }
        return newDate.compareTo(oldDate) > 0;
    }

    /**
     * 超过一个小时，认为过期
     * @param newTime
     * @return
     */
    public static boolean isExpired(String newTime) {
        Date newDate = DateUtils.toDate(newTime);
        if (newDate == null) {
            return true;
        }
        long diff = Calendar.getInstance().getTime().getTime() - newDate.getTime();
        long min = diff / (1000 * 60);
        return min > 60;
    }

    public static String millisecondToDate(String time) {
        return millisecondToTime(time, DATE_PATTERN);
    }

    public static String millisecondToTime(String time) {
        return millisecondToTime(time, TIME_PATTERN);
    }

    public static String millisecondToTime(String time, String pattern) {
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        try {
            Date date = new Date(Long.valueOf(time)*1000);
            DateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date toDate(String time) {
        return toDate(time, TIME_PATTERN);
    }

    public static Date toDate(String time, String pattern) {
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        try {
            DateFormat format = new SimpleDateFormat(pattern);
            return format.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
