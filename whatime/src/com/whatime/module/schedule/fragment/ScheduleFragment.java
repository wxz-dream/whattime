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
package com.whatime.module.schedule.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatime.R;
import com.whatime.framework.ui.adapter.ScheduleScrollingTabsAdapter;
import com.whatime.framework.ui.fragment.BaseFragment;
import com.whatime.framework.ui.view.ScrollableTabView;
import com.whatime.module.schedule.adapter.SchedulePagerAdapter;

public class ScheduleFragment extends BaseFragment
{
    private String[] tabs;
    
    private SchedulePagerAdapter listViewPagerAdapter;
    
    private ScheduleScrollingTabsAdapter mScrollingTabsAdapter;
    
    public ScheduleFragment()
    {
    }
    
    public ScheduleFragment(Activity activity)
    {
        this.mActivity = activity;
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View mView = super.onCreateView(inflater, container, savedInstanceState);
        tabs = getResources().getStringArray(R.array.tab_my_schedule);
        //展示可下拉刷新列表
        listViewPagerAdapter = new SchedulePagerAdapter(this,tabs.length);
        mPager.setAdapter(listViewPagerAdapter);
        
        pagerItemList = new ArrayList<Fragment>();
        for (int i = 0; i < tabs.length; i++)
        {
            pagerItemList.add(new Fragment());
        }
        initScrollableTabs(mView, mPager);
        return mView;
    }
    
    protected void initScrollableTabs(View view, ViewPager mViewPager)
    {
        mScrollableTabView = (ScrollableTabView)view.findViewById(R.id.scrollabletabview);
        mScrollingTabsAdapter = new ScheduleScrollingTabsAdapter(mActivity, tabs);
        mScrollableTabView.setAdapter(mScrollingTabsAdapter);
        mScrollableTabView.setViewPage(mViewPager);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        listViewPagerAdapter.notifyDataChange();
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listViewPagerAdapter = null;
        mScrollingTabsAdapter = null;
    }
}
