package com.whatime.framework.network.http;

import java.io.File;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.whatime.framework.network.pojo.ResponseCons;

public class MyFileRequestCallBack extends RequestCallBack<File>
{
    Message msg;
    Bundle data;
    Handler handler;
    
    public MyFileRequestCallBack(Message msg,Bundle data,Handler handler)
    {
        this.msg = msg;
        this.data = data;
        this.handler = handler;
    }

    @Override
    public void onFailure(HttpException arg0, String arg1)
    {
        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
        data.putString(ResponseCons.STATEINFO, arg1);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    @Override
    public void onSuccess(ResponseInfo<File> arg0)
    {
    }

}
