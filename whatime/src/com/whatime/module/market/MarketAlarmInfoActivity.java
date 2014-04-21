package com.whatime.module.market;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.controller.cons.AdvanceCons;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.ui.view.MyListView;
import com.whatime.framework.util.SysUtil;

@EActivity(R.layout.market_alarm_info)
public class MarketAlarmInfoActivity extends Activity
{
    @ViewById
    TextView market_alarm_title;
    
    @ViewById
    TextView market_alarm_share;
    
    @ViewById
    TextView market_alarm_des;
    
    @ViewById
    TextView market_alarm_cate;
    
    @ViewById
    TextView market_alarm_scope;
    
    @ViewById
    TextView market_alarm_linkman;
    
    @ViewById
    TextView market_alarm_max_joinNum;
    
    @ViewById
    TextView market_alarm_endTime;
    
    @ViewById
    MyListView market_task_lv;
    
    private MyAdapter adapter;
    
    private Context context;
    
    private Alarm mAlarm;
    
    private ArrayList<String> repeatLabels = new ArrayList<String>();
    
    final String[] langs = {"朋友圈", "网络", "新浪微博", "QQ空间", "人人网", "腾讯微博"};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context = this;
        initData();
    }
    
    @AfterViews
    void initView()
    {
        initAlarm();
        initUi();
        uptUi();
    }
    
    private void initAlarm()
    {
        mAlarm = (Alarm)getIntent().getSerializableExtra("alarm");
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        Collections.sort(mAlarm.getTasks(), new Comparator<Task>()
        {
            @Override
            public int compare(Task arg0, Task arg1)
            {
                if (arg0.getAlarmTime() >= arg1.getAlarmTime())
                {
                    return 1;
                }
                return -1;
            }
        });
        adapter.notifyDataSetChanged();
    }
    
    private void initUi()
    {
        market_alarm_title.setText(mAlarm.getTitle());
        String share = mAlarm.getShare();
        String[] s = share.split(",");
        StringBuilder sb = new StringBuilder();
        for (String str : s)
        {
            sb.append(langs[Integer.valueOf(str)]).append(",");
        }
        String shareLabel = sb.toString();
        if (shareLabel.endsWith(","))
        {
            shareLabel = shareLabel.substring(0, shareLabel.length() - 1);
        }
        market_alarm_share.setText(shareLabel);
        market_alarm_des.setText(mAlarm.getDes());
        adapter = new MyAdapter();
        market_task_lv.setAdapter(adapter);
        if(mAlarm.getCateId()!=null)
        {
            market_alarm_cate.setText(DBHelper.getInstance().getcaById(mAlarm.getCateId()).getName());
        }
        market_alarm_scope.setText(mAlarm.getScope());
        market_alarm_linkman.setText("");
        
    }
    
    @UiThread
    void uptUi()
    {
        
    }
    
    class MyAdapter extends BaseAdapter
    {
        
        @Override
        public int getCount()
        {
            return mAlarm.getTasks().size();
        }
        
        @Override
        public Object getItem(int arg0)
        {
            return mAlarm.getTasks().get(arg0);
        }
        
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }
        
        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2)
        {
            Task task = mAlarm.getTasks().get(arg0);
            View v = LayoutInflater.from(context).inflate(R.layout.add_common_item, null);
            TextView title = (TextView)v.findViewById(R.id.item_title);
            if (task.getTitle() != null)
            {
                title.setText(task.getTitle());
            }
            TextView time = (TextView)v.findViewById(R.id.item_time);
            Calendar c = Calendar.getInstance(TimeZone.getDefault());
            if (task.getAdvanceOrder() != AdvanceCons.ORDER_DEFAULT || task.getSetTime() != null)
            {
                c.setTimeInMillis(task.getSetTime());
            }
            else
            {
                c.setTimeInMillis(task.getAlarmTime());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(c.get(Calendar.YEAR))
                .append("-")
                .append(c.get(Calendar.MONTH) + 1)
                .append("-")
                .append(c.get(Calendar.DAY_OF_MONTH))
                .append("(")
                .append(SysUtil.getCurrentDayOfWeek(context, c.get(Calendar.DAY_OF_WEEK)))
                .append(")   ")
                .append(c.get(Calendar.HOUR_OF_DAY))
                .append(":")
                .append(SysUtil.doubleDataFormat(c.get(Calendar.MINUTE)))
                .append("  |")
                .append(repeatLabels.get(task.getRepeatType()));
            time.setText(sb.toString());
            LinearLayout desLL = (LinearLayout)v.findViewById(R.id.item_des_ll);
            TextView des = (TextView)v.findViewById(R.id.item_des);
            if (task.getDes() != null && task.getDes().length() > 0)
            {
                des.setText("☆  " + task.getDes());
                desLL.setVisibility(View.VISIBLE);
            }
            else
            {
                desLL.setVisibility(View.GONE);
            }
            LinearLayout addressLL = (LinearLayout)v.findViewById(R.id.item_address_ll);
            TextView address = (TextView)v.findViewById(R.id.item_address);
            if (task.getAddress() != null && task.getAddress().length() > 0)
            {
                address.setText("☆  " + task.getAddress());
                addressLL.setVisibility(View.VISIBLE);
            }
            else
            {
                addressLL.setVisibility(View.GONE);
            }
            LinearLayout noticeLL = (LinearLayout)v.findViewById(R.id.item_notice_ll);
            TextView notice = (TextView)v.findViewById(R.id.item_notice);
            if (task.getNotice() != null && task.getNotice().length() > 0)
            {
                notice.setText("☆  " + task.getNotice());
                noticeLL.setVisibility(View.VISIBLE);
            }
            else
            {
                noticeLL.setVisibility(View.GONE);
            }
            
            return v;
        }
        
    }
    
    private void initData()
    {
        repeatLabels.add(getString(R.string.repeat_default));
        repeatLabels.add(getString(R.string.repeat_work));
        repeatLabels.add(getString(R.string.repeat_m_f));
        repeatLabels.add(getString(R.string.repeat_s_s));
        repeatLabels.add(getString(R.string.repeat_everyyear));
        repeatLabels.add(getString(R.string.repeat_everymonth));
        repeatLabels.add(getString(R.string.repeat_everyweekday));
        repeatLabels.add(getString(R.string.repeat_everyday));
        repeatLabels.add(getString(R.string.other));
        
    }
}
