package com.whatime.module.market;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePickerDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.ui.adapter.MarketScrollingTabsAdapter;
import com.whatime.framework.ui.fragment.BaseMarketFragment;
import com.whatime.framework.ui.view.ScrollableTabView;
import com.whatime.framework.util.SysUtil;

public class MarketFragment extends BaseMarketFragment
{
    private long page;
    
    private Category cate;
    
    private MyListener lis = new MyListener();
    
    private List<Category> cates;
    
    private CheckBox no_scope;
    
    private CheckBox pro;
    
    private CheckBox ct;
    
    private Spinner province_spinner;
    
    private Spinner city_spinner;
    
    private HashMap<String, String> pros;
    
    private List<String> provinces;
    
    private HashMap<String, String> cts;
    
    private List<String> citys;
    
    private TextView select_startTime;
    
    private TextView select_endTime;
    
    private MarketPagerAdapter listViewPagerAdapter;
    
    private MarketScrollingTabsAdapter mScrollingTabsAdapter;
    
    private Calendar startTime = Calendar.getInstance(TimeZone.getDefault());
    
    private Calendar endTime = Calendar.getInstance(TimeZone.getDefault());
    
    public MarketFragment()
    {
    }
    
    public MarketFragment(Activity activity, int page)
    {
        this.mActivity = activity;
        this.page = page;
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        cate = DBHelper.getInstance().getcaById(page);
        mTopTitleView.setText(cate.getDes());
        mTopBackView.setBackgroundResource(R.drawable.biz_local_news_main_back_normal);
        cates = DBHelper.getInstance().getcateByParentId(page);
        listViewPagerAdapter = new MarketPagerAdapter(cates, mPager);
        mPager.setAdapter(listViewPagerAdapter);
        pagerItemList = new ArrayList<Fragment>();
        for (int i = 0; i < cates.size(); i++)
        {
            pagerItemList.add(new Fragment());
        }
        initScrollableTabs(view, mPager);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        TextView top_select = (TextView)view.findViewById(R.id.top_select);
        top_select.setOnClickListener(lis);
        return view;
    }
    
    protected void initScrollableTabs(View view, ViewPager mViewPager)
    {
        mScrollableTabView = (ScrollableTabView)view.findViewById(R.id.scrollabletabview);
        mScrollingTabsAdapter = new MarketScrollingTabsAdapter(mActivity, cates);
        mScrollableTabView.setAdapter(mScrollingTabsAdapter);
        mScrollableTabView.setViewPage(mViewPager);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listViewPagerAdapter = null;
        mScrollingTabsAdapter = null;
    }
    
