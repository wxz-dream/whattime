package com.whatime.module.market.recreation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.adapter.MyListAdapter;
import com.whatime.framework.ui.pull.XListView;
import com.whatime.framework.ui.pull.XListView.IXListViewListener;
import com.whatime.framework.ui.view.ToastMaster;

public class RecreationPagerAdapter extends PagerAdapter implements IXListViewListener
{
    
    private List<Category> cates;
    
    private Context context;
    
    private List<Alarm> alarms = new ArrayList<Alarm>();
    
    private XListView plv;
    
    private MyListAdapter listAdapter;
    
    private int position;
    
    private String scope;
    
    private String cateId;
    
    private long startTime;
    
    private long endTime;
    
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
                        alarms = (List<Alarm>)list.get(0);
                        listAdapter.notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "加载成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
            }
        };
    };
    
    public RecreationPagerAdapter(List<Category> cates)
    {
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
                plv =
                    (XListView)LayoutInflater.from(context).inflate(R.layout.layout_listview_in_viewpager,
                        container,
                        false);
                container.addView(plv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                startTime = System.currentTimeMillis();
                endTime = System.currentTimeMillis() + 1000 * 60 * 1000;
                
                new RemoteApiImpl().getMarketAlarm(handler, cates.get(position).getId(), "", startTime, endTime, 0);
                listAdapter = new MyListAdapter(context, alarms);
                plv.setAdapter(listAdapter);
                plv.setXListViewListener(this);
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
    
    @Override
    public void onRefresh()
    {
        
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                alarms.clear();
                startTime = System.currentTimeMillis();
                endTime = System.currentTimeMillis() + 1000 * 60 * 1000;
                new RemoteApiImpl().getMarketAlarm(handler, cates.get(position).getId(), "", startTime, endTime, 0);
                listAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }
    
    @Override
    public void onLoadMore()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startTime = System.currentTimeMillis();
                endTime = System.currentTimeMillis() + 1000 * 60 * 1000;
                new RemoteApiImpl().getMarketAlarm(handler, cates.get(position).getId(), "", startTime, endTime, 0);
                listAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
        
    }
    
    private void onLoad()
    {
        plv.stopRefresh();
        plv.stopLoadMore();
        plv.setRefreshTime("刚刚");
    }
    
}
