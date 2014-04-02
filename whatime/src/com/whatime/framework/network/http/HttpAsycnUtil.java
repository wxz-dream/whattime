package com.whatime.framework.network.http;

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
}