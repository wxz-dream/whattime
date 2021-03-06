package com.whatime.framework.ui.fragment;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.addcolock.QuickAddActivity;
import com.whatime.module.weather.activity.WeatherWebServiceActivity;
import com.whatime.module.weather.util.WeatherUtil;
import com.whatime.module.weather.util.WebServiceUtil;

public class ScheduleBaseFragment extends BaseFragment
{
    
    private View weatherLayout;
    
    private SharedPreferences preference;
    
    private ImageView wheatherIcon;
    
    private TextView wheatherCity;
    
    private TextView wheatherTemperature;
    
    private GetWhearTask task;
    
    protected TextView addBt;
    
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what)
            {
                case 0x01:
                    wheatherIcon.setImageResource(data.getInt("icon"));
                    wheatherCity.setText(data.getString("city"));
                    wheatherTemperature.setText(data.getString("temperature"));
                    break;
            }
        }
    };
    
    public ScheduleBaseFragment()
    {
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        String city = preference.getString("city", "北京");
        if (wheatherCity.getText().equals(city))
        {
            return;
        }
        getWeatherData();
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.view_pager, null);
        
        mContext = container.getContext();
        preference = mContext.getSharedPreferences("weather", Activity.MODE_PRIVATE);
        showLeft = (View)mView.findViewById(R.id.head_layout_showLeft);
        showRight = (View)mView.findViewById(R.id.head_layout_showRight);
        leftBt = (ImageView)showLeft.findViewById(R.id.head_layout_back);
        rightBt = (ImageView)showRight.findViewById(R.id.head_layout_me);
        
        mTopTitleView = (TextView)showLeft.findViewById(R.id.head_layout_text);
        mTopTitleView.setText(getString(R.string.tab_schedule));
        mTopBackView = (ImageView)showLeft.findViewById(R.id.head_layout_back);
        mTopBackView.setBackgroundResource(R.drawable.showright_selector);
        
        showAdd = (View)mView.findViewById(R.id.head_layout_add);
        addBt = (TextView)showAdd.findViewById(R.id.head_add);
        weatherLayout = (View)mView.findViewById(R.id.head_layout_weather);
        weatherLayout.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0)
            {
                if (!SysUtil.hasNetWorkConection(mContext))
                {
                    return;
                }
                startActivity(new Intent(mActivity, WeatherWebServiceActivity.class));
            }
        });
        wheatherIcon = (ImageView)weatherLayout.findViewById(R.id.whear_icon);
        wheatherCity = (TextView)weatherLayout.findViewById(R.id.whear_city);
        String city = preference.getString("city", "北京");
        wheatherCity.setText(city + " ");
        wheatherTemperature = (TextView)weatherLayout.findViewById(R.id.whear_temperature);
        
        mPager = (ViewPager)mView.findViewById(R.id.vp_list);
        //展示可下拉刷新列表
        
        pagerItemList = new ArrayList<Fragment>();
        
        mPager.setOnPageChangeListener(this);
        
        getWeatherData();
        return mView;
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        
        super.onActivityCreated(savedInstanceState);
        addBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0)
            {
                startActivity(new Intent(mContext, QuickAddActivity.class));
            }
        });
    }
    
    private void getWeatherData()
    {
        long time = preference.getLong("time", 0);
        long cur = System.currentTimeMillis();
        if (cur - time < 1000 * 60 * 60)
        {
            Message msg = new Message();
            msg.what = 0x01;
            Bundle data = new Bundle();
            data.putString("city", preference.getString("city", "北京"));
            data.putInt("icon", preference.getInt("icon", 0));
            data.putString("temperature", preference.getString("temperature", ""));
            msg.setData(data);
            handler.sendMessage(msg);
        }
        else
        {
            if (!SysUtil.hasNetWorkConection(mContext))
            {
                return;
            }
            task = new GetWhearTask();
            task.execute("");
        }
        
    }
    
    class GetWhearTask extends AsyncTask<String, Integer, String>
    {
        
        public GetWhearTask()
        {
            
        }
        
        @Override
        protected String doInBackground(String... arg0)
        {
            String city = preference.getString("city", "北京");
            SoapObject detail = WebServiceUtil.getWeatherByCity(city);
            try
            {
                Message msg = new Message();
                msg.what = 0x01;
                Bundle data = new Bundle();
                data.putString("city", city);
                data.putInt("icon", WeatherUtil.parseIcon(detail.getProperty(10).toString()));
                data.putString("temperature", detail.getProperty(8).toString());
                msg.setData(data);
                handler.sendMessage(msg);
                
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("city", city);
                editor.putInt("icon", WeatherUtil.parseIcon(detail.getProperty(10).toString()));
                editor.putString("temperature", detail.getProperty(8).toString());
                editor.putLong("time", System.currentTimeMillis());
                editor.commit();
            }
            catch (Exception e)
            {
                
            }
            return "";
        }
        
    }
}
