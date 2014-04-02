package com.whatime.framework.network.http;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;
import com.whatime.framework.constant.Constants;

public class HttpSycnUtil
{
    static HttpUtils http = new HttpUtils();
    
    public static String post(String urlString, RequestParams params)
    {
        http.configCurrentHttpCacheExpiry(1000 * 20);
        try
        {
            ResponseStream responseStream =
                http.sendSync(HttpRequest.HttpMethod.POST, urlString, params);
            return responseStream.readString();
        }
        catch (Exception e)
        {
        }
        return "";
    }
    
    public static String getUrl(String action)
    {
        return Constants.URL + action;
        
    }
}