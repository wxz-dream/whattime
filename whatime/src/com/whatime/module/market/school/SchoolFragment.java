package com.whatime.module.market.school;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatime.R;
import com.whatime.framework.ui.fragment.MarketFragment;

public class SchoolFragment extends MarketFragment {
    
    private int page;
    public SchoolFragment()
    {
    }
    
    public SchoolFragment(Activity activity, int page)
    {
        this.mActivity = activity;
        this.page = page;
    }
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	    View view = super.onCreateView(inflater, container, savedInstanceState);
        mTopTitleView.setText(getString(R.string.tab_school));
        mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
        return view;
	}


}
