package com.whatime.framework.constant;

public class Constants
{
    //App路径
    public static final String BASE_SDCARD_PATH = "/mnt/sdcard/Whatime";
    
    //日志路径
    public static final String LOCAL_LOGS_PATH = BASE_SDCARD_PATH + "logs/";
    
    public static final String PACKAGENAME = "com.whatime";
    
    //本地测试用    
    //http://192.168.11.5:8080/whatimeServer/android/
    //http://whatimeServer.sinaapp.com/android/
    //http://192.168.11.7:8080/whatimeSae/android/
    //http://whatime.duapp.com/
    public static final String URL = "http://whatime.duapp.com/android/";
    
    public static final String REGIST_USER_POST_URL = "user/registUser";
    
    public static final String LOGIN_USER_POST_URL = "user/login";
    
    public static final String MODIFY_USER_PASSWORD_POST_URL = "user/uptPassword";
    
    public static final String MODIFY_USER_POST_URL = "user/uptUserInfo";
    
    public static final String ALARM_LOCAL_ADD_POST_URL = "userLocalAlarm/alarmLocalAdd";
    
    public static final String ALARM_SHARE_ADD_POST_URL = "userShareAlarm/alarmShareAdd";
    
    public static final String ALARM_LOCAL_EDIT_POST_URL = "userLocalAlarm/alarmLocalEdit";
    
    public static final String ALARM_SHARE_EDIT_POST_URL = "userShareAlarm/alarmShareEdit";
    
    public static final String ALARM_LOCAL_GETSYNCTIME_POST_URL = "userLocalAlarm/alarmLocalGetLastSyncTime";
    
    public static final String ALARM_SHARE_GETSYNCTIME_POST_URL = "userShareAlarm/alarmShareGetLastSyncTime";
    
    public static final String ALARM_LOCAL_SYNC_POST_URL = "userLocalAlarm/alarmLocalSync";
    
    public static final String ALARM_SHARE_SYNC_POST_URL = "userShareAlarm/alarmShareSync";
    
    public static final String MARKET_GET_MARKET_ALARM_POST_URL = "market/getMarketAlarm";
    
    public static final String MARKET_GET_SYNC_CATEGORY_POST_URL = "cate/getSyncCategory";
    
    //操作
    public static final String OPT = "OPT";
    
    //添加操作
    public static final int OPT_ADD = 1;
    
    //修改操作
    public static final int OPT_EDIT = 2;
}
