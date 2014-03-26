package com.whatime.framework.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.db.Category;
import com.whatime.framework.ui.view.TabAdapter;

public class MarketScrollingTabsAdapter implements TabAdapter
{
    
    private final Activity activity;
    
    private List<Category> cates;
    
    public MarketScrollingTabsAdapter(Activity act, List<Category> cates)
    {
        activity = act;
        this.cates = cates;
    }
    
    public View getView(int position)
    {
        if (activity == null || cates == null)
        {
            return null;
        }
        final TextView tab = (TextView)LayoutInflater.from(activity).inflate(R.layout.tabs, null);
        if (position < cates.size())
            tab.setText(cates.get(position).getName());
        return tab;
    }
    
}
