package com.whatime.module.schedule.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.books.FriendAddActivity_;
import com.whatime.module.books.SortAdapter;
import com.whatime.module.books.SortModel;
import com.whatime.module.books.hander.CharacterParser;
import com.whatime.module.books.hander.ClearEditText;
import com.whatime.module.books.hander.PinyinComparator;
import com.whatime.module.books.hander.SideBar;
import com.whatime.module.books.hander.SideBar.OnTouchingLetterChangedListener;

public class SchedulePagerAdapter extends PagerAdapter
{
    
    public static final String PREFERENCES = "AlarmClock";
    
    /** This must be false for production.  If true, turns on logging,
        test code, etc. */
    static final boolean DEBUG = false;
    
    private View clockView;
    
    private ExpandableListView mAlarmsList;
    
    private List<OneBar> bars = new ArrayList<OneBar>();
    
    private StatusExpandAdapter statusAdapter;
    
    private Context context;
    
    private ListView sortListView;
    
    private int itemCount;
    
    private SideBar sideBar;
    
    private TextView dialog;
    
    private SortAdapter sortAdapter;
    
    private ClearEditText mClearEditText;
    
    private View view;
    
    private View lastView;
    
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    
    private List<SortModel> SourceDateList;
    
    private AlarmController controller = new AlarmController();
    
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0x001:
                    statusAdapter.setList(bars);
                    statusAdapter.notifyDataSetChanged();
                    // 遍历所有group,将所有项设置成默认展开
                    int groupCount = mAlarmsList.getCount();
                    for (int i = 0; i < groupCount; i++)
                    {
                        mAlarmsList.expandGroup(i);
                    }
                    break;
            }
        };
    };
    
    public SchedulePagerAdapter(int itemCount)
    {
        this.itemCount = itemCount;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        context = container.getContext();
        Object obj = new Object();
        switch (position)
        {
            case 0:
                //取自定义布局的LayoutInflater
                clockView = LayoutInflater.from(context).inflate(R.layout.alarm_clock, container, false);
                //更新布局界面
                mAlarmsList = (ExpandableListView)clockView.findViewById(R.id.expandlist);
                getBars();
                statusAdapter = new StatusExpandAdapter(this,context, bars);
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
                container.addView(clockView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                obj = clockView;
                
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.address_book_fragment, null);
                TextView friendAdd = (TextView)view.findViewById(R.id.friendAdd);
                friendAdd.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        context.startActivity(new Intent(context, FriendAddActivity_.class));
                    }
                });
                container.removeView(lastView);
                lastView = view;
                container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                initViews();
                obj = view;
                break;
            case 1:
            default:
                break;
        }
        
        return obj;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        //container.removeView((View)object);
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
        return itemCount;
    }
    
    public void notifyDataChange()
    {
        if (statusAdapter != null)
        {
            new Thread()
            {
                @Override
                public void run()
                {
                    getBars();
                    handler.sendEmptyMessage(0x001);
                    controller.setNextAlert();
                    
                }
            }.start();
        }
    }
    
    private void getBars()
    {
        bars.clear();
        //今天
        Calendar todayStart = Calendar.getInstance(TimeZone.getDefault());
        todayStart.setTimeInMillis(System.currentTimeMillis());
        Calendar todayEnd = Calendar.getInstance(TimeZone.getDefault());
        todayEnd.setTimeInMillis(System.currentTimeMillis());
        todayEnd.set(Calendar.DAY_OF_YEAR, todayStart.get(Calendar.DAY_OF_YEAR) + 1);
        todayEnd.set(Calendar.HOUR_OF_DAY, 0);
        todayEnd.set(Calendar.MINUTE, 0);
        todayEnd.set(Calendar.SECOND, 0);
        String today_title =
            new StringBuilder().append("今天(")
                .append(todayStart.get(Calendar.MONTH) + 1)
                .append("-")
                .append(todayStart.get(Calendar.DAY_OF_MONTH))
                .append("[")
                .append(SysUtil.getCurrentDayOfWeek(context, todayStart.get(Calendar.DAY_OF_WEEK)))
                .append("]")
                .append(")")
                .toString();
        List<Alarm> todayAlarms =
            DBHelper.getInstance().getAllAlarmByTime(todayStart.getTimeInMillis(), todayEnd.getTimeInMillis());
        if (todayAlarms != null && todayAlarms.size() > 0)
        {
            bars.add(new OneBar(today_title, todayAlarms));
        }
        
        //明天
        Calendar tomorrowEnd = Calendar.getInstance(TimeZone.getDefault());
        tomorrowEnd.setTimeInMillis(System.currentTimeMillis());
        tomorrowEnd.set(Calendar.DAY_OF_YEAR, todayEnd.get(Calendar.DAY_OF_YEAR) + 1);
        tomorrowEnd.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowEnd.set(Calendar.MINUTE, 0);
        tomorrowEnd.set(Calendar.SECOND, 0);
        String tomorrow_title =
            new StringBuilder().append("明天(")
                .append(todayEnd.get(Calendar.MONTH) + 1)
                .append("-")
                .append(todayEnd.get(Calendar.DAY_OF_MONTH))
                .append("[")
                .append(SysUtil.getCurrentDayOfWeek(context, todayEnd.get(Calendar.DAY_OF_WEEK)))
                .append("]")
                .append(")")
                .toString();
        List<Alarm> tomorrowAlarms =
            DBHelper.getInstance().getAllAlarmByTime(todayEnd.getTimeInMillis(), tomorrowEnd.getTimeInMillis());
        if (tomorrowAlarms != null && tomorrowAlarms.size() > 0)
        {
            bars.add(new OneBar(tomorrow_title, tomorrowAlarms));
        }
        
        //本周
        Calendar week = Calendar.getInstance(TimeZone.getDefault());
        week.setTimeInMillis(System.currentTimeMillis());
        int less = 7 - week.get(Calendar.DAY_OF_WEEK);
        week.set(Calendar.DAY_OF_YEAR, tomorrowEnd.get(Calendar.DAY_OF_YEAR) + less);
        week.set(Calendar.HOUR_OF_DAY, 0);
        week.set(Calendar.MINUTE, 0);
        week.set(Calendar.SECOND, 0);
        String week_title =
            new StringBuilder().append("本周(")
                .append(tomorrowEnd.get(Calendar.MONTH) + 1)
                .append(".")
                .append(tomorrowEnd.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(week.get(Calendar.MONTH) + 1)
                .append(".")
                .append(week.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> weekAlarms =
            DBHelper.getInstance().getAllAlarmByTime(tomorrowEnd.getTimeInMillis(), week.getTimeInMillis());
        if (weekAlarms != null && weekAlarms.size() > 0)
        {
            bars.add(new OneBar(week_title, weekAlarms));
        }
        
        //本月
        Calendar month = Calendar.getInstance(TimeZone.getDefault());
        month.setTimeInMillis(System.currentTimeMillis());
        month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
        month.set(Calendar.DAY_OF_MONTH, 1);
        month.set(Calendar.HOUR_OF_DAY, 0);
        month.set(Calendar.MINUTE, 0);
        month.set(Calendar.SECOND, 0);
        String month2_title =
            new StringBuilder().append("本月(")
                .append(week.get(Calendar.MONTH) + 1)
                .append(".")
                .append(week.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(month.get(Calendar.MONTH) + 1)
                .append(".")
                .append(month.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> monthAlarms =
            DBHelper.getInstance().getAllAlarmByTime(week.getTimeInMillis(), month.getTimeInMillis());
        if (monthAlarms != null && monthAlarms.size() > 0)
        {
            bars.add(new OneBar(month2_title, monthAlarms));
        }
        //本年
        if(month.getTimeInMillis()<week.getTimeInMillis())
        {
            month = week;
        }
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
                .append(month.get(Calendar.YEAR))
                .append(".")
                .append(month.get(Calendar.MONTH) + 1)
                .append(".")
                .append(month.get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(year.get(Calendar.YEAR))
                .append(".")
                .append(year.get(Calendar.MONTH) + 1)
                .append(".")
                .append(year.get(Calendar.DAY_OF_MONTH))
                .append(")")
                .toString();
        List<Alarm> yearAlarms =
            DBHelper.getInstance().getAllAlarmByTime(month.getTimeInMillis(), year.getTimeInMillis());
        if (yearAlarms != null && yearAlarms.size() > 0)
        {
            bars.add(new OneBar(year_title, yearAlarms));
        }
        //本年后所有
        Calendar other = Calendar.getInstance(TimeZone.getDefault());
        other.setTimeInMillis(System.currentTimeMillis());
        other.set(Calendar.YEAR, month.get(Calendar.YEAR) + 100);
        other.set(Calendar.DAY_OF_MONTH, 1);
        other.set(Calendar.HOUR_OF_DAY, 0);
        other.set(Calendar.MINUTE, 0);
        other.set(Calendar.SECOND, 0);
        String other_title =
            new StringBuilder().append("本年后所有(")
                .append(month.get(Calendar.YEAR))
                .append(".")
                .append(year.get(Calendar.MONTH) + 1)
                .append(".")
                .append(year.get(Calendar.DAY_OF_MONTH))
                .append("- ")
                .append(")")
                .toString();
        List<Alarm> otherAlarms =
            DBHelper.getInstance().getAllAlarmByTime(year.getTimeInMillis(), other.getTimeInMillis());
        if (otherAlarms != null && otherAlarms.size() > 0)
        {
            bars.add(new OneBar(other_title, otherAlarms));
        }
        
    }
    
    private void initViews()
    {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        
        pinyinComparator = new PinyinComparator();
        
        sideBar = (SideBar)view.findViewById(R.id.sidrbar);
        dialog = (TextView)view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener()
        {
            
            @Override
            public void onTouchingLetterChanged(String s)
            {
                //该字母首次出现的位置
                int position = sortAdapter.getPositionForSection(s.charAt(0));
                if (position != -1)
                {
                    sortListView.setSelection(position);
                }
                
            }
        });
        
        sortListView = (ListView)view.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener()
        {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(context, ((SortModel)sortAdapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        
        SourceDateList =
            filledData(context.getResources().getStringArray(R.array.date),
                context.getResources().getStringArray(R.array.img_src_data));
        
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        sortAdapter = new SortAdapter(context, SourceDateList);
        sortListView.setAdapter(sortAdapter);
        
        //      mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
        //      
        //      //根据输入框输入值的改变来过滤搜索
        //      mClearEditText.addTextChangedListener(new TextWatcher() {
        //          
        //          @Override
        //          public void onTextChanged(CharSequence s, int start, int before, int count) {
        //              //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
        //              filterData(s.toString());
        //          }
        //          
        //          @Override
        //          public void beforeTextChanged(CharSequence s, int start, int count,
        //                  int after) {
        //              
        //          }
        //          
        //          @Override
        //          public void afterTextChanged(Editable s) {
        //          }
        //      });
    }
    
    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date, String[] imgData)
    {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        
        for (int i = 0; i < date.length; i++)
        {
            SortModel sortModel = new SortModel();
            sortModel.setImgSrc(imgData[i]);
            sortModel.setName(date[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]"))
            {
                sortModel.setSortLetters(sortString.toUpperCase());
            }
            else
            {
                sortModel.setSortLetters("#");
            }
            
            mSortList.add(sortModel);
        }
        return mSortList;
        
    }
    
    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr)
    {
        List<SortModel> filterDateList = new ArrayList<SortModel>();
        
        if (TextUtils.isEmpty(filterStr))
        {
            filterDateList = SourceDateList;
        }
        else
        {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList)
            {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                    || characterParser.getSelling(name).startsWith(filterStr.toString()))
                {
                    filterDateList.add(sortModel);
                }
            }
        }
        
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        sortAdapter.updateListView(filterDateList);
    }
}
