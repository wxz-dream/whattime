package com.whatime.framework.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatime.R;

public class BaseMarketFragment extends BaseFragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContext = inflater.getContext();
        View mView = inflater.inflate(R.layout.market_view_pager, null);
        showLeft = (View)mView.findViewById(R.id.head_layout_showLeft);
        showRight = (View)mView.findViewById(R.id.head_layout_showRight);
        leftBt = (ImageView)showLeft.findViewById(R.id.head_layout_back);
        rightBt = (ImageView)showRight.findViewById(R.id.head_layout_me);
        
        mTopTitleView = (TextView)showLeft.findViewById(R.id.head_layout_text);
        mTopTitleView.setText(getString(R.string.tab_schedule));
        mTopBackView = (ImageView)showLeft.findViewById(R.id.head_layout_back);
        mTopBackView.setBackgroundResource(R.drawable.showright_selector);
        mPager = (ViewPager)mView.findViewById(R.id.vp_list);
        //展示可下拉刷新列表
        pagerItemList = new ArrayList<Fragment>();
        mPager.setOnPageChangeListener(this);
        return mView;
    }
}
