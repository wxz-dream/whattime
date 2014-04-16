package com.whatime.module.books;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.db.Alarm;
import com.whatime.db.User;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.adapter.MyListAdapter;
import com.whatime.framework.ui.pull.XListView;
import com.whatime.framework.ui.pull.XListView.IXListViewListener;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.market.MarketAlarmInfoActivity_;

@EActivity(R.layout.friend_info)
public class FriendInfoActivity extends Activity implements IXListViewListener
{
    private User user;
    
    @ViewById
    ImageView login_iv;
    
    @ViewById
    TextView nick_name;
    
    @ViewById
    Button add_friend;
    
    @ViewById
    XListView friend_alarm_lv;
    
    private MyListAdapter listAdapter;
    
    private List<Alarm> alarms = new ArrayList<Alarm>();
    
    private long startTime;
    
    private long endTime;
    
    private int mPage;
    
    private Context context;
    
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            int state = msg.getData().getInt(ResponseCons.STATE);
            switch (msg.what)
            {
                case 0x001:
                    
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        Toast toast = Toast.makeText(FriendInfoActivity.this, "请求已发送", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(FriendInfoActivity.this, "发送失败，请重新尝试", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
                
                case 0x002:
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        ArrayList list = msg.getData().getParcelableArrayList(ResponseCons.RESINFO);
                        alarms = (List<Alarm>)list.get(0);
                        if (alarms.size() > 0)
                        {
                            listAdapter = new MyListAdapter(context, alarms);
                            friend_alarm_lv.setOnItemClickListener(new OnItemClickListener()
                            {
                                
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                                {
                                    int position = (int)arg3;
                                    if (position != -1)
                                    {
                                        context.startActivity(new Intent(context, MarketAlarmInfoActivity_.class).putExtra("alarm",
                                            alarms.get(position)));
                                    }
                                }
                            });
                            friend_alarm_lv.setAdapter(listAdapter);
                        }
                    }
                    onLoad();
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        user = (User)getIntent().getSerializableExtra("user");
    }
    
    @AfterViews
    void initView()
    {
        nick_name.setText(user.getNickName());
        if (SysUtil.isFriends(user.getUuid()))
        {
            add_friend.setVisibility(View.GONE);
        }
        onRefresh();
        listAdapter = new MyListAdapter(context, alarms);
        friend_alarm_lv.setAdapter(listAdapter);
        friend_alarm_lv.setXListViewListener(this);
        friend_alarm_lv.setOnItemClickListener(new OnItemClickListener()
        {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                int position = (int)arg3;
                if (position != -1)
                {
                    context.startActivity(new Intent(context, MarketAlarmInfoActivity_.class).putExtra("alarm",
                        alarms.get(position)));
                }
            }
        });
    }
    
    @Click
    void add_friend()
    {
        final EditText remark = new EditText(context);
        remark.setHint("请输入验证信息...");
        remark.setLines(5);
        new AlertDialog.Builder(context).setTitle("添加好友")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setView(remark)
            .setPositiveButton("确定", new OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    new RemoteApiImpl().addFriendReq(user.getUuid(), remark.getText().toString(), handler);
                    login_reback_btn();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    @Click
    void login_reback_btn()
    {
        finish();
    }
    
    @Override
    public void onRefresh()
    {
        Calendar startC = Calendar.getInstance(TimeZone.getDefault());
        if (startC.getTimeInMillis() < startTime)
        {
            startC.setTimeInMillis(startTime);
        }
        Calendar endC = Calendar.getInstance(TimeZone.getDefault());
        if (endC.getTimeInMillis() < endTime)
        {
            endC.setTimeInMillis(endTime);
        }
        else
        {
            endC.setTimeInMillis(startC.getTimeInMillis());
        }
        if (endC.getTimeInMillis() <= startC.getTimeInMillis())
        {
            endC.set(Calendar.DAY_OF_YEAR, endC.get(Calendar.DAY_OF_YEAR) + 365);
            endC.set(Calendar.HOUR_OF_DAY, 0);
            endC.set(Calendar.MINUTE, 0);
            endC.set(Calendar.SECOND, 0);
        }
        startTime = startC.getTimeInMillis();
        endTime = endC.getTimeInMillis();
        new RemoteApiImpl().getManAlarm(handler,
            user.getUuid(),
            startTime,
            endTime,
            0);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onLoad();
            }
        }, 4000);
    }
    
    @Override
    public void onLoadMore()
    {
        new RemoteApiImpl().getManAlarm(handler,
            user.getUuid(),
            startTime,
            endTime,
            mPage++);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onLoad();
            }
        }, 4000);
        
    }
    
    private void onLoad()
    {
        friend_alarm_lv.stopRefresh();
        friend_alarm_lv.stopLoadMore();
        friend_alarm_lv.setRefreshTime("刚刚");
        
    }
    
}
