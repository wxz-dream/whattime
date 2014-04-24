/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whatime.framework.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.framework.network.pojo.ApkVersion;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.ui.fragment.BaseFragment;
import com.whatime.framework.ui.fragment.BaseFragment.MyPageChangeListener;
import com.whatime.framework.ui.fragment.IChangeFragment;
import com.whatime.framework.ui.fragment.LeftFragment;
import com.whatime.framework.ui.fragment.RightFragment;
import com.whatime.framework.ui.view.SlidingMenu;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.market.MarketFragment;
import com.whatime.module.schedule.fragment.ScheduleFragment;

/**
 * 主界面滑动机制基础类
 * @author devilangelxpf
 *
 */
public class MainActivity extends FragmentActivity implements IChangeFragment
{
    
    private static final String TAG = "SlidingActivity";
    
    SlidingMenu mSlidingMenu;
    
    LeftFragment leftFragment;
    
    RightFragment rightFragment;
    
    ScheduleFragment newsFragment;
    
    private Context context;
    
    private AlarmController controller = new AlarmController();
    
    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            int state = msg.getData().getInt(ResponseCons.STATE);
            switch (msg.what)
            {
                case 0x001:
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        Toast toast = Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        SysUtil.installApp(context);
                    }
                    break;
                case 0x002:
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        final ApkVersion version = (ApkVersion)msg.getData().get(ResponseCons.RESINFO);
                        if (version != null)
                        {
                            final String path = Environment.getExternalStorageDirectory() + "/ttyy/ttyy.apk";
                            String myVersion = SysUtil.getAppVersionName();
                            String serverVersion = version.getVersion();
                            if (!myVersion.equals(serverVersion))
                            {
                                Long size = version.getSize() / (1024 * 1024);
                                StringBuilder sb = new StringBuilder();
                                sb.append("当前版本： ")
                                    .append(myVersion)
                                    .append("\n")
                                    .append("更新版本： ")
                                    .append(serverVersion)
                                    .append("\n")
                                    .append("新版本大小： ")
                                    .append(size)
                                    .append("MB")
                                    .append("\n")
                                    .append("更新信息： ")
                                    .append(version.getDes());
                                new AlertDialog.Builder(context).setTitle("发现新版本")
                                    .setMessage(sb)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface d, int w)
                                        {
                                            controller.getApk(version.getUrl(), path, myHandler);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show();
                            }
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        init();
        initListener(newsFragment);
        controller.checkVersion(myHandler);
        Log.e("xpf", "--------onCreate----------");
    }
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("xpf", "--------onResume----------");
    }
    
    private void init()
    {
        mSlidingMenu = (SlidingMenu)findViewById(R.id.slidingMenu);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        mSlidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.left_frame, null), (int)(mScreenWidth * 0.8));
        mSlidingMenu.setRightView(getLayoutInflater().inflate(R.layout.right_frame, null), (int)(mScreenWidth * 0.8));
        mSlidingMenu.setCenterView(getLayoutInflater().inflate(R.layout.center_frame, null));
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        leftFragment = new LeftFragment();
        leftFragment.setChangeFragmentListener(this);
        t.replace(R.id.left_frame, leftFragment);
        rightFragment = new RightFragment();
        t.replace(R.id.right_frame, rightFragment);
        newsFragment = new ScheduleFragment(this);
        t.replace(R.id.center_frame, newsFragment);
        t.commit();
    }
    
    private void initListener(final BaseFragment fragment)
    {
        fragment.setMyPageChangeListener(new MyPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                Log.e(TAG, "onPageSelected : " + position);
                if (fragment.isFirst())
                {
                    mSlidingMenu.setCanSliding(true, false);
                }
                else if (fragment.isEnd())
                {
                    mSlidingMenu.setCanSliding(false, true);
                }
                else
                {
                    mSlidingMenu.setCanSliding(false, false);
                }
            }
        });
    }
    
    public void showLeft()
    {
        mSlidingMenu.showLeftView();
    }
    
    public void showRight()
    {
        mSlidingMenu.showRightView();
    }
    
    @Override
    public void changeFragment(int position)
    {
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new ScheduleFragment(this);
                break;
            case 1:
                fragment = new MarketFragment(this, 1);
                break;
            case 2:
                fragment = new MarketFragment(this, 2);
                break;
            case 3:
                fragment = new MarketFragment(this, 3);
                break;
            case 4:
                fragment = new MarketFragment(this, 4);
                break;
            case 5:
                fragment = new MarketFragment(this, 5);
                break;
            case 6:
                fragment = new MarketFragment(this, 6);
                break;
            case 7:
                fragment = new MarketFragment(this, 7);
                break;
            default:
                break;
        
        }
        initListener(fragment);
        t.replace(R.id.center_frame, fragment);
        t.commit();
    }
}
