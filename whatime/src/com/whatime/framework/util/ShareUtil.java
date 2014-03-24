package com.whatime.framework.util;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.whatime.framework.application.MyApp;

public class ShareUtil
{
    
    /**
     * 分享
     * @param share
     */
    public static void shareAll(String share)
    {
        Context context = MyApp.getInstance().getApplicationContext();
        ShareSDK.initSDK(context);
        
        
        
     // 获取平台列表
        Platform[] tmp = ShareSDK.getPlatformList(context);
        for (Platform wb : tmp)
        {
            if (wb.isValid())
            {
                String name = wb.getName();
                if (SinaWeibo.NAME.equals(name))
                {
                    
                }
                if (QZone.NAME.equals(name))
                {
                   
                }
                if (Renren.NAME.equals(name))
                {
                    
                }
                if (TencentWeibo.NAME.equals(name))
                {
                    
                }
            }
        }
        
        
        
        ShareSDK.stopSDK(context);
    }
}
