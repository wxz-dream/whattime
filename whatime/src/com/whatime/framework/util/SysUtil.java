package com.whatime.framework.util;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;

public class SysUtil
{
    /**
     * 判断是否具有网络连接
     * @param context
     * @return
     */
    public static final boolean hasNetWorkConection(Context context)
    {
        //获取连接活动管理器
        final ConnectivityManager connectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取连接的网络信息
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable());
    }
    
    /**
     * 判断是否具有wifi连接
     * @param context
     * @return
     */
    public static final boolean hasWifiConnection(Context context)
    {
        //获取连接活动管理器
        final ConnectivityManager connectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (networkInfo != null && networkInfo.isAvailable());
    }
    
    /**
     * 判断是否有GPRS连接
     * @param context
     * @return
     */
    public static final boolean hasGPRSConnection(Context context)
    {
        //获取连接活动管理器
        final ConnectivityManager connectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (networkInfo != null && networkInfo.isAvailable());
    }
    
    /**
     * 判断网络连接类型
     * @param context
     * @return
     */
    public static final int getNetworkConnectionType(Context context)
    {
        final ConnectivityManager connectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiNetWorkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileNetWorkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (wifiNetWorkInfo != null && wifiNetWorkInfo.isAvailable())
        {
            return ConnectivityManager.TYPE_WIFI;
        }
        else if (mobileNetWorkInfo != null && mobileNetWorkInfo.isAvailable())
        {
            return ConnectivityManager.TYPE_MOBILE;
        }
        else
        {
            return -1;
        }
    }
    
    /**
     * 获取当前星期几
     */
    public static final String getCurrentDayOfWeek(Context context, int week)
    {
        String weekStr = "";
        switch (week)
        {
        
            case Calendar.MONDAY:
                weekStr = context.getString(R.string.Monday);
                break;
            case Calendar.TUESDAY:
                weekStr = context.getString(R.string.Tuesday);
                break;
            case Calendar.WEDNESDAY:
                weekStr = context.getString(R.string.Wednesday);
                break;
            case Calendar.THURSDAY:
                weekStr = context.getString(R.string.Thursday);
                break;
            case Calendar.FRIDAY:
                weekStr = context.getString(R.string.Friday);
                break;
            case Calendar.SATURDAY:
                weekStr = context.getString(R.string.Saturday);
                break;
            case Calendar.SUNDAY:
                weekStr = context.getString(R.string.Sunday);
                break;
        
        }
        return weekStr;
    }
    
    /**
     * 获取当前日期
     */
    public static final String getCurrentDay(Context context)
    {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance(TimeZone.getDefault()); //获取东八区时间
        int year = c.get(Calendar.YEAR); //获取年
        int month = c.get(Calendar.MONTH) + 1; //获取月份，0表示1月份
        int day = c.get(Calendar.DAY_OF_MONTH); //获取当前天数
        int week = c.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week)
        {
            case Calendar.MONDAY:
                weekStr = context.getString(R.string.Monday);
                break;
            case Calendar.TUESDAY:
                weekStr = context.getString(R.string.Tuesday);
                break;
            case Calendar.WEDNESDAY:
                weekStr = context.getString(R.string.Wednesday);
                break;
            case Calendar.THURSDAY:
                weekStr = context.getString(R.string.Thursday);
                break;
            case Calendar.FRIDAY:
                weekStr = context.getString(R.string.Friday);
                break;
            case Calendar.SATURDAY:
                weekStr = context.getString(R.string.Saturday);
                break;
            case Calendar.SUNDAY:
                weekStr = context.getString(R.string.Sunday);
                break;
        }
        
        sb.append(year).append("-").append(month).append("-").append(day).append("(").append(weekStr).append(")");
        return sb.toString();
    }
    
    /**
     * 获取当前时间
     */
    public static final String getCurrentTimeWithoutSecond()
    {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance(TimeZone.getDefault()); //获取东八区时间
        int time = c.get(Calendar.HOUR_OF_DAY); //获取当前小时
        int min = c.get(Calendar.MINUTE); //获取当前分钟
        sb.append(time).append(":").append(doubleDataFormat(min));
        return sb.toString();
    }
    
    /**
     * $$双位数字前补0
     */
    public static String doubleDataFormat(int i)
    {
        StringBuilder ret = new StringBuilder();
        if (i < 10)
        {
            ret.append("0");
        }
        ret.append(i);
        return ret.toString();
    }
    
    public static long getTime(int year, int month, int day, int hour, int minute, int second)
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault()); //获取东八区时间
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);
        year = year == -1 ? mYear : year;
        month = month == -1 ? mMonth : month;
        day = day == -1 ? mDay : day;
        hour = hour == -1 ? mHour : hour;
        minute = minute == -1 ? mMinute : minute;
        second = second == -1 ? mSecond : second;
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTimeInMillis();
    }
    
    /**
     * 获取客户端手机信息
     * @return
     */
    public static String getPhoneInfo()
    {
        Context ctx = MyApp.getInstance().getApplicationContext();
        StringBuilder info = new StringBuilder();
        TelephonyManager mTm = (TelephonyManager)ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号    
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        int sdk = android.os.Build.VERSION.SDK_INT;
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        
        StringBuilder sb = new StringBuilder();
        String brand = String.valueOf(Build.BRAND);
        sb.append(brand);
        sb.append("/");
        String operator = mTm.getNetworkOperatorName();
        sb.append(operator);
        
        info.append("IMEI:")
            .append(imei)
            .append("*;*")
            .append("IMSI:")
            .append(imsi)
            .append("*;*")
            .append("MTYPE:")
            .append(mtype)
            .append("*;*")
            .append("NUMER:")
            .append(numer)
            .append("*;*")
            .append("SDK:")
            .append(sdk)
            .append("*;*")
            .append("LANGUAGE:")
            .append(language)
            .append("*;*")
            .append("COUNTRY:")
            .append(country)
            .append("*;*")
            .append("OPERATOR:")
            .append(sb.toString())
            .append("*;*");
        return info.toString();
    }
    
    /**
     * 获取mime
     * @return
     */
    public static String getMime()
    {
        Context ctx = MyApp.getInstance().getApplicationContext();
        TelephonyManager mTm = (TelephonyManager)ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        return mTm.getDeviceId() == null ? "" : mTm.getDeviceId();
    }
    
    /**
     * 判断用户是否为好友
     * @param uuid
     * @return
     */
    public static boolean isFriends(String uuid)
    {
        List<User> myFriends = MyApp.getInstance().getMyFriends();
        User me = MyApp.getInstance().getUser();
        if (me != null && me.getUuid().equals(uuid))
        {
            return true;
        }
        for (User u : myFriends)
        {
            if (u.getUuid().equals(uuid))
            {
                return true;
            }
        }
        return false;
    }
    
    //获取当前版本号
    public static String getAppVersionName()
    {
        String versionName = "";
        try
        {
            PackageManager packageManager = MyApp.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.whatime", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName))
            {
                return "";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return versionName;
    }
    
    public static void installApp(Context context)
    {
        // 获取url,然后调用系统的Intent,来自动安装下载的apk文件
        final File realFilePath = new File(Environment.getExternalStorageDirectory() + "/ttyy/ttyy.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(realFilePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    
}
