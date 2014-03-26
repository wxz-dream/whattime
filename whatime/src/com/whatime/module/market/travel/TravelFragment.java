package com.whatime.module.market.travel;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatime.R;
import com.whatime.framework.ui.fragment.MarketFragment;

public class TravelFragment extends MarketFragment {
	
    private int page;
    public TravelFragment()
    {
    }
    
    public TravelFragment(Activity activity, int page)
    {
        this.mActivity = activity;
        this.page = page;
    }
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	    View view = super.onCreateView(inflater, container, savedInstanceState);
        mTopTitleView.setText(getString(R.string.tab_travel));
        mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
        return view;
	}


}
