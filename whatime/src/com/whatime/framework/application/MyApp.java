package com.whatime.framework.application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import roboguice.application.RoboApplication;
import android.content.Context;

import com.whatime.db.DBHelper;
import com.whatime.db.DaoMaster;
import com.whatime.db.DaoMaster.OpenHelper;
import com.whatime.db.DaoSession;
import com.whatime.db.User;
import com.whatime.module.weather.util.WebServiceUtil;

public class MyApp extends RoboApplication {
    public static final String DB_NAME = "wtx.db";
	private static MyApp instance;
	private static User user = null;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	private static HashMap<String, String> providers;
	private static HashMap<String, HashMap<String,String>> citys;
	

	
	public MyApp() {
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//CrashHandler.getInstance().init(this);
		new Thread(){
		    public void run() {
		        getCitys();
		    };
		}.start();
	}

	public static MyApp getInstance() {
		return instance;
	}
	
	public synchronized HashMap<String, HashMap<String,String>>  getCitys()
	{
	    if(citys==null||citys.size()==0)
	    {
	        citys = new HashMap<String, HashMap<String,String>>();
	        HashMap<String, String> ps = getProvince();
	        Iterator<String> it = ps.keySet().iterator();
	        while(it.hasNext())
	        {
	            String key = it.next();
	            HashMap<String, String> city = WebServiceUtil.getCityListByProvince(ps.get(key));
	            citys.put(ps.get(key), city);
	        }
	    }
	    
	    return citys;
	}
	
	public synchronized HashMap<String, String> getProvince()
	{
	    if(null==providers||providers.size()==0)
	    {
	        providers = WebServiceUtil.getProvinceList();
	    }
	    return providers;
	}
	
	public void setUser(User u)
	{
	    user = u;
	}
	
	public User getUser()
	{
	    if(null==user)
	    {
	        //user = DBHelper.getInstance().getUser();
	        user = new User();
	        user.setId(1l);
	        user.setUserName("aaa");
	        user.setPassword("aaa");
	        user.setAvailable(true);
	        user.setUuid(UUID.randomUUID().toString());
	    }
	    return user;
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
	public static DaoSession getDaoSession(Context context)
	{
	    if (daoSession == null)
	    {
	        if (daoMaster == null)
	        {
	            daoMaster = getDaoMaster(context);
	        }
	        daoSession = daoMaster.newSession();
	    }
	    return daoSession;
	}

	
}
