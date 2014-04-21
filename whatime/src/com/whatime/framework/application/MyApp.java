package com.whatime.framework.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;

import com.whatime.db.DBHelper;
import com.whatime.db.DaoMaster;
import com.whatime.db.DaoMaster.OpenHelper;
import com.whatime.db.DaoSession;
import com.whatime.db.User;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.module.weather.util.WebServiceUtil;

public class MyApp extends Application
{
    public static final String DB_NAME = "wtx.db";
    
    private static MyApp instance;
    
    private static User user = null;
    
    private static DaoMaster daoMaster;
    
    private static DaoSession daoSession;
    
    private static List<String> providers;
    
    private static HashMap<String, List<String>> citys;
    
    private static List<User> myFriends;
    
    public MyApp()
    {
    }
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        //CrashHandler.getInstance().init(this);
        new Thread()
        {
            public void run()
            {
                getCitys();
            };
        }.start();
    }
    
    public static MyApp getInstance()
    {
        return instance;
    }
    
    public synchronized HashMap<String, List<String>> getCitys()
    {
        if (citys == null || citys.size() == 0)
        {
            citys = new HashMap<String, List<String>>();
            List<String> ps = getProvince();
            for(String pro: ps)
            {
                List<String> city = WebServiceUtil.getCityListByProvince(pro);
                Collections.sort(city);
                citys.put(pro, city);
            }
        }
        
        return citys;
    }
    
    public synchronized List<String> getProvince()
    {
        if (null == providers || providers.size() == 0)
        {
            providers = WebServiceUtil.getProvinceList();
            Collections.sort(providers);
        }
        return providers;
    }
    
    public void setUser(User u)
    {
        user = u;
    }
    
    public User getUser()
    {
        if (null == user)
        {
            user = DBHelper.getInstance().getUser();
        }
        return user;
    }
    
    public List<User> getMyFriends()
    {
        if(myFriends ==null)
        {
            myFriends = new RemoteApiImpl().findMyFriends();
        }
        return myFriends;
    }
    
    public void setMyFriends(List<User> fs)
    {
        myFriends = fs;
    }
    
    public static DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession()
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(instance.getApplicationContext());
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
    
}
