package com.whatime.module.holiday;

import java.util.ArrayList;

import android.content.Context;

import com.whatime.db.DBHelper;
import com.whatime.db.Holiday;

public class HolidayCache
{
    private static ArrayList<Holiday> hosForAlarm = new ArrayList<Holiday>();
    private static ArrayList<Holiday> hosForRest = new ArrayList<Holiday>();
    /**
     * 获取所有需要提醒的假日信息
     * @return
     */
    public static ArrayList<Holiday> getAllHolidaysForAlarm(Context mContext)
    {
        if(hosForAlarm.size()==0)
        {
            hosForAlarm = (ArrayList<Holiday>)DBHelper.getInstance().getAllHolidaysForAlarm();
        }
        return hosForAlarm;
    }
    /**
     * 获取所有需要休息的假日信息
     * @return
     */
    public static ArrayList<Holiday> getAllHolidaysForRest(Context mContext)
    {
        if(hosForRest.size()==0)
        {
            hosForRest = (ArrayList<Holiday>)DBHelper.getInstance().getAllHolidaysForRest();
        }
        return hosForRest;
    }
    /**
     * 是否假日提醒
     * @param date
     * @return
     */
    public static boolean isHolidayForALarm(Context mContext,String date)
    {
        for(Holiday hos :getAllHolidaysForAlarm(mContext))
        {
            if(hos.getDayOfYear().equals(date))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * 是否假日休息
     * @param date
     * @return
     */
    public static boolean isHolidayForRest(Context mContext,String date)
    {
        for(Holiday hos :getAllHolidaysForRest(mContext))
        {
            if(hos.getDayOfYear().equals(date))
            {
                return true;
            }
        }
        return false;
    }
}
