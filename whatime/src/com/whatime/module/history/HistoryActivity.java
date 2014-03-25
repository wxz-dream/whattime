package com.whatime.module.history;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.schedule.adapter.OneBar;

@EActivity(R.layout.history)
public class HistoryActivity extends Activity
{
    
    private Context context;
    
    private ExpandableListView mAlarmsList;
    
    private List<OneBar> bars = new ArrayList<OneBar>();
    
    private HistoryExpandAdapter statusAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context = this;
    }
    
    @AfterViews
    void initView()
    {
        //更新布局界面
        mAlarmsList = (ExpandableListView)findViewById(R.id.expandlist);
        getBars();
        statusAdapter = new HistoryExpandAdapter(context, bars);
        mAlarmsList.setAdapter(statusAdapter);
        mAlarmsList.setGroupIndicator(null); // 去掉默认带的箭头
        
        // 遍历所有group,将所有项设置成默认展开
        int groupCount = mAlarmsList.getCount();
        for (int i = 0; i < groupCount; i++)
        {
            mAlarmsList.expandGroup(i);
        }
        mAlarmsList.setOnGroupClickListener(new OnGroupClickListener()
        {
            
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                return true;
            }
        });
    }
    
    @Click
    void regist_reback_btn()
    {
        this.finish();
    }
    
    private void getBars()
    {
        bars.clear();
        //今天
        Calendar todayNow = Calendar.getInstance(TimeZone.getDefault());
        todayNow.setTimeInMillis(System.currentTimeMillis());
        Calendar todayStart = Calendar.getInstance(TimeZone.getDefault());
        todayStart.setTimeInMillis(System.currentTimeMillis());
        todayStart.set(Calendar.DAY_OF_MONTH, todayNow.get(Calendar.DAY_OF_MONTH) - 1);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        String today_title =
            new StringBuilder().append("今天(")
                .append(todayNow.get(Calendar.MONTH) + 1)
                .append("-")
                .append(todayNow.get(Calendar.DAY_OF_MONTH))
                .append("[")
                .append(SysUtil.getCurrentDayOfWeek(context, todayNow.get(Calendar.DAY_OF_WEEK)))
                .append("]")
                .append(")")
                .toString();
        List<Alarm> todayAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(todayStart.getTimeInMillis(), todayNow.getTimeInMillis());
        if (todayAlarms != null && todayAlarms.size() > 0)
        {
            bars.add(new OneBar(today_title, todayAlarms));
        }
        
        //昨天
        Calendar yestodayStart = Calendar.getInstance(TimeZone.getDefault());
        yestodayStart.setTimeInMillis(System.currentTimeMillis());
        yestodayStart.set(Calendar.DAY_OF_MONTH, todayStart.get(Calendar.DAY_OF_MONTH) - 1);
        yestodayStart.set(Calendar.HOUR_OF_DAY, 0);
        yestodayStart.set(Calendar.MINUTE, 0);
        yestodayStart.set(Calendar.SECOND, 0);
        String yestoday_title =
            new StringBuilder().append("昨天(")
                .append(yestodayStart.get(Calendar.MONTH) + 1)
                .append("-")
                .append(yestodayStart.get(Calendar.DAY_OF_MONTH))
                .append("[")
                .append(SysUtil.getCurrentDayOfWeek(context, todayStart.get(Calendar.DAY_OF_WEEK)))
                .append("]")
                .append(")")
                .toString();
        List<Alarm> yestodayAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(yestodayStart.getTimeInMillis(), todayStart.getTimeInMillis());
        if (yestodayAlarms != null && yestodayAlarms.size() > 0)
        {
            bars.add(new OneBar(yestoday_title, yestodayAlarms));
        }
        
        //上周
        Calendar week = Calendar.getInstance(TimeZone.getDefault());
        week.setTimeInMillis(System.currentTimeMillis());
        int less = week.get(Calendar.DAY_OF_WEEK);
        week.set(Calendar.DAY_OF_MONTH, yestodayStart.get(Calendar.DAY_OF_MONTH) - less);
        week.set(Calendar.HOUR_OF_DAY, 0);
        week.set(Calendar.MINUTE, 0);
        week.set(Calendar.SECOND, 0);
        String week_title =
            new StringBuilder().append("上周(")
                .append(week.get(Calendar.MONTH) + 1)
                .append(".")
                .append(week.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(yestodayStart.get(Calendar.MONTH) + 1)
                .append(".")
                .append(yestodayStart.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> weekAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(week.getTimeInMillis(), yestodayStart.getTimeInMillis());
        if (weekAlarms != null && weekAlarms.size() > 0)
        {
            bars.add(new OneBar(week_title, weekAlarms));
        }
        
        //上月
        Calendar month = Calendar.getInstance(TimeZone.getDefault());
        month.setTimeInMillis(System.currentTimeMillis());
        month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
        month.set(Calendar.DAY_OF_MONTH, 1);
        month.set(Calendar.HOUR_OF_DAY, 0);
        month.set(Calendar.MINUTE, 0);
        month.set(Calendar.SECOND, 0);
        String month2_title =
            new StringBuilder().append("上月(")
                .append(month.get(Calendar.MONTH) + 1)
                .append(".")
                .append(month.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(week.get(Calendar.MONTH) + 1)
                .append(".")
                .append(week.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> monthAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(month.getTimeInMillis(), week.getTimeInMillis());
        if (monthAlarms != null && monthAlarms.size() > 0)
        {
            bars.add(new OneBar(month2_title, monthAlarms));
        }
        //本年
        Calendar year = Calendar.getInstance(TimeZone.getDefault());
        year.setTimeInMillis(System.currentTimeMillis());
        year.set(Calendar.YEAR, month.get(Calendar.YEAR) + 1);
        year.set(Calendar.MONTH, 0);
        year.set(Calendar.DAY_OF_MONTH, 1);
        year.set(Calendar.HOUR_OF_DAY, 0);
        year.set(Calendar.MINUTE, 0);
        year.set(Calendar.SECOND, 0);
        String year_title =
            new StringBuilder().append("本年(")
                .append(year.get(Calendar.YEAR))
                .append(".")
                .append(year.get(Calendar.MONTH) + 1)
                .append(".")
                .append(year.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(month.get(Calendar.YEAR))
                .append(".")
                .append(month.get(Calendar.MONTH) + 1)
                .append(".")
                .append(month.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> yearAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(year.getTimeInMillis(),month.getTimeInMillis());
        if (yearAlarms != null && yearAlarms.size() > 0)
        {
            bars.add(new OneBar(year_title, yearAlarms));
        }
        //本年前所有
        Calendar other = Calendar.getInstance(TimeZone.getDefault());
        other.setTimeInMillis(System.currentTimeMillis());
        other.set(Calendar.YEAR, month.get(Calendar.YEAR) + 100);
        other.set(Calendar.DAY_OF_MONTH, 1);
        other.set(Calendar.HOUR_OF_DAY, 0);
        other.set(Calendar.MINUTE, 0);
        other.set(Calendar.SECOND, 0);
        String other_title =
            new StringBuilder().append("本年前所有(")
                .append(month.get(Calendar.YEAR))
                .append(".")
                .append(year.get(Calendar.MONTH) + 1)
                .append(".")
                .append(year.get(Calendar.DAY_OF_MONTH))
                .append("- ")
                .append(")")
                .toString();
        List<Alarm> otherAlarms =
            DBHelper.getInstance().getHistoryAlarmByTime(other.getTimeInMillis(),year.getTimeInMillis());
        if (otherAlarms != null && otherAlarms.size() > 0)
        {
            bars.add(new OneBar(other_title, otherAlarms));
        }
        
    }
}
