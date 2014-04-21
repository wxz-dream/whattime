package com.whatime.controller.center;

import android.os.Handler;

import com.whatime.controller.service.AlarmService;
import com.whatime.controller.service.impl.AlarmServiceImpl;
import com.whatime.db.Alarm;
import com.whatime.framework.network.pojo.ApkVersion;

public class AlarmController
{
    public AlarmService service = new AlarmServiceImpl();
    
    /**
     * 设置下一个提醒
     * @param context
     */
    public void setNextAlert()
    {
        service.setNextAlert();
    }
    
    /**
     * 取消过期的闹钟
     * @param context
     */
    public void disableExpiredAlarms()
    {
        service.disableExpiredAlarms();
    }
    
    /**
     * 删除一个提醒
     * @param mContext
     * @param alarmId
     */
    public void deleteAlarm(long alarmId)
    {
        service.deleteAlarmById(alarmId);
    }
    
    /**
     * 根据提醒ID获取提醒信息
     * @param mContext
     * @param alarmId
     * @return
     */
    public Alarm getAlarmById(long alarmId)
    {
        return service.getAlarmById(alarmId);
    }
    
    /**
     * 添加提醒
     * @param context
     * @param alarm
     * @return
     */
    public void addAlarm(Alarm alarm, Handler myHandler)
    {
        service.addAlarm(alarm, myHandler);
    }
    
    /**
     * 更新提醒
     * @param context
     * @param alarm
     * @return
     */
    public void uptAlarm(Alarm alarm, Handler myHandler)
    {
        service.uptAlarm(alarm, myHandler);
    }
    
    /**
     * 开启提醒
     * @param context
     * @param id
     * @param enabled
     */
    public void enableAlarm(final long id, boolean enabled)
    {
        service.enableAlarm(id, enabled);
    }
    
    /**
     * 同步
     * @param context
     */
    public void sync()
    {
        service.syncCategory();
        service.syncAlarm();
    }
    
    /**
     * 获取下一个提醒
     * @return
     */
    public Alarm getNextAlarm()
    {
        return service.getNextAlarm();
    }
    
    /**
     * 检测版本更新
     * @param myHandler 
     */
    public ApkVersion checkVersion()
    {
        return service.checkVersion();
    }
    
    public void getApk(String url, String path, Handler handler)
    {
        service.getApk(url, path, handler);
    }
    
}
