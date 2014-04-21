package com.whatime.controller.service;

import android.os.Handler;

import com.whatime.db.Alarm;
import com.whatime.framework.network.pojo.ApkVersion;

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

    public ApkVersion checkVersion();

    public void getApk(String url,String path, Handler handler);
}
