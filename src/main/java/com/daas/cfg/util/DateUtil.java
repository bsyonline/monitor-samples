package com.daas.cfg.util;

import org.joda.time.*;

import java.util.Date;

/**
 * Created with IntelliJ DIEA.
 * User: rolex
 * Date: 16-5-1
 * Version: 1.0
 */
public class DateUtil {

    public static String format(Date start, Date end){
        int d = Days.daysBetween(new DateTime(start),new DateTime(end)).getDays();
        int h = Hours.hoursBetween(new DateTime(start),new DateTime(end)).getHours()%24;
        int m = Minutes.minutesBetween(new DateTime(start),new DateTime(end)).getMinutes()%60;
        int s = Seconds.secondsBetween(new DateTime(start),new DateTime(end)).getSeconds()%60;
        String date = "";
        if(d>0){
            date += d+"天";
        }
        if(h>0){
            date += h+"小时";
        }
        if(m>0){
            date += m+"分";
        }
        if (s > 0) {
            date += s+"秒";
        }



        return date;
    }
}
