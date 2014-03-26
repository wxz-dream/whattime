package com.whatime.module.market.recreation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.framework.ui.adapter.MyListAdapter;
import com.whatime.framework.ui.view.ToastMaster;

public class RecreationPagerAdapter extends PagerAdapter
{
    
    private List<Category> cates;
    
    private OnRefreshListener<ListView> listener;
    
    private Context context;
    
    private List<Alarm> alarms = new ArrayList<Alarm>();
    
    public RecreationPagerAdapter(OnRefreshListener<ListView> listener, List<Category> cates)
    {
        this.listener = listener;
        this.cates = cates;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        context = container.getContext();
        Object obj = new Object();
        switch (position)
        {
            default:
                PullToRefreshListView plv =
                    (PullToRefreshListView)LayoutInflater.from(context).inflate(R.layout.layout_listview_in_viewpager,
                        container,
                        false);
                // Now just add ListView to ViewPager and return it
                container.addView(plv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                
                alarms = DBHelper.getInstance().getOpenAlarms();
                ListAdapter listAdapter = new MyListAdapter(context, alarms);
                plv.setAdapter(listAdapter);
                plv.setOnRefreshListener(listener);
                obj = plv;
                break;
        }
        
        return obj;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
        ToastMaster.cancelToast();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }
    
    
    @Override
    public int getCount()
    {
        return cates.size();
    }
    
}
