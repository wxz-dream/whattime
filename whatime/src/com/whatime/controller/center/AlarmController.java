package com.whatime.controller.center;

import android.content.Context;

import com.whatime.controller.service.AlarmService;
import com.whatime.db.Alarm;

public class AlarmController
{
    /**
     * 设置下一个提醒
     * @param context
     */
    public static void setNextAlert(final Context context)
    {
        AlarmService.setNextAlert(context);
    }
    /**
     * 取消过期的闹钟
     * @param context
     */
    public static void disableExpiredAlarms(final Context context)
    {
        AlarmService.disableExpiredAlarms(context);
    }
    /**
     * 删除一个提醒
     * @param mContext
     * @param alarmId
     */
    public static void deleteAlarm(Context mContext, long alarmId)
    {
        AlarmService.deleteAlarmById(mContext, alarmId);
    }
    /**
     * 根据提醒ID获取提醒信息
     * @param mContext
     * @param alarmId
     * @return
     */
    public static Alarm getAlarmById(Context mContext, long alarmId)
    {
        return AlarmService.getAlarmById(mContext, alarmId);
    }
    /**
     * 更新提醒
     * @param context
     * @param alarm
     * @return
     */
    public static void uptAlarm(Context context, Alarm alarm)
    {
        AlarmService.uptAlarm(context, alarm);
    }
    /**
     * 开启提醒
     * @param context
     * @param id
     * @param enabled
     */
    public static void enableAlarm(final Context context, final long id, boolean enabled)
    {
        AlarmService.enableAlarm(context, id, enabled);
    }
    public static void sync(Context context)
    {
        AlarmService.syncCategory(context);
        AlarmService.syncAlarm(context);
    }
    
}
