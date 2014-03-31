package com.whatime.framework.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.whatime.framework.ui.activity.MainActivity;
import com.whatime.framework.ui.view.ScrollableTabView;

public class BaseFragment extends Fragment implements ViewPager.OnPageChangeListener
{
    
    protected View showLeft;
    
    protected View showRight;
    
    protected View showAdd;
    
    protected ImageView leftBt;
    
    protected ImageView rightBt;
    
    //标题
    protected TextView mTopTitleView;
    
    //图片
    protected ImageView mTopBackView;
    
    protected ViewPager mPager;
    
    protected ArrayList<Fragment> pagerItemList = null;
    
    protected Context mContext;
    protected Activity mActivity;
    
    protected ScrollableTabView mScrollableTabView;
    public BaseFragment()
    {
    }
    public ViewPager getViewPage()
    {
        return mPager;
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        
        super.onActivityCreated(savedInstanceState);
        
        leftBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).showLeft();
            }
        });
        
        rightBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).showRight();
            }
        });
    }
    
    public boolean isFirst()
    {
        if (mPager.getCurrentItem() == 0)
            return true;
        else
            return false;
    }
    
    public boolean isEnd()
    {
        if (mPager.getCurrentItem() == pagerItemList.size() - 1)
            return true;
        else
            return false;
    }
    
    public class MyAdapter extends FragmentPagerAdapter
    {
        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }
        
        @Override
        public int getCount()
        {
            return pagerItemList.size();
        }
        
        @Override
        public Fragment getItem(int position)
        {
            
            Fragment fragment = null;
            if (position < pagerItemList.size())
                fragment = pagerItemList.get(position);
            else
                fragment = pagerItemList.get(0);
            
            return fragment;
            
        }
    }
    
    protected MyPageChangeListener myPageChangeListener;
    
    public void setMyPageChangeListener(MyPageChangeListener l)
    {
        myPageChangeListener = l;
        
    }
    
    public interface MyPageChangeListener
    {
        public void onPageSelected(int position);
    }
    
    @Override
    public void onPageScrollStateChanged(int position)
    {
        
    }
    
    @Override
    public void onPageScrolled(int position, float arg1, int arg2)
    {
        
    }
    
    @Override
    public void onPageSelected(int position)
    {
        if (myPageChangeListener != null)
        {
            myPageChangeListener.onPageSelected(position);
        }
        if (mScrollableTabView != null)
        {
            mScrollableTabView.selectTab(position);
        }
        
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        pagerItemList.clear();
        pagerItemList = null;
        mScrollableTabView = null;
        mActivity = null;
    }
}