    private DatePickerDialog.OnDateSetListener startTimeSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, monthOfYear);
            startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            select_startTime.setText(getTimeLabel(startTime));
            listViewPagerAdapter.setStartTime(startTime.getTimeInMillis());
        }
    };
    
    private DatePickerDialog.OnDateSetListener endTimeSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            endTime.set(Calendar.YEAR, year);
            endTime.set(Calendar.MONTH, monthOfYear);
            endTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            select_endTime.setText(getTimeLabel(endTime));
            listViewPagerAdapter.setEndTime(endTime.getTimeInMillis());
        }
    };
    
    private String getTimeLabel(Calendar c)
    {
        return new StringBuilder().append(c.get(Calendar.YEAR))
            .append("-")
            .append(c.get(Calendar.MONTH) + 1)
            .append("-")
            .append(SysUtil.doubleDataFormat(c.get(Calendar.DAY_OF_MONTH)))
            .append(" (")
            .append(SysUtil.getCurrentDayOfWeek(mContext, c.get(Calendar.DAY_OF_WEEK)))
            .append(")")
            .toString();
    }
    
    class MyListener implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.top_select:
                    // 取得city_layout.xml中的视图
                    final View selectView = LayoutInflater.from(mContext).inflate(R.layout.market_select_pup, null);
                    // 省份Spinner
                    province_spinner = (Spinner)selectView.findViewById(R.id.province_spinner);
                    // 城市Spinner
                    city_spinner = (Spinner)selectView.findViewById(R.id.city_spinner);
                    // 省份列表
                    pros = MyApp.getInstance().getProvince();
                    provinces = new ArrayList<String>();
                    for (String p : pros.keySet())
                    {
                        provinces.add(p);
                    }
                    ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, provinces);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    no_scope = (CheckBox)selectView.findViewById(R.id.no_scope);
                    pro = (CheckBox)selectView.findViewById(R.id.pro);
                    ct = (CheckBox)selectView.findViewById(R.id.ct);
                    
                    no_scope.setOnCheckedChangeListener(new MyOncheckListener());
                    pro.setOnCheckedChangeListener(new MyOncheckListener());
                    ct.setOnCheckedChangeListener(new MyOncheckListener());
                    province_spinner.setAdapter(adapter);
                    // 省份Spinner监听器
                    province_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
                    {
                        
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
                        {
                            cts = MyApp.getInstance().getCitys().get(pros.get(provinces.get(position)));
                            citys = new ArrayList<String>();
                            for (String c : cts.keySet())
                            {
                                citys.add(c);
                            }
                            ArrayAdapter adapter1 =
                                new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, citys);
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            city_spinner.setAdapter(adapter1);
                        }
                        
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0)
                        {
                        }
                    });
                    select_startTime = (TextView)selectView.findViewById(R.id.select_startTime);
                    select_endTime = (TextView)selectView.findViewById(R.id.select_endTime);
                    if (listViewPagerAdapter.getStartTime() > startTime.getTimeInMillis())
                    {
                        startTime.setTimeInMillis(listViewPagerAdapter.getStartTime());
                    }
                    if (listViewPagerAdapter.getEndTime() > listViewPagerAdapter.getStartTime())
                    {
                        endTime.setTimeInMillis(listViewPagerAdapter.getEndTime());
                    }
                    else
                    {
                        endTime.setTimeInMillis(startTime.getTimeInMillis());
                        endTime.set(Calendar.DAY_OF_YEAR, endTime.get(Calendar.DAY_OF_YEAR) + 1);
                    }
                    select_startTime.setText(getTimeLabel(startTime));
                    select_endTime.setText(getTimeLabel(endTime));
                    select_startTime.setOnClickListener(new OnClickListener()
                    {
                        
                        @Override
                        public void onClick(View view)
                        {
                            new DatePickerDialog(mContext, startTimeSetListener, startTime.get(Calendar.YEAR),
                                startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    select_endTime.setOnClickListener(new OnClickListener()
                    {
                        
                        @Override
                        public void onClick(View view)
                        {
                            new DatePickerDialog(mContext, endTimeSetListener, endTime.get(Calendar.YEAR),
                                endTime.get(Calendar.MONTH), endTime.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    
                    // 选择城市对话框
                    AlertDialog.Builder select_dialog = new AlertDialog.Builder(mActivity);
                    select_dialog.setTitle("请选择筛选信息");
                    select_dialog.setView(selectView);
                    select_dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String scope = "";
                            if (no_scope.isChecked())
                            {
                                scope = null;
                            }
                            if (pro.isChecked())
                            {
                                scope = provinces.get(province_spinner.getSelectedItemPosition());
                            }
                            if (ct.isChecked())
                            {
                                scope =
                                    provinces.get(province_spinner.getSelectedItemPosition()) + "|"
                                        + citys.get(city_spinner.getSelectedItemPosition());
                            }
                            listViewPagerAdapter.setScope(scope);
                            listViewPagerAdapter.onRefresh();
                        }
                    });
                    select_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    String scope = listViewPagerAdapter.getScope();
                    if (scope == null)
                    {
                        no_scope.setChecked(true);
                    }
                    else if (!scope.contains("|"))
                    {
                        pro.setChecked(true);
                        province_spinner.setSelection(provinces.indexOf(scope));
                    }
                    else
                    {
                        pro.setChecked(true);
                        province_spinner.setSelection(provinces.indexOf(scope.split("\\|")[0]));
                        ct.setChecked(true);
                    }
                    select_dialog.show();
                    break;
            }
        }
    }
    
    class MyOncheckListener implements OnCheckedChangeListener
    {
        
        @Override
        public void onCheckedChanged(CompoundButton check, boolean flag)
        {
            switch (check.getId())
            {
                case R.id.no_scope:
                    if (flag)
                    {
                        ct.setChecked(false);
                        pro.setChecked(false);
                        province_spinner.setVisibility(View.GONE);
                        city_spinner.setVisibility(View.GONE);
                    }
                    break;
                case R.id.pro:
                    if (flag)
                    {
                        no_scope.setChecked(false);
                        province_spinner.setVisibility(View.VISIBLE);
                        city_spinner.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (ct.isChecked())
                        {
                            pro.setChecked(true);
                        }
                        else
                        {
                            province_spinner.setVisibility(View.GONE);
                            city_spinner.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.ct:
                    if (flag)
                    {
                        no_scope.setChecked(false);
                        pro.setChecked(true);
                        province_spinner.setVisibility(View.VISIBLE);
                        city_spinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if (!pro.isChecked())
                        {
                            province_spinner.setVisibility(View.GONE);
                        }
                        city_spinner.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }
}
