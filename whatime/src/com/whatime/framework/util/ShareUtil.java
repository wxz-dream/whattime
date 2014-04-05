package com.whatime.framework.util;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.whatime.db.Alarm;
import com.whatime.framework.application.MyApp;
import com.whatime.module.thirdLogin.MyPlatformActionListener;

public class ShareUtil
{
    final Context context = MyApp.getInstance().getApplicationContext();
    /**
     * 分享
     * @param share
     * @param alarm 
     * @param context 
     */
    public void shareAll(String share, Alarm alarm)
    {
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
                    cn.sharesdk.sina.weibo.SinaWeibo.ShareParams sp = new cn.sharesdk.sina.weibo.SinaWeibo.ShareParams();
                    sp.setText("测试分享的文本");
                    // 执行图文分享
                    wb.setPlatformActionListener(new MyPlatformActionListener());
                    wb.share(sp);
                }
                if (QZone.NAME.equals(name))
                {
                    cn.sharesdk.tencent.qzone.QZone.ShareParams sp = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
                    sp.setTitle("测试分享的标题");
                    sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
                    sp.setText("测试分享的文本");
                    sp.setSite("http://sharesdk.cn");
                    sp.setSiteUrl("http://sharesdk.cn");
                    // 执行图文分享
                    wb.setPlatformActionListener(new MyPlatformActionListener());
                    wb.share(sp);
                }
                if (Renren.NAME.equals(name))
                {
                    cn.sharesdk.renren.Renren.ShareParams sp = new cn.sharesdk.renren.Renren.ShareParams();
                    sp.setText("测试分享的文本");
                    // 执行图文分享
                    wb.setPlatformActionListener(new MyPlatformActionListener());
                    wb.share(sp);
                }
                if (TencentWeibo.NAME.equals(name))
                {
                    cn.sharesdk.tencent.weibo.TencentWeibo.ShareParams sp = new cn.sharesdk.tencent.weibo.TencentWeibo.ShareParams();
                    sp.setText("测试分享的文本");
                    // 执行图文分享
                    wb.setPlatformActionListener(new MyPlatformActionListener());
                    wb.share(sp);
                }
            }
        }
        
        ShareSDK.stopSDK(context);
    }
}
