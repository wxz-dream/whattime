package com.whatime.module.market.health;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatime.R;
import com.whatime.framework.ui.fragment.BaseFragment;

public class HealthLiftFragment extends BaseFragment {
    
    public HealthLiftFragment()
    {
    }
    
    public HealthLiftFragment(Activity activity)
    {
        this.mActivity = activity;
    }
    
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mTopTitleView.setText(getString(R.string.tab_health_life));
		mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
		return view;
	}

}
