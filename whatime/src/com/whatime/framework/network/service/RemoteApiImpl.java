package com.whatime.framework.network.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.constant.Constants;
import com.whatime.framework.network.http.HttpAsycnUtil;
import com.whatime.framework.network.http.HttpSycnUtil;
import com.whatime.framework.network.http.MD5;
import com.whatime.framework.network.http.MyJsonHttpResponseHandler;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.ui.activity.MainActivity;
import com.whatime.framework.util.SysUtil;

public class RemoteApiImpl
{
    private AlarmController controller = new AlarmController();
    /**
     * 注册
     * @param c
     * @param userName
     * @param password
     * @return
     */
    public void registUser(final String userName, final String password, final Handler handler)
    {
        RequestParams params = new RequestParams();
        Resources r = MyApp.getInstance().getApplicationContext().getResources();
        Uri uri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(R.drawable.icon) + "/"
                + r.getResourceTypeName(R.drawable.icon) + "/" + r.getResourceEntryName(R.drawable.icon));
        final User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUserName(userName);
        user.setPassword(MD5.GetMD5Code(password));
        user.setMime(SysUtil.getMime());
        user.setPhoneInfo(SysUtil.getPhoneInfo());
        user.setUserphotoUri(uri.toString());
        user.setDel(false);
        user.setAvailable(false);
        params.put("user", JSON.toJSONString(user));
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.REGIST_USER_POST_URL), params, new MyJsonHttpResponseHandler(
            msg, data, handler)
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                if (response == null)
                {
                    return;
                }
                
                try
                {
                    int state = response.getInt("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        DBHelper.getInstance().clearUser();
                        DBHelper.getInstance().addUser(user);
                        DBHelper.getInstance().setAlarmUserUuid(user.getUuid());
                    }
                }
                catch (JSONException e)
                {
                    data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                    data.putString(ResponseCons.STATEINFO, e.getMessage());
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }
    
    /**
     * 修改密码
     * @param uuid
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public void uptPassword(final String oldPassword, final String newPassword, final Handler handler)
    {
        final User user = MyApp.getInstance().getUser();
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        final String md5Pwd = MD5.GetMD5Code(newPassword);
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("oldPassword", MD5.GetMD5Code(oldPassword));
        params.put("newPassword", md5Pwd);
        HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.MODIFY_USER_PASSWORD_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (user != null)
                            {
                                user.setPassword(md5Pwd);
                                DBHelper.getInstance().uptUser(user);
                                MyApp.getInstance().setUser(user);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 登录
     * @param c
     * @param userName
     * @param password
     * @return
     */
    public void login(final String userName, final String password, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userName", userName);
        final String md5Pwd = MD5.GetMD5Code(password);
        params.put("password", md5Pwd);
        HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.LOGIN_USER_POST_URL), params, new MyJsonHttpResponseHandler(
            msg, data, handler)
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                if (response == null)
                {
                    return;
                }
                try
                {
                    int state = response.getInt("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        
                        DBHelper.getInstance().clearUser();
                        User user = JSON.parseObject(response.getString("resInfo"), User.class);
                        DBHelper.getInstance().addUser(user);
                        MyApp.getInstance().setUser(user);
                        DBHelper.getInstance().setAlarmUserUuid(user.getUuid());
                        controller.sync();
                    }
                }
                catch (JSONException e)
                {
                    data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                    data.putString(ResponseCons.STATEINFO, e.getMessage());
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
        
    }
    
    /**
     * 更新用户信息
     * @param user
     * @param handler
     */
    public void uptUserInfo(final User user, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("user", JSON.toJSONString(user));
        HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.MODIFY_USER_POST_URL), params, new MyJsonHttpResponseHandler(
            msg, data, handler)
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                if (response == null)
                {
                    return;
                }
                try
                {
                    int state = response.getInt("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (user != null)
                        {
                            DBHelper.getInstance().uptUser(user);
                            MyApp.getInstance().setUser(user);
                        }
                    }
                }
                catch (JSONException e)
                {
                    data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                    data.putString(ResponseCons.STATEINFO, e.getMessage());
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }
    
    /**
     * 非分享提醒添加
     * @param user
     * @param alarm
     * @param handler
     */
    public void alarmLocalAdd(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userLocalAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_ADD_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            Alarm resAlarm = JSON.parseObject(response.getString("resInfo"), Alarm.class);
                            for (Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            DBHelper.getInstance().uptAlarm(alarm);
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 分享提醒添加
     * @param user
     * @param alarm
     * @param handler
     */
    public void alarmShareAdd(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userShareAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_ADD_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            Alarm resAlarm = JSON.parseObject(response.getString("resInfo"), Alarm.class);
                            for (Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            DBHelper.getInstance().uptAlarm(alarm);
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 非分享提醒修改
     * @param user
     * @param alarm
     * @param handler
     */
    public void alarmLocalEdit(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userLocalAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_EDIT_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            Alarm resAlarm = JSON.parseObject(response.getString("resInfo"), Alarm.class);
                            for (Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            DBHelper.getInstance().uptAlarm(alarm);
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 分享提醒修改
     * @param user
     * @param alarm
     * @param handler
     */
    public void alarmShareEdit(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userShareAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_EDIT_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            Alarm resAlarm = JSON.parseObject(response.getString("resInfo"), Alarm.class);
                            for (Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            DBHelper.getInstance().uptAlarm(alarm);
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 非分享提醒获取同步时间
     */
    public void alarmLocalGetLastSyncTime(final String userUuid, final String mime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", userUuid);
        params.put("mime", mime);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_GETSYNCTIME_POST_URL),
            params,
            new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                String resInfo = response.getString("resInfo");
                                com.alibaba.fastjson.JSONObject obj = JSON.parseObject(resInfo);
                                long shareSyncTime = obj.getLongValue("syncTime");
                                long localSyncTime = DBHelper.getInstance().getAlarmMaxSyncTime();
                                //本地同步时间小于服务器同步时间
                                if (localSyncTime < shareSyncTime)
                                {
                                    alarmLocalSync(userUuid, mime, localSyncTime);
                                }
                            }
                        }
                        List<Alarm> noSyncAlarms = DBHelper.getInstance().getNoSyncLocalAlarms();
                        if (noSyncAlarms != null && noSyncAlarms.size() > 0)
                        {
                            for (Alarm a : noSyncAlarms)
                            {
                                alarmLocalAdd(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                        List<Alarm> noUptAlarms = DBHelper.getInstance().getNoUptLocalAlarms();
                        if (noUptAlarms != null && noUptAlarms.size() > 0)
                        {
                            for (Alarm a : noUptAlarms)
                            {
                                alarmLocalEdit(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }
    
    /**
     * 根据用户uuid获取用户信息
     * @param userUuid
     * @return
     */
    public void getUserByUuid(String userUuid,final Handler myHandler)
    {
        
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", userUuid);
        HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.GET_USER_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, myHandler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                User user = JSON.parseObject(response.getString("resInfo"), User.class);
                                data.putSerializable(ResponseCons.RESINFO, user);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    myHandler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 获取分享最后同步时间
     * @param uuid
     * @param mime
     */
    public void alarmShareGetLastSyncTime(final String userUuid, final String mime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", userUuid);
        params.put("mime", mime);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_GETSYNCTIME_POST_URL),
            params,
            new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                String resInfo = response.getString("resInfo");
                                com.alibaba.fastjson.JSONObject obj = JSON.parseObject(resInfo);
                                long shareSyncTime = obj.getLongValue("syncTime");
                                long localSyncTime = DBHelper.getInstance().getAlarmMaxSyncTime();
                                //本地同步时间小于服务器同步时间
                                if (localSyncTime < shareSyncTime)
                                {
                                    alarmShareSync(userUuid, mime, localSyncTime);
                                }
                            }
                        }
                        List<Alarm> noSyncAlarms = DBHelper.getInstance().getNoSyncShareAlarms();
                        if (noSyncAlarms != null && noSyncAlarms.size() > 0)
                        {
                            for (Alarm a : noSyncAlarms)
                            {
                                alarmShareAdd(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                        List<Alarm> noUptAlarms = DBHelper.getInstance().getNoUptShareAlarms();
                        if (noUptAlarms != null && noUptAlarms.size() > 0)
                        {
                            for (Alarm a : noUptAlarms)
                            {
                                alarmShareEdit(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }
    
    /**
     * 根据上一次同步时间获取之后的所有同步信息
     * @param uuid
     * @param mime
     * @param syncTime
     */
    public void alarmShareSync(String uuid, String mime, long syncTime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", uuid);
        params.put("mime", mime);
        params.put("syncTime", String.valueOf(syncTime));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_SYNC_POST_URL),
            params,
            new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                uptAlarms(response.getString("resInfo"));
                                Context context = MyApp.getInstance().getApplicationContext();
                                controller.setNextAlert();
                                Intent i = new Intent(context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                            
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }
    
    /**
     * 获取非分享提醒同步信息
     * @param userUuid
     * @param mime
     * @param localSyncTime
     */
    public void alarmLocalSync(final String userUuid, final String mime, long localSyncTime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", userUuid);
        params.put("mime", mime);
        params.put("syncTime", String.valueOf(localSyncTime));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_SYNC_POST_URL),
            params,
            new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                uptAlarms(response.getString("resInfo"));
                                Context context = MyApp.getInstance().getApplicationContext();
                                controller.setNextAlert();
                                Intent i = new Intent(context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
                
            });
    }
    
    private synchronized void uptAlarms(String json)
    {
        com.alibaba.fastjson.JSONArray arrs = JSONArray.parseArray(json);
        
        for (Object obj : arrs)
        {
            Alarm ps = JSON.parseObject(obj.toString(), Alarm.class);
            Alarm alarm = DBHelper.getInstance().getAlarmByUuidd(ps.getUuid());
            if (alarm != null)
            {
                ps.setId(alarm.getId());
                if (ps.getShare() != null || (ps.getShare() != null && !ps.getAllowChange()))
                {
                    DBHelper.getInstance().uptAlarm(ps);
                }
            }
            else
            {
                DBHelper.getInstance().addAlarm(ps);
            }
            for (Task psTask : ps.getTasks())
            {
                if (psTask != null)
                {
                    Task t = DBHelper.getInstance().getTaskByUuid(psTask.getUuid());
                    if (t != null)
                    {
                        psTask.setAlarm(ps);
                        psTask.setId(t.getId());
                        DBHelper.getInstance().uptTask(psTask);
                    }
                    else
                    {
                        psTask.setAlarm(ps);
                        DBHelper.getInstance().addTask(psTask);
                    }
                }
            }
            Alarm uptAlarm = DBHelper.getInstance().getAlarmById(ps.getId());
            uptAlarm.setTask(DBHelper.getInstance().getNextTaskByAlarmId(ps.getId()));
            DBHelper.getInstance().uptAlarm(uptAlarm);
            
        }
    }
    
    public void getSyncCategory(String uuid, String mime, int count)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", uuid);
        params.put("mime", mime);
        params.put("count", String.valueOf(count));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_GET_SYNC_CATEGORY_POST_URL),
            params,
            new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                String resInfo = response.getString("resInfo");
                                List<Category> cates = JSONArray.parseArray(resInfo, Category.class);
                                DBHelper.getInstance().clearCate();
                                for (Category cate : cates)
                                {
                                    DBHelper.getInstance().addCate(cate);
                                }
                                Context context = MyApp.getInstance().getApplicationContext();
                                Intent i = new Intent(context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }

    public void getMarketAlarm(final Handler handler,Long cateid, String scope, long startTime, long endTime, int page)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        
        RequestParams params = new RequestParams();
        if(user!=null)
        {
            params.put("userUuid", user.getUuid());
            params.put("mime", user.getMime());
        }
        params.put("cateId", String.valueOf(cateid));
        params.put("scope", scope);
        params.put("startTime", String.valueOf(startTime));
        params.put("endTime", String.valueOf(endTime));
        params.put("page", String.valueOf(page));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_GET_MARKET_ALARM_POST_URL),
            params,
            new MyJsonHttpResponseHandler(msg, data, handler)
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    try
                    {
                        int state = response.getInt("state");
                        String stateInfo = response.getString("stateInfo");
                        data.putInt(ResponseCons.STATE, state);
                        data.putString(ResponseCons.STATEINFO, stateInfo);
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            if (response.has("resInfo"))
                            {
                                String resInfo = response.getString("resInfo");
                                ArrayList list = new ArrayList();
                                List<Alarm> alarms = JSONArray.parseArray(resInfo, Alarm.class);
                                list.add(alarms);
                                data.putParcelableArrayList(ResponseCons.RESINFO, list);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
                        data.putString(ResponseCons.STATEINFO, e.getMessage());
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
}
