package com.whatime.module.market;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.adapter.MyListAdapter;
import com.whatime.framework.ui.pull.XListView;
import com.whatime.framework.ui.pull.XListView.IXListViewListener;
import com.whatime.framework.ui.view.ToastMaster;

public class MarketPagerAdapter extends PagerAdapter implements IXListViewListener
{
    
    private List<Category> cates;
    
    private ViewPager mPager;
    
    private Context context;
    
    private List<List<Alarm>> alarms = new ArrayList<List<Alarm>>();
    
    private XListView plv;
    
    private MyListAdapter listAdapter;
    
    private String scope;
    
    private long startTime;
    
    private long endTime;
    
    private int mPage;
    
    private List<View> views = new ArrayList<View>();
    
    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 0x001:
                    int state = msg.getData().getInt(ResponseCons.STATE);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        ArrayList list = msg.getData().getParcelableArrayList(ResponseCons.RESINFO);
                        alarms.add(mPager.getCurrentItem(), (List<Alarm>)list.get(0));
                        if (alarms.size() > 0)
                        {
                            listAdapter = new MyListAdapter(context, alarms.get(mPager.getCurrentItem()));
                            plv = (XListView)views.get(mPager.getCurrentItem());
                            plv.setOnItemClickListener(new OnItemClickListener()
                            {
                                
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                                {
                                    context.startActivity(new Intent(context, MarketAlarmInfoActivity_.class).putExtra("alarm",
                                        alarms.get(mPager.getCurrentItem()).get((int)arg3)));
                                }
                            });
                            plv.setAdapter(listAdapter);
                        }
                    }
                    onLoad();
                    break;
            }
        };
    };
    
    public MarketPagerAdapter(List<Category> cates, ViewPager mPager)
    {
        this.cates = cates;
        this.mPager = mPager;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position)
    {
        if (position >= views.size())
        {
            container.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            context = container.getContext();
            plv =
                (XListView)LayoutInflater.from(context)
                    .inflate(R.layout.layout_listview_in_viewpager, container, false);
            alarms.add(position, new ArrayList<Alarm>());
            listAdapter = new MyListAdapter(context, alarms.get(position));
            plv.setAdapter(listAdapter);
            plv.setXListViewListener(this);
            plv.setOnItemClickListener(new OnItemClickListener()
            {
                
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    context.startActivity(new Intent(context, MarketAlarmInfoActivity_.class).putExtra("alarm",
                        alarms.get(mPager.getCurrentItem()).get((int)arg3)));
                }
            });
            views.add(position, plv);
        }
        mPage = 0;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        container.addView(views.get(position), 0, params);
        onRefresh();
        return views.get(position);
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (views != null && views.size() > 0)
        {
            container.removeView(views.get(position));
        }
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
    
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        super.setPrimaryItem(container, position, object);
        
    }
    
    @Override
    public void onRefresh()
    {
        if (cates.size() == 0)
        {
            return;
        }
        Calendar startC = Calendar.getInstance(TimeZone.getDefault());
        if (startC.getTimeInMillis() < startTime)
        {
            startC.setTimeInMillis(startTime);
        }
        Calendar endC = Calendar.getInstance(TimeZone.getDefault());
        if (endC.getTimeInMillis() < endTime)
        {
            endC.setTimeInMillis(endTime);
        }
        else
        {
            endC.setTimeInMillis(startC.getTimeInMillis());
        }
        if (endC.getTimeInMillis() <= startC.getTimeInMillis())
        {
            endC.set(Calendar.DAY_OF_YEAR, endC.get(Calendar.DAY_OF_YEAR) + 1);
            endC.set(Calendar.HOUR_OF_DAY, 0);
            endC.set(Calendar.MINUTE, 0);
            endC.set(Calendar.SECOND, 0);
        }
        startTime = startC.getTimeInMillis();
        endTime = endC.getTimeInMillis();
        new RemoteApiImpl().getMarketAlarm(handler,
            cates.get(mPager.getCurrentItem()).getId(),
            "",
            startTime,
            endTime,
            0);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onLoad();
            }
        }, 4000);
    }
    
    @Override
    public void onLoadMore()
    {
        if (cates.size() == 0)
        {
            return;
        }
        new RemoteApiImpl().getMarketAlarm(handler,
            cates.get(mPager.getCurrentItem()).getId(),
            "",
            startTime,
            endTime,
            mPage++);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onLoad();
            }
        }, 4000);
        
    }
    
    private void onLoad()
    {
        plv = (XListView)views.get(mPager.getCurrentItem());
        plv.stopRefresh();
        plv.stopLoadMore();
        plv.setRefreshTime("刚刚");
        
    }
    
    public String getScope()
    {
        return scope;
    }
    
    public void setScope(String scope)
    {
        this.scope = scope;
    }
    
    public long getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }
    
    public long getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }
}
