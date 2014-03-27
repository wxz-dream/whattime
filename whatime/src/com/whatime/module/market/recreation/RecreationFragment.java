package com.whatime.module.market.recreation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatime.R;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.framework.ui.adapter.MarketScrollingTabsAdapter;
import com.whatime.framework.ui.fragment.MarketFragment;
import com.whatime.framework.ui.view.ScrollableTabView;

public class RecreationFragment extends MarketFragment
{
    private int page;
    private Category cate;
    
    private List<Category> cates;
    
    private RecreationPagerAdapter listViewPagerAdapter;
    
    private MarketScrollingTabsAdapter mScrollingTabsAdapter;
    
    public RecreationFragment()
    {
    }
    
    public RecreationFragment(Activity activity,int page)
    {
        this.mActivity = activity;
        this.page = page;
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        cate = DBHelper.getInstance().getcaById(page);
        mTopTitleView.setText(cate.getDesc());
        mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
        cates = DBHelper.getInstance().getcateByParentId(page);
        listViewPagerAdapter = new RecreationPagerAdapter(this, cates);
        mPager.setAdapter(listViewPagerAdapter);
        
        pagerItemList = new ArrayList<Fragment>();
        for (int i = 0; i < cates.size(); i++)
        {
            pagerItemList.add(new Fragment());
        }
        initScrollableTabs(view, mPager);
        
        return view;
    }
    
    protected void initScrollableTabs(View view, ViewPager mViewPager)
    {
        mScrollableTabView = (ScrollableTabView)view.findViewById(R.id.scrollabletabview);
        mScrollingTabsAdapter = new MarketScrollingTabsAdapter(mActivity, cates);
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
