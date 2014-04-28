package com.whatime.controller.service;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.holiday.HolidayCache;

public class AlarmUtil
{
    // Shared with DigitalClock
    public final static String M24 = "kk:mm";
    
    private final static String DM24 = "E k:mm";
    
    private final static String DM12 = "E h:mm aa";
    
    private final static String M12 = "h:mm aa";
    
    /**
     * @return true if clock is set to 24-hour mode
     */
    public static boolean get24HourMode(final Context context)
    {
        return android.text.format.DateFormat.is24HourFormat(context);
    }
    public static String formatDayAndTime(final Context context, Calendar c)
    {
        String format = get24HourMode(context) ? DM24 : DM12;
        return (c == null) ? "" : (String)DateFormat.format(format, c);
    }
    
    /* used by AlarmAlert */
    public static String formatTime(final Context context, Calendar c)
    {
        String format = get24HourMode(context) ? M24 : M12;
        return (c == null) ? "" : (String)DateFormat.format(format, c);
    }
    
    /**
     * 获取下一次起床的时间
     * @param ctxx
     * @param time
     * @return
     */
    public static Calendar getGetupTime(long time)
    {
        Context ctxx = MyApp.getInstance().getApplicationContext();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        judgeCalendar(ctxx,cal);
        //如果今天不需要响铃了
        if(cal.getTimeInMillis()<System.currentTimeMillis())
        {
            cal.add(Calendar.DATE, 1);
        }
        judgeCalendar(ctxx,cal);
        
        return cal;
        
    }

    private static void judgeCalendar(Context ctxx,Calendar cal)
    {
        StringBuilder date = new StringBuilder();
        date.append(cal.get(Calendar.YEAR)).append("-").append(cal.get(Calendar.MONTH)).append("-").append(cal.get(Calendar.DAY_OF_MONTH));
        if(HolidayCache.isHolidayForRest(ctxx,date.toString()))
        {
            cal.add(Calendar.DATE, 1);
            judgeCalendar(ctxx,cal);
        }
        
    }
    
    public static Calendar getChidTime(Alarm alarm)
    {
        return null;
    }
    /**
     * format "Alarm set for 2 days 7 hours and 53 minutes from
     * now"
     */
    public static String formatToast(Context context, long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? context.getString(R.string.day) :
                context.getString(R.string.days, Long.toString(days));

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? context.getString(R.string.minute) :
                context.getString(R.string.minutes, Long.toString(minutes));

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? context.getString(R.string.hour) :
                context.getString(R.string.hours, Long.toString(hours));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) |
                    (dispHour ? 2 : 0) |
                    (dispMinute ? 4 : 0);

        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }
    
    public static long getDrinkTime(int hour,int minute)
    {
        Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.HOUR_OF_DAY, hour);
        c1.set(Calendar.MINUTE, minute);
        if(c1.getTimeInMillis()<System.currentTimeMillis())
        {
            c1.add(Calendar.DATE, 1);
        }
        return c1.getTimeInMillis();
    }
    
    public static String getShowTime(Context context,long time)
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(time);
        return new StringBuilder().append(c.get(Calendar.YEAR)).append("-")
            .append(c.get(Calendar.MONTH)+1).append("-").append(c.get(Calendar.DAY_OF_MONTH))
            .append("  (").append(SysUtil.getCurrentDayOfWeek(context, c.get(Calendar.DAY_OF_WEEK)))
            .append(")  ").append(c.get(Calendar.HOUR_OF_DAY)).append(":").append(SysUtil.doubleDataFormat(c.get(Calendar.MINUTE)))
            .toString();
    }
    
    /**
     * 修改提醒
     * @param json
     */
    public static synchronized void uptAlarms(String json)
    {
        com.alibaba.fastjson.JSONArray arrs = JSONArray.parseArray(json);
        Log.e("xpf", "size_:"+arrs.size());
        for (Object obj : arrs)
        {
            Alarm ps = JSON.parseObject(obj.toString(), Alarm.class);
            Alarm alarm = DBHelper.getInstance().getAlarmByUuidd(ps.getUuid());
            if (alarm != null)
            {
                ps.setId(alarm.getId());
                if (ps.getShare() == null || (ps.getShare() != null && !ps.getAllowChange()))
                {
                    DBHelper.getInstance().uptAlarm(ps);
                }
            }
            else
            {
                DBHelper.getInstance().addAlarm(ps);
            }
            for (Task psTask : ps.getTasks())
            {
                if (psTask != null)
                {
                    Task t = DBHelper.getInstance().getTaskByUuid(psTask.getUuid());
                    if (t != null)
                    {
                        psTask.setAlarm(ps);
                        psTask.setAlarmUuid(ps.getUuid());
                        psTask.setId(t.getId());
                        DBHelper.getInstance().uptTask(psTask);
                    }
                    else
                    {
                        psTask.setAlarm(ps);
                        psTask.setAlarmUuid(ps.getUuid());
                        DBHelper.getInstance().addTask(psTask);
                    }
                }
            }
            Alarm uptAlarm = DBHelper.getInstance().getAlarmById(ps.getId());
            uptAlarm.setTask(DBHelper.getInstance().getNextTaskByAlarmId(ps.getId()));
            DBHelper.getInstance().uptAlarm(uptAlarm);
            
        }
    }
}
