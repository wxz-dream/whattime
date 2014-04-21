package com.whatime.framework.constant;

public class Constants
{
    //App路径
    public static final String BASE_SDCARD_PATH = "/mnt/sdcard/Whatime";
    
    //日志路径
    public static final String LOCAL_LOGS_PATH = BASE_SDCARD_PATH + "logs/";
    
    public static final String PACKAGENAME = "com.whatime";
    
    //本地测试用    
    //http://192.168.11.5:8080/whatimeServer/
    //http://whatimeServer.sinaapp.com/
    //http://192.168.11.7:8080/whatimeSae/
    //http://whatime.duapp.com/
    //http://192.168.11.9:8080/whatimeServer/
    public static final String URL = "http://whatime.duapp.com/";
    
    public static final String REGIST_USER_POST_URL = "android/user/registUser";
    
    public static final String LOGIN_USER_POST_URL = "android/user/login";
    
    public static final String MODIFY_USER_PASSWORD_POST_URL = "android/user/uptPassword";
    
    public static final String MODIFY_USER_POST_URL = "android/user/uptUserInfo";
    
    public static final String GET_USER_BY_UUID_POST_URL = "android/user/getUserByUuid";
    
    public static final String GET_USER_BY_USERNAME_POST_URL = "android/user/getUserByUserName";
    
    public static final String ALARM_LOCAL_ADD_POST_URL = "android/userLocalAlarm/alarmLocalAdd";
    
    public static final String ALARM_SHARE_ADD_POST_URL = "android/userShareAlarm/alarmShareAdd";
    
    public static final String ALARM_LOCAL_EDIT_POST_URL = "android/userLocalAlarm/alarmLocalEdit";
    
    public static final String ALARM_SHARE_EDIT_POST_URL = "android/userShareAlarm/alarmShareEdit";
    
    public static final String ALARM_LOCAL_GETSYNCTIME_POST_URL = "android/userLocalAlarm/alarmLocalGetLastSyncTime";
    
    public static final String ALARM_SHARE_GETSYNCTIME_POST_URL = "android/userShareAlarm/alarmShareGetLastSyncTime";
    
    public static final String ALARM_LOCAL_SYNC_POST_URL = "android/userLocalAlarm/alarmLocalSync";
    
    public static final String ALARM_SHARE_SYNC_POST_URL = "android/userShareAlarm/alarmShareSync";
    
    public static final String MARKET_GET_MARKET_ALARM_POST_URL = "android/market/getMarketAlarm";
    
    public static final String MARKET_GET_SYNC_CATEGORY_POST_URL = "android/cate/getSyncCategory";
    
    public static final String MARKET_JOIN_ALARM_POST_URL = "android/userShareAlarm/joinAlarm";
    
    public static final String MARKET_ADD_FRIEND_REQ_POST_URL = "android/friend/addFriendReq";
    
    public static final String MARKET_ADD_OPERA_FRIENDS_REQ_POST_URL = "android/friend/operaMyFriendReq";
    
    public static final String MARKET_ADD_FIND_FRIENDS_REQ_POST_URL = "android/friend/findMyAddFriendsReq";
    
    public static final String MARKET_ADD_FIND_MY_FRIENDS_POST_URL = "android/friend/findMyFriends";
    
    public static final String MARKET_GET_MAN_ALARM_POST_URL = "android/market/getManAlarm";
    
    public static final String MARKET_FIND_FRIENDS_ALARMS_POST_URL = "android/friend/findFriendsAlarm";
    
    public static final String SYSTEM_PUT_FILE_URL = "file";
    
    public static final String SYSTEM_CHECK_VERSION_URL = "android/apkVersion";
    
    //操作
    public static final String OPT = "OPT";
    
    //添加操作
    public static final int OPT_ADD = 1;
    
    //修改操作
    public static final int OPT_EDIT = 2;
}
