package com.whatime.controller.service;

import com.whatime.db.Alarm;

public interface AlarmService
{
    public void setNextAlert();
    
    public Alarm getNextAlarm();
    
    public void disableExpiredAlarms();
    
    public void deleteAlarmById(long alarmId);
    
    public Alarm getAlarmById(long alarmId);
    
    public void uptAlarm(Alarm alarm);
    
    public void enableAlarm(final long id, boolean enabled);
    
    public void syncCategory();
    
    public void syncAlarm();
}
