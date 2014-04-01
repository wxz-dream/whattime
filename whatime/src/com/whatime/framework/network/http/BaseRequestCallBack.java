package com.whatime.framework.network.http;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class BaseRequestCallBack extends RequestCallBack<String>
{

    @Override
    public void onFailure(HttpException arg0, String arg1)
    {
    }

    @Override
    public void onSuccess(ResponseInfo<String> arg0)
    {
    }
}
