package com.whatime.module.weather.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.weather.util.WeatherUtil;
import com.whatime.module.weather.util.WebServiceUtil;

public class WeatherWebServiceActivity extends Activity
{
    private TextView text;
    
    private Button city_btn;
    
    private static final int CITY = 0x11;
    
    private String city_str;
    
    private TextView city_text;
    
    private Spinner province_spinner;
    
    private Spinner city_spinner;
    
    private HashMap<String, String> pros;
    
    private List<String> provinces;
    
    private HashMap<String, String> cts;
    
    private List<String> citys;
    
    private SharedPreferences preference;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        
        setContentView(R.layout.tqyb);
        preference = getSharedPreferences("weather", MODE_PRIVATE);
        city_str = readSharpPreference("city");
        
        city_text = (TextView)findViewById(R.id.city);
        city_text.setText(city_str);
        
        text = (TextView)findViewById(R.id.test);
        
        city_btn = (Button)findViewById(R.id.city_button);
        
        city_btn.setOnClickListener(new ClickEvent());
        
        findViewById(R.id.content_today_layout).getBackground().setAlpha(120);
        findViewById(R.id.content_small_bg1).getBackground().setAlpha(120);
        findViewById(R.id.content_small_bg2).getBackground().setAlpha(120);
        findViewById(R.id.content_small_bg3).getBackground().setAlpha(120);
        
