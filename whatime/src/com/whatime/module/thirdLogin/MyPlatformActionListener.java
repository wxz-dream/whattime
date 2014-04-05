package com.whatime.module.thirdLogin;

import java.util.HashMap;

import android.util.Log;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class MyPlatformActionListener implements PlatformActionListener
{

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashmap)
    {
        Log.e("xpf", "onComplete");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        Log.e("xpf", "onError");
        System.out.println(throwable.getStackTrace());
    }

    @Override
    public void onCancel(Platform platform, int i)
    {
        Log.e("xpf", "onCancel");
        
    }
    
}