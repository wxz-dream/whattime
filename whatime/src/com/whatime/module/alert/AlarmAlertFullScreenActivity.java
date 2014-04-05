/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whatime.module.alert;

import java.util.Calendar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AlarmCons;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.service.AlarmUtil;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.receive.AlarmReceiver;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm
 * tone. This activity is the full screen version which shows over the lock
 * screen with the wallpaper as the background.
 */
public class AlarmAlertFullScreenActivity extends Activity
{
    
    // These defaults must match the values in res/xml/settings.xml
    private static final String DEFAULT_SNOOZE = "10";
    
    private static final String DEFAULT_VOLUME_BEHAVIOR = "2";
    
    protected static final String SCREEN_OFF = "screen_off";
    
    private static final int ALARM_STREAM_TYPE_BIT = 1 << AudioManager.STREAM_ALARM;
    
    public static final String KEY_ALARM_IN_SILENT_MODE = "alarm_in_silent_mode";
    
    public static final String KEY_ALARM_SNOOZE = "snooze_duration";
    
    public static final String KEY_VOLUME_BEHAVIOR = "volume_button_setting";
    
    protected Alarm mAlarm;
    
    private int mVolumeBehavior;
    
    private SliderRelativeLayout sliderLayout = null;

    private ImageView imgView_getup_arrow; // 动画图片
    private AnimationDrawable animArrowDrawable = null;

    public static int MSG_LOCK_SUCESS = 1;
    
    private AlarmController controller = new AlarmController();
    