        refresh(readSharpPreference("code"));
        
    }
    
    class ClickEvent implements View.OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.city_button:
                    if (!SysUtil.hasNetWorkConection(WeatherWebServiceActivity.this))
                    {
                        return;
                    }
                    show_dialog(CITY);
                    
                    break;
                
                default:
                    break;
            }
            
        }
        
    }
    
    public void showTast(String string)
    {
        Toast.makeText(WeatherWebServiceActivity.this, string, 1).show();
        
    }
    
    public void show_dialog(int cityId)
    {
        switch (cityId)
        {
            case CITY:
                
                // 取得city_layout.xml中的视图
                final View view = LayoutInflater.from(this).inflate(R.layout.city_layout, null);
                
                // 省份Spinner
                province_spinner = (Spinner)view.findViewById(R.id.province_spinner);
                // 城市Spinner
                city_spinner = (Spinner)view.findViewById(R.id.city_spinner);
                
                // 省份列表
                pros = MyApp.getInstance().getProvince();
                provinces = new ArrayList<String>();
                for (String p : pros.keySet())
                {
                    provinces.add(p);
                }
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                
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
                            new ArrayAdapter(WeatherWebServiceActivity.this, android.R.layout.simple_spinner_item,
                                citys);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        city_spinner.setAdapter(adapter1);
                        
                    }
                    
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0)
                    {
                        
                    }
                });
                
                // 城市Spinner监听器
                city_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
                {
                    
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        city_str = citys.get(position);
                    }
                    
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0)
                    {
                        
                    }
                });
                
                // 选择城市对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("请选择所属城市");
                dialog.setView(view);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        city_text.setText(city_str);
                        String code = cts.get(city_str);
                        writeSharpPreference(city_str, code);
                        refresh(code);
                        
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        
                    }
                });
                
                dialog.show();
                
                break;
            
            default:
                break;
        }
        
    }
    
    protected void refresh(String code)
    {
        if (!SysUtil.hasNetWorkConection(this))
        {
            return;
        }
        HashMap<String, String> detail = WebServiceUtil.getWeatherByCity(code);
        
        try
        {
            // 2013年12月5日
            TextView today_text = (TextView)findViewById(R.id.today);
            today_text.setText(detail.get("date_y"));
            
            // 海淀
            TextView city_text = (TextView)findViewById(R.id.city_text);
            city_text.setText(detail.get("city"));
            
            //晴
            TextView today_weather = (TextView)findViewById(R.id.today_weather);
            today_weather.setText(detail.get("weather1"));
            
            // 取得<string>15℃/21℃</string>中的数据
            TextView qiweng_text = (TextView)findViewById(R.id.qiweng);
            qiweng_text.setText(detail.get("temp1"));
            
            // 取得<string>今日天气实况：气温：20℃；风向/风力：东南风
            // 2级；湿度：79%</string>中的数据,并通过":"拆分成数组
            TextView shidu_text = (TextView)findViewById(R.id.shidu);
            shidu_text.setText(detail.get("index_tr"));
            
            // 取得<string>东北风3-4级</string>中的数据
            TextView fengli_text = (TextView)findViewById(R.id.fengli);
            fengli_text.setText(detail.get("wind1") + "-" + detail.get("fl1"));
            
            // 取得<string>空气质量：良；紫外线强度：最弱</string>中的数据,并通过";"拆分,再通过":"拆分,拆分两次,取得我们需要的数据
            TextView kongqi_text = (TextView)findViewById(R.id.kongqi);
            kongqi_text.setText(detail.get("index_co"));
            
            TextView zhiwai_text = (TextView)findViewById(R.id.zhiwai);
            zhiwai_text.setText(detail.get("index_uv"));
            
            // 设置小贴士数据
            // <string>穿衣指数：较凉爽，建议着长袖衬衫加单裤等春秋过渡装。年老体弱者宜着针织长袖衬衫、马甲和长裤。感冒指数：虽然温度适宜但风力较大，仍较易发生感冒，体质较弱的朋友请注意适当防护。
            //运动指数：阴天，较适宜开展各种户内外运动。洗车指数：较不宜洗车，路面少量积水，如果执意擦洗汽车，要做好溅上泥水的心理准备。晾晒指数：天气阴沉，不利于水分的迅速蒸发，不太适宜晾晒。若需要晾晒，请尽量选择通风的地点。
            //旅游指数：阴天，风稍大，但温度适宜，总体来说还是好天气。这样的天气很适宜旅游，您可以尽情享受大自然的风光。路况指数：阴天，路面比较干燥，路况较好。舒适度指数：温度适宜，风力不大，您在这样的天气条件下，会感到比较清爽和舒适。
            //空气污染指数：气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。紫外线指数：属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。</string>
            TextView xiaotieshi_text = (TextView)findViewById(R.id.xiaotieshi);
            xiaotieshi_text.setText(detail.get("index_d"));
            
            // 设置当日图片
            ImageView image = (ImageView)findViewById(R.id.imageView1);
            int icon = WeatherUtil.parseIcon("a_" + detail.get("img1"));
            image.setImageResource(icon);
            Calendar cl = Calendar.getInstance();
            cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) + 1);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
            // 取得第二天的天气情况
            TextView tomorrow_date = (TextView)findViewById(R.id.tomorrow_date);
            tomorrow_date.setText(sf.format(cl.getTime()));
            
            TextView tomorrow_qiweng = (TextView)findViewById(R.id.tomorrow_qiweng);
            tomorrow_qiweng.setText(detail.get("temp2"));
            
            TextView tomorrow_tianqi = (TextView)findViewById(R.id.tomorrow_tianqi);
            tomorrow_tianqi.setText(detail.get("weather2"));
            
            ImageView tomorrow_image = (ImageView)findViewById(R.id.tomorrow_image);
            int icon1 = WeatherUtil.parseIcon("a_" + detail.get("img3"));
            tomorrow_image.setImageResource(icon1);
            
            // 取得第三天的天气情况
            cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) + 1);
            TextView afterday_date = (TextView)findViewById(R.id.afterday_date);
            afterday_date.setText(sf.format(cl.getTime()));
            
            TextView afterday_qiweng = (TextView)findViewById(R.id.afterday_qiweng);
            afterday_qiweng.setText(detail.get("temp3"));
            
            TextView afterday_tianqi = (TextView)findViewById(R.id.afterday_tianqi);
            afterday_tianqi.setText(detail.get("weather3"));
            
            ImageView afterday_image = (ImageView)findViewById(R.id.afterday_image);
            int icon2 = WeatherUtil.parseIcon("a_" + detail.get("img5"));
            afterday_image.setImageResource(icon2);
            
            // 取得第四天的天气情况
            cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) + 1);
            TextView nextday_date = (TextView)findViewById(R.id.nextday_date);
            nextday_date.setText(sf.format(cl.getTime()));
            
            TextView nextday_qiweng = (TextView)findViewById(R.id.nextday_qiweng);
            nextday_qiweng.setText(detail.get("temp4"));
            
            TextView nextday_tianqi = (TextView)findViewById(R.id.nextday_tianqi);
            nextday_tianqi.setText(detail.get("weather4"));
            
            ImageView nextday_image = (ImageView)findViewById(R.id.nextday_image);
            int icon3 = WeatherUtil.parseIcon("a_" + detail.get("img7"));
            nextday_image.setImageResource(icon3);
            
        }
        catch (Exception e)
        {
        }
        
    }
    
    public void writeSharpPreference(String string, String code)
    {
        
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("city", string);
        editor.putString("code", code);
        editor.commit();
        
    }
    
    public String readSharpPreference(String key)
    {
        String ret = "";
        if (key.equals("city"))
        {
            ret = preference.getString("city", "北京");
        }
        else if (key.equals("code"))
        {
            ret = preference.getString("code", "101010200");
        }
        
        return ret;
        
    }
}
