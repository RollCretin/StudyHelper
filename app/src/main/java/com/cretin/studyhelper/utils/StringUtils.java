package com.cretin.studyhelper.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cretin on 2018/3/15.
 */

public class StringUtils {

    /**
     * 哦按段两个日期相差几天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if ( year1 != year2 ) // 同一年
        {
            int timeDistance = 0;
            for ( int i = year1; i < year2; i++ ) {
                if ( i % 4 == 0 && i % 100 != 0 || i % 400 == 0 ) // 闰年
                {
                    timeDistance += 366;
                } else // 不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else // 不同年
        {
            return day2 - day1;
        }
    }

    /**
     * 时间格式化
     *
     * @param time
     * @param format
     * @return
     */
    public static String formatTimeStr(String time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date currentTime;
        try {
            currentTime = formatter.parse(time);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return time;
        }
        // 获取现在时间
        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int date = cc.get(Calendar.DATE);
        // 获取今天00：00：00 的时间
        cc.set(Calendar.DATE, date);
        cc.set(Calendar.YEAR, year);
        cc.set(Calendar.MONTH, month);
        cc.set(Calendar.HOUR_OF_DAY, 0);
        cc.set(Calendar.MINUTE, 0);
        cc.set(Calendar.SECOND, 0);
        // 今天00:00:00的时间
        long today = cc.getTimeInMillis();
        // 计算时间
        String timeResult = "";
        // 已经是昨天或者之前的时间了
        if ( today < currentTime.getTime() ) {
            //今天
            timeResult = "今天 " + time.split(" ")[1];
        } else if ( today - currentTime.getTime() >= 24 * 60 * 60 * 1000 * 2 ) {
            // 前天 之前
            return time;
        } else if ( today - currentTime.getTime() >= 24 * 60 * 60 * 1000 ) {
            // 前天
            timeResult = "前天 " + time.split(" ")[1];
        } else {
            // 昨天
            timeResult = "昨天 " + time.split(" ")[1];
        }
        return timeResult;
    }
}
