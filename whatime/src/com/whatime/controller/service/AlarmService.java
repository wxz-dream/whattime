package com.whatime.controller.service;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.whatime.controller.alarm.AdvanceCons;
import com.whatime.controller.alarm.AlarmCons;
import com.whatime.controller.alarm.RepeatCons;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.util.SysUtil;

/**
 * The Alarms provider supplies info about Alarm Clock settings
 * 核心类，对Clock设置提供支持信息
 */
public class AlarmService
{
    
    public static final String ALARM_DONE_ACTION = "com.whatime.ALARM_DONE";
    
    public static final String ALARM_SNOOZE_ACTION = "com.whatime.ALARM_SNOOZE";
    
    public static final String ALARM_DISMISS_ACTION = "com.whatime.ALARM_DISMISS";
    
    public static final String ALARM_KILLED = "alarm_killed";
    
    public static final String ALARM_KILLED_TIMEOUT = "alarm_killed_timeout";
    
    public static final String ALARM_ALERT_SILENT = "silent";
    
    public static final String CANCEL_SNOOZE = "cancel_snooze";
    
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";
    
    public static final String ALARM_ID = "alarm_id";
    
    public static final String ALARM_TYPE = "alarm_type";
    
    public static final String ALARM_UUID = "alarm_uuid";
    
    public static final String UPT_NOTIFICATION = "com.whatime.UPT_NOTIFICATION";
    
    /**
     * Creates a new Alarm and fills in the given alarm's id.
     * @param tasks 
     */
    public static boolean addAlarm(Context mContext, long alarmId)
    {
        Alarm alarm = DBHelper.getInstance().getAlarmById(alarmId);
        if (alarm == null)
        {
            return false;
        }
        if (alarm.getOpen())
        {
            uptPreferences(mContext, alarm.getId(), alarm.getAlarmTime());
        }
        return true;
    }
    
    /**
     * Removes an existing Alarm.  If this alarm is snoozing, disables
     * snooze.  Sets next alert.
     */
    public static void deleteAlarmById(Context mContext, long id)
    {
        DBHelper.getInstance().deleteAlarmList(id);
        setNextAlert(mContext);
    }
    
    // Private method to get a more limited set of alarms from the database.
    private static List<Alarm> getOpenAlarms(Context mContext)
    {
        return DBHelper.getInstance().getOpenAlarms();
    }
    
    public static void uptPreferences(Context context, long id, long alarmTime)
    {
        // If this alarm fires before the next snooze, clear the snooze to
        // enable this alarm.
        SharedPreferences prefs = context.getSharedPreferences(AlarmCons.PREFERENCES, 0);
        long snoozeTime = prefs.getLong(AlarmCons.PREF_SNOOZE_TIME, 0);
        long alarmId = prefs.getLong(AlarmCons.PREF_SNOOZE_ID, -1);
        if (alarmId != -1)
        {
            //如果设置时间比下一次响铃时间小，更新文件
            if (alarmTime < snoozeTime || snoozeTime < System.currentTimeMillis())
            {
                NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(0);
                SharedPreferences.Editor ed = prefs.edit();
                ed.remove(AlarmCons.PREF_SNOOZE_ID);
                ed.remove(AlarmCons.PREF_SNOOZE_TIME);
                ed.apply();
                saveSnoozeAlert(context, prefs, id, alarmTime);
                uptNotification(context);
            }
        }
        else
        {
            saveSnoozeAlert(context, prefs, id, alarmTime);
        }
    }
    
    private static void uptNotification(Context context)
    {
        Intent uptNotify = new Intent(UPT_NOTIFICATION);
        context.startService(uptNotify);
        
    }

    /**
     * Return an Alarm object representing the alarm id in the database.
     * Returns null if no alarm exists.
     */
    public static Alarm getAlarmById(Context mContext, long id)
    {
        return DBHelper.getInstance().getAlarmById(id);
    }
    
    /**
     * A convenience method to set an alarm in the Alarms
     * content provider.
     * @return Time when the alarm will fire.
     */
    public static void uptAlarm(Context context, Alarm alarm)
    {
        DBHelper.getInstance().uptAlarm(alarm);
        setNextAlert(context);
    }
    
    /**
     * A convenience method to enable or disable an alarm.
     *
     * @param id             corresponds to the _id column
     * @param enabled        corresponds to the ENABLED column
     */
    
    public static void enableAlarm(final Context context, final long id, boolean enabled)
    {
        DBHelper.getInstance().uptAlarmIsOpen(id, enabled);
        setNextAlert(context);
    }
    
    public static Task getTaskById(final Context context, long id)
    {
        return DBHelper.getInstance().getTaskById(id);
    }
    public static Alarm getNextAlarm(final Context context)
    {
        List<Alarm> alarms = getOpenAlarms(context);
        if (alarms.size() > 0)
        {
            return alarms.get(0);
        }
        return null;
    }
    /**
     * Called at system startup, on time/timezone change, and whenever
     * the user changes alarm settings.  Activates snooze if set,
     * otherwise loads all alarms, activates next alert.
     */
    public static void setNextAlert(final Context context)
    {
        disableExpiredAlarms(context);
        List<Alarm> alarms = getOpenAlarms(context);
        if (alarms.size() > 0)
        {
            //获取下一个响铃
            Alarm alarm = alarms.get(0);
            if (alarm != null)
            {
                uptPreferences(context, alarm.getId(), alarm.getAlarmTime());
            }
            else
            {
                disableAlert(context);
            }
        }
        
    }
    
    /**
     * Sets alert in AlarmManger and StatusBar.  This is what will
     * actually launch the alert when the alarm triggers.
     *
     * @param alarm Alarm.
     * @param atTimeInMillis milliseconds since epoch
     */
    private static void enableAlert(Context context, final long id, final long time)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        
        if (true)
        {
            Log.e("wangxianming", "** setAlert id " + id + " atTime " + time);
        }
        