    // Receives the ALARM_KILLED action from the AlarmKlaxon,
    // and also ALARM_SNOOZE_ACTION / ALARM_DISMISS_ACTION from other applications
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(AlarmServiceCons.ALARM_SNOOZE_ACTION))
            {
                snooze();
            }
            else if (action.equals(AlarmServiceCons.ALARM_DISMISS_ACTION))
            {
                dismiss(false);
            }
            else
            {
                Alarm alarm =
                    DBHelper.getInstance().getAlarmById(intent.getLongExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, -1));
                
                if (alarm != null && mAlarm.getId() == alarm.getId())
                {
                    dismiss(true);
                }
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        mAlarm = DBHelper.getInstance().getAlarmById(getIntent().getLongExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, -1));
        // Get the volume/camera button behavior setting
        final String vol =
            PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_VOLUME_BEHAVIOR, DEFAULT_VOLUME_BEHAVIOR);
        mVolumeBehavior = Integer.parseInt(vol);
        
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Turn on the screen unless we are being launched from the AlarmAlert
        // subclass.
        if (!getIntent().getBooleanExtra(SCREEN_OFF, false))
        {
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
        
        setContentView(R.layout.alarm_alert);
        initViews();
        
        sliderLayout.setMainHandler(mHandler);
        updateLayout();
        
        // Register to get the alarm killed/snooze/dismiss intent.
        IntentFilter filter = new IntentFilter(AlarmServiceCons.ALARM_KILLED);
        filter.addAction(AlarmServiceCons.ALARM_SNOOZE_ACTION);
        filter.addAction(AlarmServiceCons.ALARM_DISMISS_ACTION);
        registerReceiver(mReceiver, filter);
    }
    
    private void initViews(){
        sliderLayout = (SliderRelativeLayout)findViewById(R.id.slider_layout);
        //获得动画，并开始转动
        imgView_getup_arrow = (ImageView)findViewById(R.id.getup_arrow);
        animArrowDrawable = (AnimationDrawable) imgView_getup_arrow.getBackground() ;
    }
    @Override
    protected void onPause() {
        super.onPause();
        animArrowDrawable.stop();
    }
    
    //通过延时控制当前绘制bitmap的位置坐标
    private Runnable AnimationDrawableTask = new Runnable(){
        
        public void run(){
            animArrowDrawable.start();
            mHandler.postDelayed(AnimationDrawableTask, 300);
        }
    };
    
    private Handler mHandler =new Handler (){
        
        public void handleMessage(Message msg){
            
            if(MSG_LOCK_SUCESS == msg.what)
                dismiss(false);
                finish(); // 锁屏成功时，结束我们的Activity界面
        }
    };
    
    //屏蔽掉Home键
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }
    
    //屏蔽掉Back键
    public boolean onKeyDown(int keyCode ,KeyEvent event){
        
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true ;
        else
            return super.onKeyDown(keyCode, event);
        
    }

    private void setTitle()
    {
        Task task = mAlarm.getTask();
        String label = "";
        if (task != null && task.getDes() != null)
        {
            label = task.getDes();
        }
        TextView title = (TextView)findViewById(R.id.alertTitle);
        title.setText(label);
    }
    
    private void updateLayout()
    {
        
        /* snooze behavior: pop a snooze confirmation view, kick alarm
           manager. */
        Button snooze = (Button)findViewById(R.id.snooze);
        snooze.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                snooze();
            }
        });
        
        /* Set the title from the passed in alarm */
        setTitle();
    }
    
    // Attempt to snooze this alert.
    private void snooze()
    {
        // Do not snooze if the snooze button is disabled.
        if (!findViewById(R.id.snooze).isEnabled())
        {
            dismiss(false);
            return;
        }
        final String snooze =
            PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_ALARM_SNOOZE, DEFAULT_SNOOZE);
        int snoozeMinutes = Integer.parseInt(snooze);
        
        final long snoozeTime = mAlarm.getAlarmTime() + (1000 * 60 * snoozeMinutes);
        //修改此活动时间
        Task t = mAlarm.getTask();
        if (t.getSetTime() == null)
        {
            t.setSetTime(t.getAlarmTime());
        }
        else if (t.getSetTime() < t.getAlarmTime())
        {
            t.setAlarmTime(snoozeTime);
        }
        t.setAlarmTime(snoozeTime);
        t.setOpen(true);
        DBHelper.getInstance().uptTask(t);
        mAlarm.setOpen(true);
        //重新赋值
        Task currentTask = DBHelper.getInstance().getNextTaskByAlarmId(mAlarm.getId());
        mAlarm.setTask(currentTask);
        mAlarm.setAlarmTime(currentTask.getAlarmTime());
        controller.uptAlarm(mAlarm, new Handler());
        
        // Get the display time for the snooze and update the notification.
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(snoozeTime);
        
        // Append (snoozed) to the label.
        Task task = mAlarm.getTask();
        String label = "";
        if (task != null && task.getDes() != null)
        {
            label = task.getDes();
        }
        // Notify the user that the alarm has been snoozed.
        Intent cancelSnooze = new Intent(this, AlarmReceiver.class);
        cancelSnooze.setAction(AlarmServiceCons.CANCEL_SNOOZE);
        cancelSnooze.putExtra(AlarmServiceCons.ALARM_ID, mAlarm.getId());
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, cancelSnooze, 0);
        NotificationManager nm = getNotificationManager();
        Notification n = new Notification(R.drawable.stat_notify_alarm, label, 0);
        n.setLatestEventInfo(this,
            label,
            getString(R.string.alarm_notify_snooze_text, AlarmUtil.formatTime(this, c)),
            broadcast);
        n.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
        nm.notify(0, n);
        label = getString(R.string.alarm_notify_snooze_label, label);
        String displayTime = getString(R.string.alarm_alert_snooze_set, snoozeMinutes);
        // Intentionally log the snooze time for debugging.
        Log.v("wangxianming", " AlarmAlertFullScreen" + displayTime);
        
        // Display the snooze minutes in a toast.
        Toast.makeText(AlarmAlertFullScreenActivity.this, displayTime, Toast.LENGTH_LONG).show();
        stopService(new Intent(AlarmCons.ALARM_ALERT_ACTION));
        finish();
    }
    
    private NotificationManager getNotificationManager()
    {
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    
    // Dismiss the alarm.
    private void dismiss(boolean killed)
    {
        // The service told us that the alarm has been killed, do not modify
        // the notification or stop the service.
        if (!killed)
        {
            // Cancel the notification and stop playing the alarm
            NotificationManager nm = getNotificationManager();
            nm.cancel(0);
            stopService(new Intent(AlarmCons.ALARM_ALERT_ACTION));
        }
        finish();
    }
    
    /**
     * this is called when a second alarm is triggered while a
     * previous alert window is still active.
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        
        Log.v("wangxianming", "AlarmAlert.OnNewIntent()");
        
        mAlarm = intent.getParcelableExtra(AlarmServiceCons.ALARM_INTENT_EXTRA);
        
        setTitle();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        // If the alarm was deleted at some point, disable snooze.
        if (controller.getAlarmById(mAlarm.getId()) == null)
        {
            Button snooze = (Button)findViewById(R.id.snooze);
            snooze.setEnabled(false);
        }
        mHandler.postDelayed(AnimationDrawableTask, 300);  //开始绘制动画
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v("wangxianming", "AlarmAlert.onDestroy()");
        // No longer care about the alarm being killed.
        unregisterReceiver(mReceiver);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        // Do this on key down to handle a few of the system keys.
        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        switch (event.getKeyCode())
        {
        // Volume keys and camera keys dismiss the alarm
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_FOCUS:
                if (up)
                {
                    switch (mVolumeBehavior)
                    {
                        case 1:
                            snooze();
                            break;
                        
                        case 2:
                            dismiss(false);
                            break;
                        
                        default:
                            break;
                    }
                }
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    public void onBackPressed()
    {
        snooze();
        return;
    }
}
