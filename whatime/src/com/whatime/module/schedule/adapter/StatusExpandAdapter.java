package com.whatime.module.schedule.adapter;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.service.AlarmUtil;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.module.addcolock.AlarmAddActivity_;
import com.whatime.module.schedule.view.DigitalClock;

public class StatusExpandAdapter extends BaseExpandableListAdapter
{
    //private static final String TAG = "StatusExpandAdapter";
    private LayoutInflater inflater = null;
    
    private List<OneBar> oneList;
    
    private Context context;
    
    private AlarmController controller = new AlarmController();
    
    private SchedulePagerAdapter schedulePagerAdapter;
    
    public StatusExpandAdapter(SchedulePagerAdapter schedulePagerAdapter, Context context, List<OneBar> oneList)
    {
        this.oneList = oneList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.schedulePagerAdapter = schedulePagerAdapter;
    }
    
    public void setList(List<OneBar> oneList)
    {
        this.oneList = oneList;
        
    }
    
    @Override
    public int getGroupCount()
    {
        // TODO Auto-generated method stub
        return oneList.size();
    }
    
    @Override
    public int getChildrenCount(int groupPosition)
    {
        if (groupPosition >= oneList.size())
        {
            return 0;
        }
        if (oneList.get(groupPosition).alarms == null)
        {
            return 0;
        }
        else
        {
            return oneList.get(groupPosition).alarms.size();
        }
    }
    
    @Override
    public OneBar getGroup(int groupPosition)
    {
        // TODO Auto-generated method stub
        return oneList.get(groupPosition);
    }
    
    @Override
    public Alarm getChild(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        if(oneList.size()==0 || oneList.get(groupPosition).alarms.size()==0)
        {
            return null;
        }
        return oneList.get(groupPosition).alarms.get(childPosition);
    }
    
