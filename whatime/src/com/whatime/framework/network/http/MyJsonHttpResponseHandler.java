package com.whatime.framework.network.http;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.whatime.framework.network.pojo.ResponseCons;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler
{
    Message msg;
    Bundle data;
    Handler handler;
    
    public MyJsonHttpResponseHandler(Message msg,Bundle data,Handler handler)
    {
        this.msg = msg;
        this.data = data;
        this.handler = handler;
    }
    
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable error, String content)
    {
        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
        data.putString(ResponseCons.STATEINFO, content);
        msg.setData(data);
        handler.sendMessage(msg);
    }
    @Override
    public void onFailure(Throwable error)
    {
        super.onFailure(error);
        data.putInt(ResponseCons.STATE, ResponseCons.STATE_EXCEPTION);
        data.putString(ResponseCons.STATEINFO, "连接失败");
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
