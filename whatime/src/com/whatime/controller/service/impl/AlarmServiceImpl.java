package com.whatime.controller.service.impl;

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
import android.os.Handler;
import android.util.Log;

import com.whatime.controller.cons.AdvanceCons;
import com.whatime.controller.cons.AlarmCons;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.cons.RepeatCons;
import com.whatime.controller.service.AlarmService;
import com.whatime.controller.service.AlarmUtil;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.util.SysUtil;

public class AlarmServiceImpl implements AlarmService
{
    
    public static final Context context = MyApp.getInstance().getApplicationContext();
    
    @Override
    public void setNextAlert()
    {
        disableExpiredAlarms();
        List<Alarm> alarms = getOpenAlarms();
        if (alarms.size() > 0)
        {
            //获取下一个响铃
            Alarm alarm = alarms.get(0);
            uptNotification();
            if (alarm != null)
            {
                uptPreferences(alarm.getId(), alarm.getAlarmTime());
            }
            else
            {
                disableAlert();
            }
        }
    }
    
    @Override
    public void disableExpiredAlarms()
    {
        List<Alarm> alarms = DBHelper.getInstance().getOpenAlarms();
        for (Alarm a : alarms)
        {
            setNextAlarmAlert(a);
        }
    }
    
    @Override
    public void deleteAlarmById(long alarmId)
    {
        DBHelper.getInstance().deleteAlarmList(alarmId);
        setNextAlert();
    }
    
    @Override
    public Alarm getAlarmById(long alarmId)
    {
        return DBHelper.getInstance().getAlarmById(alarmId);
    }
    
    @Override
    public void uptAlarm(Alarm alarm, Handler myHandler)
    {
        DBHelper.getInstance().uptAlarm(alarm);
        setNextAlert();
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                if (alarm.getShare() != null && alarm.getShare().length() > 0)
                {
                    new RemoteApiImpl().alarmShareEdit(user, alarm, myHandler);
                }
                else
                {
                    new RemoteApiImpl().alarmLocalEdit(user, alarm, myHandler);
                }
            }
        }
    }
    
    @Override
    public void enableAlarm(long id, boolean enabled)
    {
        DBHelper.getInstance().uptAlarmIsOpen(id, enabled);
        setNextAlert();
    }
    
    @Override
    public void syncAlarm()
    {
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                new RemoteApiImpl().alarmLocalGetLastSyncTime(user.getUuid(), user.getMime());
                new RemoteApiImpl().alarmShareGetLastSyncTime(user.getUuid(), user.getMime());
            }
        }
    }
    
    @Override
    public void syncCategory()
    {
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user == null)
            {
                user = new User();
                user.setUuid("");
                user.setMime("");
            }
            new RemoteApiImpl().getSyncCategory(user.getUuid(), user.getMime(), DBHelper.getInstance()
                .getCategoryCount());
        }
    }
    
    @Override
    public Alarm getNextAlarm()
    {
        List<Alarm> alarms = getOpenAlarms();
        if (alarms.size() > 0)
        {
            return alarms.get(0);
        }
        return null;
    }
    
    private List<Alarm> getOpenAlarms()
    {
        return DBHelper.getInstance().getOpenAlarms();
    }
    
    private void uptPreferences(Long id, Long alarmTime)
    {
        SharedPreferences prefs = context.getSharedPreferences(AlarmCons.PREFERENCES, 0);
        long snoozeTime = prefs.getLong(AlarmCons.PREF_SNOOZE_TIME, 0);
        long alarmId = prefs.getLong(AlarmCons.PREF_SNOOZE_ID, -1);
        if (alarmId != -1)
        {
            //如果设置时间比下一次响铃时间小，更新文件
            if (alarmTime < snoozeTime || snoozeTime < System.currentTimeMillis() || alarmId == id)
            {
                NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(0);
                SharedPreferences.Editor ed = prefs.edit();
                ed.remove(AlarmCons.PREF_SNOOZE_ID);
                ed.remove(AlarmCons.PREF_SNOOZE_TIME);
                ed.apply();
                saveSnoozeAlert(context, prefs, id, alarmTime);
            }
        }
        else
        {
            saveSnoozeAlert(context, prefs, id, alarmTime);
        }
    }
    
    private void uptNotification()
    {
        Intent uptNotify = new Intent(AlarmServiceCons.UPT_NOTIFICATION);
        context.startService(uptNotify);
    }
    
    public void saveSnoozeAlert(final Context context, final SharedPreferences prefs, final long id,
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
            enableAlert(id, time);
        }
    }
    
    private void setNextAlarmAlert(Alarm a)
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
    
    private void alarmRepeatOther(Calendar cal, Task task)
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
    
    private void larmRepeatTwo(Calendar cal, Task task)
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
    
    private void alarmRepeatFive(Calendar cal, Task task)
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
    
    private void disableAlert()
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender =
            PendingIntent.getBroadcast(context,
                0,
                new Intent(AlarmCons.ALARM_ALERT_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }
    
    private void enableAlert(long id, long time)
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
    
    @Override
    public void addAlarm(Alarm alarm, Handler myHandler)
    {
        setNextAlert();
        if (SysUtil.hasNetWorkConection(context))
        {
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                if (alarm.getShare() != null && alarm.getShare().length() > 0)
                {
                    alarm.setOwerUuid(alarm.getUuid());
                    alarm.setOwerUserUuid(alarm.getUserUuid());
                    new RemoteApiImpl().alarmShareAdd(user, alarm, myHandler);
                }
                else
                {
                    new RemoteApiImpl().alarmLocalAdd(user, alarm, myHandler);
                }
            }
        }
    }
    
}