    @Override
    public long getGroupId(int groupPosition)
    {
        // TODO Auto-generated method stub
        return groupPosition;
    }
    
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return childPosition;
    }
    
    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        
        GroupViewHolder holder = new GroupViewHolder();
        
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.one_status_item, null);
        }
        if(oneList.size()==0)
        {
            return convertView;
        }
        holder.groupName = (TextView)convertView.findViewById(R.id.one_status_name);
        holder.group_tiao = (TextView)convertView.findViewById(R.id.group_tiao);
        holder.groupName.setText(oneList.get(groupPosition).name);
        if (oneList.get(groupPosition).alarms.get(0).getOpen())
        {
            holder.group_tiao.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }
        else
        {
            holder.group_tiao.setBackgroundColor(context.getResources().getColor(R.color.grey));
        }
        
        return convertView;
    }
    
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
        ViewGroup parent)
    {
        final Alarm entity = getChild(groupPosition, childPosition);
        convertView = inflater.inflate(R.layout.alarm_time, null);
        DigitalClock digitalClock = (DigitalClock)convertView.findViewById(R.id.digitalClock);
        digitalClock.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context, AlarmAddActivity_.class).putExtra(AlarmServiceCons.ALARM_ID,
                    entity.getId()).putExtra(AlarmServiceCons.ALARM_TYPE, childPosition));
            }
        });
        digitalClock.setOnLongClickListener(new OnLongClickListener()
        {
            
            @Override
            public boolean onLongClick(View arg0)
            {
                // Confirm that the alarm will be deleted.
                new AlertDialog.Builder(context).setTitle(context.getString(R.string.delete_alarm))
                    .setMessage(context.getString(R.string.delete_alarm_confirm))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface d, int w)
                        {
                            for (Task task : entity.getTasks())
                            {
                                task.setDel(true);
                                task.setOpen(false);
                                DBHelper.getInstance().uptTask(task);
                            }
                            entity.setDel(true);
                            entity.setOpen(false);
                            entity.setOwerUserUuid("");
                            entity.setOwerUuid("");
                            controller.uptAlarm(entity,new Handler());
                            schedulePagerAdapter.notifyDataChange();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
                return true;
            }
        });
        View indicator = convertView.findViewById(R.id.indicator);
        final ImageView barOnOff = (ImageView)indicator.findViewById(R.id.bar_onoff);
        barOnOff.setImageResource(entity.getOpen() ? R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);
        final CheckBox clockOnOff = (CheckBox)indicator.findViewById(R.id.clock_onoff);
        clockOnOff.setChecked(entity.getOpen());
        clockOnOff.setBackgroundResource(entity.getOpen() ? R.drawable.ic_clock_alarm_on
            : R.drawable.ic_clock_alarm_off);
        indicator.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                clockOnOff.toggle();
                updateIndicatorAndAlarm(clockOnOff.isChecked(), barOnOff, clockOnOff, entity);
            }
        });
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(entity.getAlarmTime());
        digitalClock.updateTime(c);
        digitalClock.setTypeface(Typeface.DEFAULT);
        digitalClock.setLive(false);
        Calendar todayStart = Calendar.getInstance(TimeZone.getDefault());
        todayStart.setTimeInMillis(entity.getAlarmTime());
        TextView calend = (TextView)convertView.findViewById(R.id.calend);
        calend.setText(new StringBuilder().append(todayStart.get(Calendar.MONTH) + 1)
            .append(".")
            .append(todayStart.get(Calendar.DAY_OF_MONTH)));
        LinearLayout content = (LinearLayout)convertView.findViewById(R.id.content);
        content.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context, AlarmAddActivity_.class).putExtra(AlarmServiceCons.ALARM_ID,
                    entity.getId()).putExtra(AlarmServiceCons.ALARM_TYPE, childPosition));
            }
        });
        content.setOnLongClickListener(new OnLongClickListener()
        {
            
            @Override
            public boolean onLongClick(View arg0)
            {
                // Confirm that the alarm will be deleted.
                new AlertDialog.Builder(context).setTitle(context.getString(R.string.delete_alarm))
                    .setMessage(context.getString(R.string.delete_alarm_confirm))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface d, int w)
                        {
                            for (Task task : entity.getTasks())
                            {
                                task.setDel(true);
                                task.setOpen(false);
                                DBHelper.getInstance().uptTask(task);
                            }
                            entity.setDel(true);
                            entity.setOpen(false);
                            controller.uptAlarm(entity,new Handler());
                            schedulePagerAdapter.notifyDataChange();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
                return true;
            }
        });
        TextView labelView = (TextView)convertView.findViewById(R.id.alarm_title);
        StringBuilder oneLine = new StringBuilder();
        String alarmTitle = entity.getTitle();
        Task task = entity.getTask();
        if(task==null)
        {
            return convertView;
        }
        String taskTitle = task.getTitle();
        
        if (alarmTitle != null || taskTitle != null)
        {
            labelView.setVisibility(View.VISIBLE);
            if (alarmTitle != null && alarmTitle.length() > 0)
            {
                oneLine.append("[").append(alarmTitle).append("]  ");
            }
            if (taskTitle != null && taskTitle.length() > 0)
            {
                oneLine.append("★").append(taskTitle);
            }
            labelView.setText(oneLine);
        }
        else
        {
            labelView.setVisibility(View.GONE);
        }
        TextView taskDes = (TextView)convertView.findViewById(R.id.task_des);
        String task_des = entity.getTask().getDes();
        if (task_des != null && task_des.length() != 0)
        {
            taskDes.setText("☆" + task_des);
            taskDes.setVisibility(View.VISIBLE);
        }
        else
        {
            taskDes.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    //更新checkbox
    private void updateIndicatorAndAlarm(boolean enabled, ImageView bar, CheckBox clockOnOff, Alarm alarm)
    {
        clockOnOff.setBackgroundResource(enabled ? R.drawable.ic_clock_alarm_on : R.drawable.ic_clock_alarm_off);
        bar.setImageResource(enabled ? R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);
        controller.enableAlarm(alarm.getId(), enabled);
        if (enabled)
        {
            String toastText = AlarmUtil.formatToast(context, alarm.getAlarmTime());
            Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
            ToastMaster.setToast(toast);
            toast.show();
        }
    }
    
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    private class GroupViewHolder
    {
        TextView groupName;
        
        public TextView group_tiao;
    }
    
}
