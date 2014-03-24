package com.whatime.framework.network.service;

import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
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
import com.whatime.framework.network.pojo.AlarmParser;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.util.SysUtil;

public class RemoteApiImpl
{
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
        final String uuid = UUID.randomUUID().toString();
        final String md5Pwd = MD5.GetMD5Code(password);
        final String mime = SysUtil.getMime();
        final String phoneInfo = SysUtil.getPhoneInfo();
        params.put("uuid", uuid);
        params.put("userName", userName);
        params.put("password", md5Pwd);
        params.put("mime", mime);
        params.put("phoneInfo", phoneInfo);
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
                        User user = new User();
                        user.setUuid(uuid);
                        user.setUserName(userName);
                        user.setPassword(md5Pwd);
                        user.setMime(mime);
                        user.setPhoneInfo(phoneInfo);
                        Resources r = MyApp.getInstance().getApplicationContext().getResources();
                        Uri uri =
                            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                + r.getResourcePackageName(R.drawable.icon) + "/"
                                + r.getResourceTypeName(R.drawable.icon) + "/"
                                + r.getResourceEntryName(R.drawable.icon));
                        user.setUserphotoUri(uri.toString());
                        DBHelper.getInstance().addUser(user);
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
        params.put("uuid", user.getUuid());
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
                        User user = MyApp.getInstance().getUser();
                        if (user == null)
                        {
                            user = new User();
                        }
                        else
                        {
                            user.setUuid(user.getUuid());
                        }
                        user.setUserName(userName);
                        user.setPassword(md5Pwd);
                        if(MyApp.getInstance().getUser()==null)
                        {
                            DBHelper.getInstance().addUser(user);
                        }
                        else
                        {
                            DBHelper.getInstance().uptUser(user);
                        }
                        MyApp.getInstance().setUser(user);
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
        params.put("uuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("nickName", user.getNickName());
        params.put("sex", user.getSex());
        params.put("userphotoUri", user.getUserphotoUri());
        params.put("city", user.getCity());
        params.put("uptTime", user.getUptTime());
        params.put("available", user.getAvailable());
        params.put("levelUuid", user.getLevelUuid());
        params.put("email", user.getEmail());
        params.put("realName", user.getRealName());
        params.put("telphone", user.getTelphone());
        params.put("identityCard", user.getIdentityCard());
        params.put("qq", user.getQq());
        params.put("authType", user.getAuthType());
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
    public void alrmLocalAdd(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userLocalAlarm", JSON.toJSON(alarm));
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
                            alarm.setSyncTime(System.currentTimeMillis());
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
    public void alrmShareAdd(final User user, final Alarm alarm, final Handler handler)
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
                            AlarmParser resAlarm = (AlarmParser)JSON.parseObject(response.getString("resInfo"), AlarmParser.class);
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            alarm.setJoinNum(resAlarm.getJoinNum());
                            alarm.setUptTime(resAlarm.getUptTime());
                            for(Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
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
    public void alrmLocalEdit(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userLocalAlarm", JSON.toJSON(alarm));
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
                            alarm.setSyncTime(System.currentTimeMillis());
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
    public void alrmShareEdit(final User user, final Alarm alarm, final Handler handler)
    {
        final Message msg = new Message();
        final Bundle data = new Bundle();
        msg.what = 0x001;
        RequestParams params = new RequestParams();
        params.put("userUuid", user.getUuid());
        params.put("mime", user.getMime());
        params.put("userShareAlarm", JSON.toJSON(alarm));
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
                            AlarmParser resAlarm = (AlarmParser)JSON.parseObject(response.getString("resInfo"), AlarmParser.class);
                            alarm.setSyncTime(resAlarm.getSyncTime());
                            alarm.setJoinNum(resAlarm.getJoinNum());
                            alarm.setUptTime(resAlarm.getUptTime());
                            for(Task t : alarm.getTasks())
                            {
                                t.setSyncTime(resAlarm.getSyncTime());
                                t.setUptTime(resAlarm.getUptTime());
                            }
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
     * 根据用户uuid获取用户信息
     * @param userUuid
     * @return
     */
    public User getUserByUuid(String userUuid)
    {
        
        return null;
    }
    
    /**
     * 获取分享最后同步时间
     * @param uuid
     * @param mime
     */
    public void alrmShareGetLastSyncTime(final String uuid, final String mime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", uuid);
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
                        String resInfo = response.getString("resInfo");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(resInfo);
                            long shareSyncTime = obj.getLongValue("syncTime");
                            long localSyncTime = DBHelper.getInstance().getAlarmMaxSyncTime();
                            //本地同步时间小于服务器同步时间
                            if (localSyncTime < shareSyncTime)
                            {
                                alrmShareSync(uuid, mime, localSyncTime);
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
    public void alrmShareSync(String uuid, String mime, long syncTime)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", uuid);
        params.put("mime", mime);
        params.put("syncTime", syncTime);
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
                        String resInfo = response.getString("resInfo");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(resInfo);
                            
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }

    public void getSyncCategory(String uuid, String mime, int count)
    {
        RequestParams params = new RequestParams();
        params.put("userUuid", uuid);
        params.put("mime", mime);
        params.put("count", count);
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
                        String resInfo = response.getString("resInfo");
                        if (state == ResponseCons.STATE_SUCCESS)
                        {
                            List<Category> cates = JSONArray.parseArray(resInfo, Category.class);
                            
                        }
                    }
                    catch (JSONException e)
                    {
                    }
                }
            });
    }
    
}
