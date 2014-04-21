package com.whatime.framework.network.http;

import java.io.File;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class BaseFileRequestCallBack extends RequestCallBack<File>
{

    @Override
    public void onFailure(HttpException arg0, String arg1)
    {
    }

    @Override
    public void onSuccess(ResponseInfo<File> arg0)
    {
    }
}
