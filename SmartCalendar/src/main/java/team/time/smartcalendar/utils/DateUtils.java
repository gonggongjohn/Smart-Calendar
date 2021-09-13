package team.time.smartcalendar.utils;

import android.annotation.SuppressLint;
import com.haibin.calendarview.Calendar;
import team.time.smartcalendar.dataBeans.CalendarItem;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateUtils {
    public static long A_DAY_MILLISECOND=86400000L;
    public static long A_MIN_MILLISECOND=60000L;

    public static String getFormatTime(Date date){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("yyyy年 MM月dd日 HH:mm");
        return format.format(date);
    }

    public static String getOnlyDayTime(long time){
        return getOnlyDayTime(new Date(time));
    }

    public static String getOnlyDayTime(Date date){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("MM.dd");
        return format.format(date);
    }

    public static String getDayTime(long time){
        return getDayTime(new Date(time));
    }

    public static String getDayTime(Date date){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("yyyy年 MM月dd日");
        return format.format(date);
    }

    public static String getClockTime(Date date){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String getClockTime(long time){
        Date date=new Date(time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String getStartClockTime(long time,long dayStart){
        return time<=dayStart?"00:00":getClockTime(time);
    }

    public static String getEndClockTime(long time,long dayStart){
        long dayEnd=dayStart+A_DAY_MILLISECOND;
        return time>=dayEnd?"24:00":getClockTime(time);
    }

    public static String getWeekDay(long time){
        return getWeekDay(new Date(time));
    }

    public static String getWeekDay(Date date){
        switch (date.getDay()){
            case 0:
                return "周日";
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            default:
                return "未知";
        }
    }

    public static long getTimeStamp(Calendar calendar){
        return new Date(
                calendar.getYear()-1900,
                calendar.getMonth()-1,
                calendar.getDay()
        ).getTime();
    }

    public static long getTimeStamp(int year,int month,int day){
        return new Date(
                year-1900,
                month-1,
                day
        ).getTime();
    }

    public static Date getMinDate(Date date){
        return new Date(
                date.getYear(),
                date.getMonth(),
                date.getDate(),
                date.getHours(),
                date.getMinutes()
        );
    }

    public static Date getDayDate(Date date){
        return new Date(
                date.getYear(),
                date.getMonth(),
                date.getDate()
        );
    }

    public static boolean includeItem(CalendarItem item,long dayStart){
        long dayEnd=dayStart + A_DAY_MILLISECOND;
        if(item.endTime==dayStart){
            return item.startTime == dayStart;
        }else {
            return item.endTime > dayStart && item.startTime < dayEnd;
        }
    }

    public static void sortItemList(List<CalendarItem>items){
        Collections.sort(items, (o1, o2) -> {
            if(o1.startTime<o2.startTime){
                return -1;
            }else if(o1.startTime>o2.startTime){
                return 1;
            }else {
                return 0;
            }
        });
    }

    public static boolean includeItem(CalendarItem item,long monthStart,long monthEnd){
        if(item.endTime==monthStart){
            return item.startTime == monthStart;
        }else {
            return item.endTime > monthStart && item.startTime < monthEnd;
        }
    }

    public static CalendarItem findItemById(String uuid,List<CalendarItem>items){
        for(CalendarItem item:items){
            if(item.uuid.equals(uuid)){
                return item;
            }
        }
        return null;
    }

    public static int getDayOfMonth(int year,int month){
        java.util.Calendar calendar= java.util.Calendar.getInstance();
        calendar.set(year,month-1,1);
        return calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
    }

    public static Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);
        calendar.setScheme(text);
        return calendar;
    }

    public static void setMonthScheme(Map<String,Calendar>map,List<CalendarItem>items,int year,int month){
        map.clear();
        int days=DateUtils.getDayOfMonth(year, month);
        long start=DateUtils.getTimeStamp(year,month,1);
        long end=start + days * DateUtils.A_DAY_MILLISECOND;
        for(CalendarItem item:items){
            if(includeItem(item,start,end)){
                int startDay=item.startTime<=start?
                        1 :
                        (int)Math.floor((double)(item.startTime-start)/DateUtils.A_DAY_MILLISECOND+1);
                int endDay=item.endTime>=end?
                        days :
                        (int)Math.ceil((double)(item.endTime-start)/DateUtils.A_DAY_MILLISECOND);
                if(endDay<startDay){
                    endDay++;
                }
                for (int i = startDay; i <= endDay; i++) {
                    map.put(
                            DateUtils.getSchemeCalendar(year, month, i, ColorUtils.DoDodgerBlue, "")
                                    .toString(),
                            DateUtils.getSchemeCalendar(year, month, i, ColorUtils.DoDodgerBlue, "")
                    );
                }
            }
        }
    }
}
