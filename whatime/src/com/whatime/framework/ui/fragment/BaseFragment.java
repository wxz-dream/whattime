package com.whatime.framework.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.whatime.R;
import com.whatime.framework.ui.activity.MainActivity;
import com.whatime.framework.ui.view.ScrollableTabView;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.addcolock.QuickAddActivity;
import com.whatime.module.weather.activity.WeatherWebServiceActivity;
import com.whatime.module.weather.util.WeatherUtil;
import com.whatime.module.weather.util.WebServiceUtil;

public class BaseFragment extends Fragment implements OnRefreshListener<ListView>, ViewPager.OnPageChangeListener
{
    
    private View showLeft;
    
    private View showRight;
    
    private View showAdd;
    
    private ImageView leftBt;
    
    private ImageView rightBt;
    
    private TextView addBt;
    
    //标题
    protected TextView mTopTitleView;
    
    //图片
    protected ImageView mTopBackView;
    
    protected ViewPager mPager;
    
    protected ArrayList<Fragment> pagerItemList = null;
    
    private View weatherLayout;
    
    private SharedPreferences preference;
    
    private Context mContext;
    
    private ImageView wheatherIcon;
    
    private TextView wheatherCity;
    
    private TextView wheatherTemperature;
    
    protected Activity mActivity;
    
    protected ScrollableTabView mScrollableTabView;
    
    
    private GetWhearTask task;
    
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
    
    public BaseFragment()
    {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
    
    private void getWeatherData()
    {
        if (!SysUtil.hasNetWorkConection(mContext))
        {
            return;
        }
        task = new GetWhearTask();
        task.execute("");
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
            String code = preference.getString("code", "101010200");
            HashMap<String, String> detail = WebServiceUtil.getWeatherByCity(code);
            try
            {
                Message msg = new Message();
                msg.what = 0x01;
                Bundle data = new Bundle();
                data.putString("city", city);
                data.putInt("icon", WeatherUtil.parseIcon("a_" + detail.get("img1")));
                data.putString("temperature", detail.get("temp1"));
                msg.setData(data);
                handler.sendMessage(msg);
                
            }
            catch (Exception e)
            {
                
            }
            return "";
        }
        
    }
    
    
    
    public ViewPager getViewPage()
    {
        return mPager;
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        
        super.onActivityCreated(savedInstanceState);
        
        leftBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).showLeft();
            }
        });
        
        rightBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).showRight();
            }
        });
        addBt.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0)
            {
                startActivity(new Intent(mContext,QuickAddActivity.class));
            }
        });
    }
    
    public boolean isFirst()
    {
        if (mPager.getCurrentItem() == 0)
            return true;
        else
            return false;
    }
    
    public boolean isEnd()
    {
        if (mPager.getCurrentItem() == pagerItemList.size() - 1)
            return true;
        else
            return false;
    }
    
    public class MyAdapter extends FragmentPagerAdapter
    {
        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }
        
        @Override
        public int getCount()
        {
            return pagerItemList.size();
        }
        
        @Override
        public Fragment getItem(int position)
        {
            
            Fragment fragment = null;
            if (position < pagerItemList.size())
                fragment = pagerItemList.get(position);
            else
                fragment = pagerItemList.get(0);
            
            return fragment;
            
        }
    }
    
    private MyPageChangeListener myPageChangeListener;
    
    public void setMyPageChangeListener(MyPageChangeListener l)
    {
        
        myPageChangeListener = l;
        
    }
    
    public interface MyPageChangeListener
    {
        public void onPageSelected(int position);
    }
    
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        new GetDataTask(refreshView).execute();
    }
    
    private static class GetDataTask extends AsyncTask<Void, Void, Void>
    {
        
        PullToRefreshBase<?> mRefreshedView;
        
        public GetDataTask(PullToRefreshBase<?> refreshedView)
        {
            mRefreshedView = refreshedView;
        }
        
        @Override
        protected Void doInBackground(Void... params)
        {
            // Simulates a background job.
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException e)
            {
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            mRefreshedView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
    
    @Override
    public void onPageScrollStateChanged(int position)
    {
        
    }
    
    @Override
    public void onPageScrolled(int position, float arg1, int arg2)
    {
        
    }
    
    @Override
    public void onPageSelected(int position)
    {
        if (myPageChangeListener != null)
        {
            myPageChangeListener.onPageSelected(position);
        }
        if (mScrollableTabView != null)
        {
            mScrollableTabView.selectTab(position);
        }
        
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        pagerItemList.clear();
        pagerItemList = null;
        mScrollableTabView = null;
        mActivity = null;
    }
}
