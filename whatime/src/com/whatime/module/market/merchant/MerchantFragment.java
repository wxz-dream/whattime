package com.whatime.module.market.merchant;

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

public class MerchantFragment extends BaseFragment
{
    private String[] tabs;
    
    private MerchantPagerAdapter listViewPagerAdapter;
    
    private ScheduleScrollingTabsAdapter mScrollingTabsAdapter;
    
    public MerchantFragment()
    {
    }
    
    public MerchantFragment(Activity activity)
    {
        this.mActivity = activity;
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        tabs = getResources().getStringArray(R.array.tab_my_schedule);
        mTopTitleView.setText(getString(R.string.tab_merchant));
        mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
        
        listViewPagerAdapter = new MerchantPagerAdapter(this, tabs.length);
        mPager.setAdapter(listViewPagerAdapter);
        
        pagerItemList = new ArrayList<Fragment>();
        for (int i = 0; i < tabs.length; i++)
        {
            pagerItemList.add(new Fragment());
        }
        initScrollableTabs(view, mPager);
        
        return view;
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
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listViewPagerAdapter = null;
        mScrollingTabsAdapter = null;
    }
}
