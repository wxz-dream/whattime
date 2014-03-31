package com.whatime.framework.ui.adapter;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.User;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.util.SysUtil;

public class MyListAdapter extends BaseAdapter implements ListAdapter
{
    private Context context;
    private List<Alarm> alarms;
    
    public MyListAdapter(Context context,List<Alarm> alarms)
    {
        this.context = context;
        this.alarms = alarms;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Alarm alarm = alarms.get(i);
        User user = new RemoteApiImpl().getUserByUuid(alarm.getUserUuid());
        View v = LayoutInflater.from(context).inflate(R.layout.market_item, null);
        ImageView userPhoto = (ImageView)v.findViewById(R.id.item_user_photo);
        
        ImageView item_add = (ImageView)v.findViewById(R.id.item_add);
        if(user==null)
        {
            return v;
        }
        TextView alarmTitle = (TextView)v.findViewById(R.id.item_alarm_title);
        alarmTitle.setText(alarm.getTitle());
        TextView item_createtime = (TextView)v.findViewById(R.id.item_createtime);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(alarm.getAlarmTime());
        StringBuilder sb = new StringBuilder();
        sb.append(c.get(Calendar.YEAR))
            .append("-")
            .append(c.get(Calendar.MONTH) + 1)
            .append("-")
            .append(c.get(Calendar.DAY_OF_MONTH))
            .append(" ")
            .append(c.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(c.get(Calendar.MINUTE)));
        item_createtime.setText(sb.toString());
        sb.setLength(0);
        TextView item_des = (TextView)v.findViewById(R.id.item_des);
        sb.append("[").append(alarm.getTitle()).append("]")
        .append(alarm.getDes());
        item_des.setText(sb.toString());
        
        return v;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver datasetobserver)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void unregisterDataSetObserver(DataSetObserver datasetobserver)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getCount()
    {
        return alarms.size();
    }
    
    @Override
    public Object getItem(int i)
    {
        return alarms.get(i);
    }
    
    @Override
    public long getItemId(int i)
    {
        return alarms.get(i).getId();
    }
    
    @Override
    public boolean hasStableIds()
    {
        return true;
    }
    
    @Override
    public int getItemViewType(int i)
    {
        return 1;
    }
    
    @Override
    public int getViewTypeCount()
    {
        return 1;
    }
    
    @Override
    public boolean isEmpty()
    {
        return alarms.isEmpty();
    }
    
    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }
    
    @Override
    public boolean isEnabled(int i)
    {
        return true;
    }
    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }
    
    
}
