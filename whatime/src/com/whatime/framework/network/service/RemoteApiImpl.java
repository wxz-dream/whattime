package com.whatime.framework.network.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.whatime.controller.center.AlarmController;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.constant.Constants;
import com.whatime.framework.network.http.BaseRequestCallBack;
import com.whatime.framework.network.http.HttpAsycnUtil;
import com.whatime.framework.network.http.HttpSycnUtil;
import com.whatime.framework.network.http.MD5;
import com.whatime.framework.network.http.MyFileRequestCallBack;
import com.whatime.framework.network.http.MyRequestCallBack;
import com.whatime.framework.network.pojo.ApkVersion;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.ui.activity.MainActivity;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.books.FriendReq;

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
        final User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUserName(userName);
        user.setPassword(MD5.GetMD5Code(password));
        user.setMime(SysUtil.getMime());
        user.setPhoneInfo(SysUtil.getPhoneInfo());
        user.setDel(false);
        user.setAvailable(false);
        params.addBodyParameter("user", JSON.toJSONString(user));
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        HttpAsycnUtil.post(HttpSycnUtil.getUrl(Constants.REGIST_USER_POST_URL), params, new MyRequestCallBack(msg,
            data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
                String stateInfo = response.getString("stateInfo");
                data.putInt(ResponseCons.STATE, state);
                data.putString(ResponseCons.STATEINFO, stateInfo);
                if (state == ResponseCons.STATE_SUCCESS)
                {
                    DBHelper.getInstance().clearUser();
                    DBHelper.getInstance().addUser(user);
                    DBHelper.getInstance().setAlarmUserUuid(user.getUuid());
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
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("oldPassword", MD5.GetMD5Code(oldPassword));
        params.addBodyParameter("newPassword", md5Pwd);
        HttpAsycnUtil.post(HttpSycnUtil.getUrl(Constants.MODIFY_USER_PASSWORD_POST_URL), params, new MyRequestCallBack(
            msg, data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        params.addBodyParameter("userName", userName);
        final String md5Pwd = MD5.GetMD5Code(password);
        params.addBodyParameter("password", md5Pwd);
        HttpAsycnUtil.post(HttpSycnUtil.getUrl(Constants.LOGIN_USER_POST_URL), params, new MyRequestCallBack(msg, data,
            handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("user", JSON.toJSONString(user));
        HttpAsycnUtil.post(HttpSycnUtil.getUrl(Constants.MODIFY_USER_POST_URL), params, new MyRequestCallBack(msg,
            data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        if (alarm.getTasks() == null || alarm.getTasks().size() == 0)
        {
            return;
        }
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("userLocalAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_ADD_POST_URL), params, new MyRequestCallBack(msg,
            data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        if (alarm.getTasks() == null || alarm.getTasks().size() == 0)
        {
            return;
        }
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("userShareAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_ADD_POST_URL), params, new MyRequestCallBack(msg,
            data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        if (alarm.getTasks() == null || alarm.getTasks().size() == 0)
        {
            return;
        }
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("userLocalAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_EDIT_POST_URL), params, new MyRequestCallBack(
            msg, data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        if (alarm.getTasks() == null || alarm.getTasks().size() == 0)
        {
            return;
        }
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", user.getUuid());
        params.addBodyParameter("mime", user.getMime());
        params.addBodyParameter("userShareAlarm", JSON.toJSONString(alarm));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_EDIT_POST_URL), params, new MyRequestCallBack(
            msg, data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
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
        params.addBodyParameter("userUuid", userUuid);
        params.addBodyParameter("mime", mime);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_GETSYNCTIME_POST_URL),
            params,
            new BaseRequestCallBack()
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
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
                            List<Task> ts = a.getTasks();
                            if (ts != null && ts.size() > 0)
                            {
                                alarmLocalAdd(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                    }
                    List<Alarm> noUptAlarms = DBHelper.getInstance().getNoUptLocalAlarms();
                    if (noUptAlarms != null && noUptAlarms.size() > 0)
                    {
                        for (Alarm a : noUptAlarms)
                        {
                            List<Task> ts = a.getTasks();
                            if (ts != null && ts.size() > 0)
                            {
                                alarmLocalEdit(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                    }
                }
            });
    }
    
    /**
     * 根据用户uuid获取用户信息
     * @param userUuid
     * @return
     */
    public User getUserByUuid(String userUuid)
    {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", userUuid);
        String json = HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.GET_USER_BY_UUID_POST_URL), params);
        User user = null;
        JSONObject response = JSON.parseObject(json);
        if (response == null)
        {
            return user;
        }
        int state = response.getInteger("state");
        if (state == ResponseCons.STATE_SUCCESS)
        {
            if (response.containsKey("resInfo"))
            {
                user = JSON.parseObject(response.getString("resInfo"), User.class);
            }
        }
        return user;
    }
    
    /**
     * 获取分享最后同步时间
     * @param uuid
     * @param mime
     */
    public void alarmShareGetLastSyncTime(final String userUuid, final String mime)
    {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", userUuid);
        params.addBodyParameter("mime", mime);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_GETSYNCTIME_POST_URL),
            params,
            new BaseRequestCallBack()
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
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
                            List<Task> ts = a.getTasks();
                            if (ts != null && ts.size() > 0)
                            {
                                alarmShareAdd(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
                    }
                    List<Alarm> noUptAlarms = DBHelper.getInstance().getNoUptShareAlarms();
                    if (noUptAlarms != null && noUptAlarms.size() > 0)
                    {
                        for (Alarm a : noUptAlarms)
                        {
                            List<Task> ts = a.getTasks();
                            if (ts != null && ts.size() > 0)
                            {
                                alarmShareEdit(MyApp.getInstance().getUser(), a, new Handler());
                            }
                        }
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
        params.addBodyParameter("userUuid", uuid);
        params.addBodyParameter("mime", mime);
        params.addBodyParameter("syncTime", String.valueOf(syncTime));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_SHARE_SYNC_POST_URL), params, new BaseRequestCallBack()
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
                if (state == ResponseCons.STATE_SUCCESS)
                {
                    if (response.containsKey("resInfo"))
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
        params.addBodyParameter("userUuid", userUuid);
        params.addBodyParameter("mime", mime);
        params.addBodyParameter("syncTime", String.valueOf(localSyncTime));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.ALARM_LOCAL_SYNC_POST_URL), params, new BaseRequestCallBack()
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
                if (state == ResponseCons.STATE_SUCCESS)
                {
                    if (response.containsKey("resInfo"))
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
            
        });
    }
    
    /**
     * 修改提醒
     * @param json
     */
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
    
    /**
     * 获取同步分类
     * @param uuid
     * @param mime
     * @param count
     */
    public void getSyncCategory(String uuid, String mime, int count)
    {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userUuid", uuid);
        params.addBodyParameter("mime", mime);
        params.addBodyParameter("count", String.valueOf(count));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_GET_SYNC_CATEGORY_POST_URL),
            params,
            new BaseRequestCallBack()
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
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
            });
    }
    
    /**
     * 获取商城提醒集合
     * @param handler
     * @param cateid
     * @param scope
     * @param startTime
     * @param endTime
     * @param page
     */
    public void getMarketAlarm(final Handler handler, Long cateid, String scope, long startTime, long endTime, int page)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("cateId", String.valueOf(cateid));
        params.addBodyParameter("scope", scope);
        params.addBodyParameter("startTime", String.valueOf(startTime));
        params.addBodyParameter("endTime", String.valueOf(endTime));
        params.addBodyParameter("page", String.valueOf(page));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_GET_MARKET_ALARM_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String resInfo = response.getString("resInfo");
                            ArrayList list = new ArrayList();
                            List<Alarm> alarms = JSONArray.parseArray(resInfo, Alarm.class);
                            list.add(alarms);
                            data.putParcelableArrayList(ResponseCons.RESINFO, list);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 根据userName获取用户信息
     * @param userName
     * @param handler
     */
    public void getUserByUserName(String userName, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("userName", userName);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.GET_USER_BY_USERNAME_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            data.putSerializable(ResponseCons.RESINFO,
                                JSON.parseObject(response.getString("resInfo"), User.class));
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 参加一个活动
     * @param alarmUuid
     * @param handler
     */
    public void joinAlarm(String alarmUuid, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("alarmUuid", alarmUuid);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_JOIN_ALARM_POST_URL), params, new MyRequestCallBack(
            msg, data, handler)
        {
            @Override
            public void onSuccess(ResponseInfo<String> json)
            {
                JSONObject response = JSON.parseObject(json.result);
                if (response == null)
                {
                    return;
                }
                int state = response.getInteger("state");
                String stateInfo = response.getString("stateInfo");
                data.putInt(ResponseCons.STATE, state);
                data.putString(ResponseCons.STATEINFO, stateInfo);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }
    
    /**
     * 添加好友申请
     * @param uuid
     * @param remark
     * @param handler
     */
    public void addFriendReq(String uuid, String remark, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("friendUuid", uuid);
        params.addBodyParameter("remark", remark);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_ADD_FRIEND_REQ_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 操作好友申请
     * @param friendReqUuid
     * @param access
     * @param uuid
     * @param handler
     */
    public void operaMyFriendReq(int access, String uuid, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("access", String.valueOf(access));
        params.addBodyParameter("uuid", uuid);
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_ADD_OPERA_FRIENDS_REQ_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 获取好友申请集合
     * @param handler
     */
    public void findMyAddFriendsReq(final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_ADD_FIND_FRIENDS_REQ_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String resInfo = response.getString("resInfo");
                            ArrayList list = new ArrayList();
                            List<FriendReq> reqs = JSONArray.parseArray(resInfo, FriendReq.class);
                            list.add(reqs);
                            data.putParcelableArrayList(ResponseCons.RESINFO, list);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 获取好友列表
     * @param handler
     */
    public void findMyFriends(final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_ADD_FIND_MY_FRIENDS_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String resInfo = response.getString("resInfo");
                            ArrayList list = new ArrayList();
                            List<User> reqs = JSONArray.parseArray(resInfo, User.class);
                            list.add(reqs);
                            data.putParcelableArrayList(ResponseCons.RESINFO, list);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 获取好友列表同步接口
     * @return
     */
    public List<User> findMyFriends()
    {
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        String json = HttpSycnUtil.post(HttpSycnUtil.getUrl(Constants.MARKET_ADD_FIND_MY_FRIENDS_POST_URL), params);
        JSONObject response = JSON.parseObject(json);
        List<User> friends = new ArrayList<User>();
        if (response == null)
        {
            return friends;
        }
        int state = response.getInteger("state");
        if (state == ResponseCons.STATE_SUCCESS)
        {
            if (response.containsKey("resInfo"))
            {
                String resInfo = response.getString("resInfo");
                friends = JSONArray.parseArray(resInfo, User.class);
            }
        }
        return friends;
    }
    
    public void getManAlarm(final Handler handler, String userUuid, long startTime, long endTime, int page)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x002;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("manUuid", userUuid);
        params.addBodyParameter("startTime", String.valueOf(startTime));
        params.addBodyParameter("endTime", String.valueOf(endTime));
        params.addBodyParameter("page", String.valueOf(page));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_GET_MAN_ALARM_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String resInfo = response.getString("resInfo");
                            ArrayList list = new ArrayList();
                            List<Alarm> alarms = JSONArray.parseArray(resInfo, Alarm.class);
                            list.add(alarms);
                            data.putParcelableArrayList(ResponseCons.RESINFO, list);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    public void findUserFriendsAlarms(final Handler handler, long startTime, long endTime, int page)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x002;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("startTime", String.valueOf(startTime));
        params.addBodyParameter("endTime", String.valueOf(endTime));
        params.addBodyParameter("page", String.valueOf(page));
        HttpAsycnUtil.post(HttpAsycnUtil.getUrl(Constants.MARKET_FIND_FRIENDS_ALARMS_POST_URL),
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String resInfo = response.getString("resInfo");
                            ArrayList list = new ArrayList();
                            List<Alarm> alarms = JSONArray.parseArray(resInfo, Alarm.class);
                            list.add(alarms);
                            data.putParcelableArrayList(ResponseCons.RESINFO, list);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    public void putUserPhotoFile(File photoFile, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x003;
        final User user = MyApp.getInstance().getUser();
        RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        params.addBodyParameter("file", photoFile, "image/png");
        params.addBodyParameter("fileType", "img");
        HttpAsycnUtil.putFile(HttpAsycnUtil.getUrl(Constants.SYSTEM_PUT_FILE_URL),
            photoFile,
            params,
            new MyRequestCallBack(msg, data, handler)
            {
                @Override
                public void onSuccess(ResponseInfo<String> json)
                {
                    JSONObject response = JSON.parseObject(json.result);
                    if (response == null)
                    {
                        return;
                    }
                    int state = response.getInteger("state");
                    String stateInfo = response.getString("stateInfo");
                    data.putInt(ResponseCons.STATE, state);
                    data.putString(ResponseCons.STATEINFO, stateInfo);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        if (response.containsKey("resInfo"))
                        {
                            String photoUri = response.getString("resInfo");
                            user.setUserphotoUri(photoUri);
                            DBHelper.getInstance().uptUser(user);
                            MyApp.getInstance().setUser(user);
                        }
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
    }
    
    /**
     * 获取版本信息
     * @return
     */
    public ApkVersion updateAppVersion()
    {
        final User user = MyApp.getInstance().getUser();
        final RequestParams params = new RequestParams();
        if (user != null)
        {
            params.addBodyParameter("userUuid", user.getUuid());
            params.addBodyParameter("mime", user.getMime());
        }
        String json = HttpSycnUtil.post(HttpAsycnUtil.getUrl(Constants.SYSTEM_CHECK_VERSION_URL), params);
        JSONObject response = JSON.parseObject(json);
        if (response == null)
        {
            return null;
        }
        int state = response.getInteger("state");
        if (state == ResponseCons.STATE_SUCCESS)
        {
            if (response.containsKey("resInfo"))
            {
                String verStr = response.getString("resInfo");
                ApkVersion version = JSON.parseObject(verStr, ApkVersion.class);
                return version;
            }
        }
        return null;
    }
    
    /**
     * 获取更新
     * @param url
     * @param path
     * @param handler
     */
    public void getApk(String url, String path, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        final RequestParams params = new RequestParams();
        HttpAsycnUtil.getFile(url, path, params, new MyFileRequestCallBack(msg, data, handler)
        {
            
            @Override
            public void onSuccess(ResponseInfo<File> json)
            {
                data.putInt(ResponseCons.STATE, ResponseCons.STATE_SUCCESS);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }
}