        Intent intent = new Intent(AlarmCons.ALARM_ALERT_ACTION);
        intent.putExtra(AlarmCons.ALARM_RAW_DATA, id);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        am.set(AlarmManager.RTC_WAKEUP, time, sender);
        
    }
    
    /**
     * Disables alert in AlarmManger and StatusBar.
     *
     * @param id Alarm ID.
     */
    static void disableAlert(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender =
            PendingIntent.getBroadcast(context,
                0,
                new Intent(AlarmCons.ALARM_ALERT_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }
    
    public static void saveSnoozeAlert(final Context context, final SharedPreferences prefs, final long id,
        final long time)
    {
        if (id == -1)
        {
            return;
        }
        else
        {
            SharedPreferences.Editor ed = prefs.edit();
            ed.putLong(AlarmCons.PREF_SNOOZE_ID, id);
            ed.putLong(AlarmCons.PREF_SNOOZE_TIME, time);
            ed.apply();
            enableAlert(context, id, time);
        }
    }
    public static void disableExpiredAlarms(Context context)
    {
        List<Alarm> alarms = DBHelper.getInstance().getOpenAlarms();
        for (Alarm a : alarms)
        {
            setNextAlarmAlert(a);
        }
    }
    
    public static void setNextAlarmAlert(Alarm a)
    {
        boolean open = false;
        if (!a.getOpen() || (a.getAlarmTime() != null && a.getAlarmTime() > System.currentTimeMillis()))
        {
            return;
        }
        Iterator<Task> it = a.getTasks().iterator();
        while (it.hasNext())
        {
            Task task = it.next();
            if (task.getDel())
            {
                continue;
            }
            if (task.getAlarmTime() < System.currentTimeMillis())
            {
                long setTime = task.getAlarmTime();
                //如果是提前响铃或者延迟响铃
                if (task.getSetTime() != null)
                {
                    open = true;
                    //如果提前响铃时间小于设置时间，将设置时间返回
                    if (task.getAdvanceOrder() != AdvanceCons.ORDER_DEFAULT && task.getSetTime() > task.getAlarmTime())
                    {
                        task.setAlarmTime(task.getSetTime());
                        
                    }
                    task.setOpen(true);
                    DBHelper.getInstance().uptTask(task);
                    continue;
                }
                if (task.getRepeatType() != RepeatCons.TYPE_DEFAULT)
                {
                    open = true;
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    cal.setTimeInMillis(setTime);
                    cal.set(Calendar.SECOND, 0);
                    switch (task.getRepeatType())
                    {
                        case RepeatCons.TYPE_M_F:
                            alarmRepeatFive(cal, task);
                            break;
                        case RepeatCons.TYPE_S_S:
                            larmRepeatTwo(cal, task);
                            break;
                        case RepeatCons.TYPE_YEAR:
                            cal.add(Calendar.YEAR, 1);
                            break;
                        case RepeatCons.TYPE_MONTH:
                            cal.add(Calendar.MONTH, 1);
                            break;
                        case RepeatCons.TYPE_WEEK:
                            cal.add(Calendar.WEEK_OF_MONTH, 1);
                            break;
                        case RepeatCons.TYPE_DAY:
                            cal.add(Calendar.DATE, 1);
                            break;
                        case RepeatCons.TYPE_OTHER:
                            alarmRepeatOther(cal, task);
                        case RepeatCons.TYPE_WORK:
                            cal = AlarmUtil.getGetupTime(cal.getTimeInMillis());
                            break;
                    
                    }
                    task.setAlarmTime(cal.getTimeInMillis());
                }
                else
                {
                    task.setOpen(false);
                }
                DBHelper.getInstance().uptTask(task);
            }
            open = true;
        }
        
        Task t = DBHelper.getInstance().getNextTaskByAlarmId(a.getId());
        if (t == null)
        {
            open = false;
        }
        else
        {
            a.setTask(t);
            a.setTaskUuid(t.getUuid());
            a.setAlarmTime(t.getAlarmTime());
        }
        a.setOpen(open);
        DBHelper.getInstance().uptAlarm(a);
    }
    
    private static void alarmRepeatOther(Calendar cal, Task task)
    {
        String[] strs = task.getRepeatInfo().split(",");
        switch (Integer.valueOf(strs[0]))
        {
            case 0:
                cal.add(Calendar.YEAR, Integer.valueOf(strs[1]));
                break;
            case 1:
                cal.add(Calendar.MONTH, Integer.valueOf(strs[1]));
                break;
            case 2:
                cal.add(Calendar.WEEK_OF_MONTH, Integer.valueOf(strs[1]));
                break;
        }
    }
    
    private static void larmRepeatTwo(Calendar cal, Task task)
    {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek)
        {
            case Calendar.SATURDAY:
                cal.add(Calendar.DATE, 1);
                break;
            case Calendar.SUNDAY:
                cal.add(Calendar.DATE, 6);
                break;
        }
    }
    
    private static void alarmRepeatFive(Calendar cal, Task task)
    {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek)
        {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                cal.add(Calendar.DATE, 1);
                break;
            case Calendar.FRIDAY:
                cal.add(Calendar.DATE, 3);
                break;
        }
    }
    
    public static void syncAlarm(Context context)
    {
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                new RemoteApiImpl().alrmShareGetLastSyncTime(user.getUuid(), user.getMime());
            }
        }
    }
    
    public static void syncCategory(Context context)
    {
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                new RemoteApiImpl().getSyncCategory(user.getUuid(), user.getMime(), DBHelper.getInstance()
                    .getCategoryCount());
            }
        }
    }
    
}
