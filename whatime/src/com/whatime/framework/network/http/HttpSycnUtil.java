package com.whatime.framework.network.http;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.whatime.framework.constant.Constants;

public class HttpSycnUtil
{
    private static SyncHttpClient client = new SyncHttpClient(); //实例话对象
    static
    {
        client.setTimeout(11000); //设置链接超时，如果不设置，默认为10s
    }
    
    public static void get(String urlString, ResponseHandlerInterface res) //用一个完整url获取一个string对象
    {
        client.get(urlString, res);
    }
    public static void post(String urlString, ResponseHandlerInterface res) //用一个完整url获取一个string对象
    {
        client.post(urlString, res);
    }
    
    public static void get(String urlString, RequestParams params, ResponseHandlerInterface res) //url里面带参数
    {
        client.get(urlString, params, res);
    }
    
    public static void get(String urlString, JsonHttpResponseHandler res) //不带参数，获取json对象或者数组
    {
        client.get(urlString, res);
    }
    
    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) //带参数，获取json对象或者数组
    {
        client.get(urlString, params, res);
    }
    public static void post(String urlString, RequestParams params, JsonHttpResponseHandler res) //带参数，获取json对象或者数组
    {
        client.post(urlString, params, res);
    }
    
    public static void get(String uString, BinaryHttpResponseHandler bHandler) //下载数据使用，会返回byte数据
    {
        client.get(uString, bHandler);
    }
    
    public static SyncHttpClient getClient()
    {
        return client;
    }
    public static String getUrl(String action)
    {
        return Constants.URL+action;
            
    }
}