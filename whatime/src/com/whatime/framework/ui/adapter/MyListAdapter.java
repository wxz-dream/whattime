package com.whatime.framework.ui.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.BitmapUtils;
import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.books.FriendInfoActivity_;
import com.whatime.module.login.LoginActivity;

public class MyListAdapter extends BaseAdapter implements ListAdapter
{
    private Context context;
    
    private List<Alarm> alarms;
    
    private List<User> users = new ArrayList<User>();
    
    private AlarmController controller = new AlarmController();
    
    private BitmapUtils bitmapUtils;
    
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
                        
                        notifyDataSetChanged();
                    }
                    break;
            }
        };
    };
    
    public MyListAdapter(Context context, List<Alarm> alarms)
    {
        this.context = context;
        this.alarms = alarms;
        bitmapUtils = new BitmapUtils(context);
    }
    
    @Override
    public View getView(final int i, View view, ViewGroup viewgroup)
    {
        final Alarm alarm = alarms.get(i);
        final User user = new RemoteApiImpl().getUserByUuid(alarm.getUserUuid());
        View v = LayoutInflater.from(context).inflate(R.layout.market_item, null);
        ImageView userPhoto = (ImageView)v.findViewById(R.id.item_user_photo);
        userPhoto.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (user != null)
                {
                    context.startActivity(new Intent(context, FriendInfoActivity_.class).putExtra("user", users.get(i)));
                }
            }
        });
        if (user!=null&&user.getUserphotoUri() != null)
        {
            bitmapUtils.display(userPhoto, user.getUserphotoUri());
        }
        ImageView item_add = (ImageView)v.findViewById(R.id.item_add);
        if (DBHelper.getInstance().isExist(alarm.getUuid()))
        {
            item_add.setImageResource(R.drawable.added_live_clock);
        }
        else
        {
            item_add.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    User me = MyApp.getInstance().getUser();
                    if (me != null)
                    {
                        new RemoteApiImpl().joinAlarm(alarm.getUuid(), handler);
                        List<Task> myTasks = alarm.getTasks();
                        String uuid = UUID.randomUUID().toString();
                        alarm.setUuid(uuid);
                        DBHelper.getInstance().addAlarm(alarm);
                        for (Task task : myTasks)
                        {
                            task.setUuid(UUID.randomUUID().toString());
                            task.setAlarm(alarm);
                            task.setAlarmUuid(alarm.getUuid());
                            DBHelper.getInstance().addTask(task);
                        }
                        controller.addAlarm(alarm, handler);
                      //share
                        if (alarm.getShare() != null && alarm.getShare().contains("2"))
                        {
                            String url = "http://whatime.duapp.com/android/android";
                            Calendar c = Calendar.getInstance(TimeZone.getDefault());
                            c.setTimeInMillis(alarm.getAlarmTime());
                            StringBuilder title = new StringBuilder();
                            title.append("天天有约(")
                                .append(c.get(Calendar.MONTH) + 1)
                                .append(".")
                                .append(c.get(Calendar.DAY_OF_MONTH))
                                .append(")一起来[")
                                .append(alarm.getTitle())
                                .append("]吧");
                            StringBuilder des = new StringBuilder();
                            des.append("快下载APP[天天有约]来参加吧。下载地址：")
                                .append(url)
                                .append("\n*详情：\n")
                                .append("*主题：")
                                .append(alarm.getTitle())
                                .append("\n")
                                .append("*时间：")
                                .append(c.get(Calendar.MONTH) + 1)
                                .append(".")
                                .append(c.get(Calendar.DAY_OF_MONTH))
                                .append("\n")
                                .append("*描述：")
                                .append(alarm.getDes());
                            ShareSDK.initSDK(context);
                            showOnekeyshare(null, false, url, title.toString(), des.toString());
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            });
        }
        StringBuilder sb = new StringBuilder();
        users.add(user);
        TextView item_createtime = (TextView)v.findViewById(R.id.item_createtime);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(alarm.getCreateTime());
        if (user != null)
        {
            users.add(i, user);
            if(user.getNickName()!=null)
            {
                sb.append(user.getNickName());
            }
        }
        sb.append(" * ")
            .append(c.get(Calendar.YEAR))
            .append("-")
            .append(c.get(Calendar.MONTH) + 1)
            .append("-")
            .append(c.get(Calendar.DAY_OF_MONTH))
            .append(" ")
            .append(c.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(c.get(Calendar.MINUTE)));
        item_createtime.setText(sb.toString());
        TextView alarmTitle = (TextView)v.findViewById(R.id.item_alarm_title);
        alarmTitle.setText(alarm.getTitle());
        sb.setLength(0);
        TextView item_des = (TextView)v.findViewById(R.id.item_des);
        sb.append(alarm.getDes());
        item_des.setText(sb.toString());
        TextView alarmTime = (TextView)v.findViewById(R.id.item_alarm_alarmTime);
        Calendar alarmC = Calendar.getInstance(TimeZone.getDefault());
        alarmC.setTimeInMillis(alarm.getAlarmTime());
        StringBuilder alarmLabel = new StringBuilder()
            .append(alarmC.get(Calendar.YEAR))
            .append("-")
            .append(alarmC.get(Calendar.MONTH) + 1)
            .append("-")
            .append(alarmC.get(Calendar.DAY_OF_MONTH))
            .append(" ")
            .append(alarmC.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(alarmC.get(Calendar.MINUTE)));
        alarmTime.setText(alarmLabel);
        TextView joinNum = (TextView)v.findViewById(R.id.item_joinNum);
        long num = alarm.getJoinNum();
        if(num<10000)
        {
            joinNum.setText(String.valueOf(num));
        }
        else
        {
            joinNum.setText(String.valueOf(num/10000)+"W+");
        }
        
        return v;
    }
    private void showOnekeyshare(String platform, boolean silent, String url, String title, String des)
    {
        OnekeyShare oks = new OnekeyShare();
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, context.getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(des);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        // 是否直接分享（true则直接分享）
        oks.setSilent(silent);
        // 指定分享平台，和slient一起使用可以直接分享到指定的平台
        if (platform != null)
        {
            oks.setPlatform(platform);
        }
        
        oks.show(context);
    }
    
    @Override
    public void registerDataSetObserver(DataSetObserver datasetobserver)
    {
    }
    
    @Override
    public void unregisterDataSetObserver(DataSetObserver datasetobserver)
    {
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
        return i;
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
    
    public void setAlarms(List<Alarm> alarms)
    {
        this.alarms = alarms;
    }
    
}
