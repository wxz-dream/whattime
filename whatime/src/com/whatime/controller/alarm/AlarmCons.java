package com.whatime.controller.alarm;

public class AlarmCons
{

    // TYPE 0 快速闹钟 1 起床闹钟
    public static final int TYPE_QUICK = 0;

    public static final int TYPE_OTHER = 1;

    public static final int TYPE_BIRTHDAY = 2;

    public static final int TYPE_GETUP = 3;

    public static final int TYPE_SLEEP = 4;

    public static final int TYPE_DRINK = 5;

    public static final int TYPE_SOMEDAY = 6;

    public static final int TYPE_TAKE_PILLS = 7;

    // 来源
    public static final int FROMS_ANDROID = 1;

    public static final int FROMS_IPHONE = 2;

    // 分享到
    public static final int SHARE_FRIENDS = 0;

    public static final int SHARE_NET = 1;

    public static final int SHARE_SINA = 2;
    
    public static final int SHARE_QQ = 3;
    
    public static final int SHARE_RENREN = 4;
    
    public static final int SHARE_TECENT = 5;
    
    public static final String PREFERENCES = "AlarmClock";
    
    public final static String PREF_SNOOZE_TIME = "snooze_time";
    
    public final static String PREF_SNOOZE_ID = "snooze_id";
    
    public static final String ALARM_ALERT_ACTION = "com.whatime.ALARM_ALERT";
    
    public static final String ALARM_RAW_DATA = "intent.extra.alarm_raw";

}
