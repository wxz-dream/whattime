package com.whatime.controller.service;

import android.os.Handler;

import com.whatime.db.Alarm;

public interface AlarmService
{
    public void setNextAlert();
    
    public Alarm getNextAlarm();
    
    public void disableExpiredAlarms();
    
    public void deleteAlarmById(long alarmId);
    
    public Alarm getAlarmById(long alarmId);
    
    public void uptAlarm(Alarm alarm,Handler myHandler);
    
    public void enableAlarm(final long id, boolean enabled);
    
    public void syncCategory();
    
    public void syncAlarm();

    public void addAlarm(Alarm alarm,Handler myHandler);

    public void checkVersion(Handler handler);

    public void getApk(String url,String path, Handler handler);
}
