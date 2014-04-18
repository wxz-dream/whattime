package com.whatime.framework.network.http;

import java.io.File;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.whatime.framework.constant.Constants;

public class HttpAsycnUtil
{
    static HttpUtils http = new HttpUtils();
    
    public static void post(String urlString, RequestParams params, RequestCallBack<String> callBack)
    {
        http.configCurrentHttpCacheExpiry(1000 * 20);
        http.send(HttpRequest.HttpMethod.POST, urlString, params, callBack);
    }
    
    public static String getUrl(String action)
    {
        return Constants.URL + action;
    }
    
    public static void putFile(String urlString, File file, RequestParams params, RequestCallBack<String> callBack)
    {
        http.configCurrentHttpCacheExpiry(1000 * 20);
        params.addBodyParameter("file", file ,"multipart/form-data");
        http.send(HttpRequest.HttpMethod.POST, urlString, params, callBack);
    }
}