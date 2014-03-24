package com.whatime.framework.ui.adapter;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.framework.ui.view.TabAdapter;

public class ScheduleScrollingTabsAdapter implements TabAdapter{
	
	   private final Activity activity;
	   
	   private String[] names;

	    public ScheduleScrollingTabsAdapter(Activity act,String[] names) {
	        activity = act;
	        this.names = names;
	    }
	    
	    
	public View getView(int position) {
	    if(activity==null)
	    {
	        return null;
	    }
		 final TextView tab = (TextView)LayoutInflater.from(activity).inflate(R.layout.tabs, null);
		 final String[] mTitles = names;
	      Set<String> tab_sets = new HashSet<String>(Arrays.asList(mTitles));
	      String[] tabs_new = new String[tab_sets.size()];
	      int cnt = 0;
	      for(int i = 0; i < mTitles.length; i++){
	    	  if(tab_sets.contains(mTitles[i])){
	    		  tabs_new[cnt] = mTitles[i];
	    		  cnt++;
	    	  }
	      }
	      if (position < tabs_new.length)
	            tab.setText(tabs_new[position].toUpperCase());
		return tab;
	}

}
