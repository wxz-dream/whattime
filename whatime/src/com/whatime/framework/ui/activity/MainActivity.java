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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.framework.aservice.AlarmCenterAService;
import com.whatime.framework.ui.fragment.BaseFragment;
import com.whatime.framework.ui.fragment.BaseFragment.MyPageChangeListener;
import com.whatime.framework.ui.fragment.IChangeFragment;
import com.whatime.framework.ui.fragment.LeftFragment;
import com.whatime.framework.ui.fragment.RightFragment;
import com.whatime.framework.ui.view.SlidingMenu;
import com.whatime.module.market.game.GameFragment;
import com.whatime.module.market.health.HealthLiftFragment;
import com.whatime.module.market.movies.MoviesFragment;
import com.whatime.module.market.other.OtherFragment;
import com.whatime.module.market.recreation.RecreationFragment;
import com.whatime.module.market.school.SchoolFragment;
import com.whatime.module.market.travel.TravelFragment;
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
    
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        init();
        initListener(newsFragment);
        startService(new Intent(MainActivity.this,AlarmCenterAService.class));
        AlarmController.sync(this);
    }
    
    private void init()
    {
        
        mSlidingMenu = (SlidingMenu)findViewById(R.id.slidingMenu);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        mSlidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.left_frame, null),(int)(mScreenWidth*0.8));
        mSlidingMenu.setRightView(getLayoutInflater().inflate(R.layout.right_frame, null),(int)(mScreenWidth*0.8));
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
                fragment = new HealthLiftFragment(this,2);
                break;
            case 2:
                fragment = new RecreationFragment(this,3);
                break;
            case 3:
                fragment = new TravelFragment(this,4);
                break;
            case 4:
                fragment = new MoviesFragment(this,5);
                break;
            case 5:
                fragment = new GameFragment(this,6);
                break;
            case 6:
                fragment = new SchoolFragment(this,7);
                break;
            case 7:
                fragment = new OtherFragment(this,8);
                break;
            default:
                break;
        
        }
        initListener(fragment);
        t.replace(R.id.center_frame, fragment);
        t.commit();
    }
}
