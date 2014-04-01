/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.whatime.framework.aservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.service.AlarmAlertWakeLock;
import com.whatime.controller.service.AlarmUtil;
import com.whatime.db.Alarm;
import com.whatime.db.Task;
import com.whatime.framework.ui.activity.MainActivity;

/**
 * Manages alarms and vibe. Runs as a service so that it can continue to play
 * if another activity overrides the AlarmAlert dialog.
 */
public class AlarmCenterAService extends Service
{
    private boolean mReflectFlg = false;
    
    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service  
    
    private static final Class<?>[] mSetForegroundSignature = new Class[] {boolean.class};
    
    private static final Class<?>[] mStartForegroundSignature = new Class[] {int.class, Notification.class};
    
    private static final Class<?>[] mStopForegroundSignature = new Class[] {boolean.class};
    
    private NotificationManager mNM;
    
    private Method mSetForeground;
    
    private Method mStartForeground;
    
    private Method mStopForeground;
    
    private Object[] mSetForegroundArgs = new Object[1];
    
    private Object[] mStartForegroundArgs = new Object[2];
    
    private Object[] mStopForegroundArgs = new Object[1];
    
    /** Play alarm up to 10 minutes before silencing */
    private static final int ALARM_TIMEOUT_SECONDS = 10 * 60;
    
    private static final long[] sVibratePattern = new long[] {500, 500};
    
    private boolean mPlaying = false;
    
    private Vibrator mVibrator;
    
    private MediaPlayer mMediaPlayer;
    
    private Alarm mCurrentAlarm;
    
    private long mStartTime;
    
    private TelephonyManager mTelephonyManager;
    
    private int mInitialCallState;
    
    private AudioManager mAudioManager = null;
    
    private boolean mCurrentStates = true;
    
    private AlarmController controller = new AlarmController();
    
    // Internal messages
    private static final int KILLER = 1;
    
    private static final int FOCUSCHANGE = 2;
    
    Notification notification;
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case KILLER:
                    Log.v("wangxianming", "*********** Alarm killer triggered ***********");
                    sendKillBroadcast((Alarm)msg.obj);
                    stopSelf();
                    break;
                case FOCUSCHANGE:
                    switch (msg.arg1)
                    {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            
                            if (!mPlaying && mMediaPlayer != null)
                            {
                                stop();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            
                            if (!mPlaying && mMediaPlayer != null)
                            {
                                mMediaPlayer.pause();
                                mCurrentStates = false;
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            
                            if (mPlaying && !mCurrentStates)
                            {
                                play(mCurrentAlarm);
                            }
                            break;
                        default:
                            
                            break;
                    }
                default:
                    break;
            
            }
        }
    };
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
    {
        @Override
        public void onCallStateChanged(int state, String ignored)
        {
            // The user might already be in a call when the alarm fires. When
            // we register onCallStateChanged, we get the initial in-call state
            // which kills the alarm. Check against the initial call state so
            // we don't kill the alarm during a call.
            if (state != TelephonyManager.CALL_STATE_IDLE && state != mInitialCallState && mCurrentAlarm != null)
            {
                sendKillBroadcast(mCurrentAlarm);
                stopSelf();
            }
        }
    };
    
    @Override
    public void onCreate()
    {
        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        try
        {
            mStartForeground = AlarmCenterAService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = AlarmCenterAService.class.getMethod("stopForeground", mStopForegroundSignature);
        }
        catch (NoSuchMethodException e)
        {
            mStartForeground = mStopForeground = null;
        }
        
        try
        {
            mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
        }
        
        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        // Listen for incoming calls to kill the alarm.
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        AlarmAlertWakeLock.acquireCpuWakeLock(this);
        controller.disableExpiredAlarms();
        controller.setNextAlert();
        controller.sync();
        uptNot();
        
    }
    
    private void uptNot()
    {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification = new Notification();
        notification.icon = R.drawable.icon;
        notification.tickerText = "天天有约";
        notification.when = System.currentTimeMillis();
        String title = "天天有约";
        String time = "详情";
        Alarm a = controller.getNextAlarm();
        if (a != null)
        {
            Task t = a.getTask();
            if(t==null)
            {
                return;
            }
            title = t.getTitle();
            if(title.length()==0)
            {
                title = a.getTitle();
            }
            time = AlarmUtil.getShowTime(this, a.getAlarmTime());
            
        }
        notification.setLatestEventInfo(this, title, time, contentIntent);
        startForegroundCompat(NOTIFICATION_ID, notification);
    }
    
    void invokeMethod(Method method, Object[] args)
    {
        try
        {
            method.invoke(this, args);
        }
        catch (InvocationTargetException e)
        {
            // Should not happen.  
            Log.w("ApiDemos", "Unable to invoke method", e);
        }
        catch (IllegalAccessException e)
        {
            // Should not happen.  
            Log.w("ApiDemos", "Unable to invoke method", e);
        }
    }
    
    /** 
     * This is a wrapper around the new startForeground method, using the older 
     * APIs if it is not available. 
     */
    void startForegroundCompat(int id, Notification notification)
    {
        if (mReflectFlg)
        {
            // If we have the new startForeground API, then use it.  
            if (mStartForeground != null)
            {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }
            
            // Fall back on the old API.  
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        }
        else
        {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行， 
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行 */
            
            if (VERSION.SDK_INT >= 5)
            {
                startForeground(id, notification);
            }
            else
            {
                // Fall back on the old API.  
                mSetForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
                mNM.notify(id, notification);
            }
        }
    }
    
    /** 
     * This is a wrapper around the new stopForeground method, using the older 
     * APIs if it is not available. 
     */
    void stopForegroundCompat(int id)
    {
        if (mReflectFlg)
        {
            // If we have the new stopForeground API, then use it.  
            if (mStopForeground != null)
            {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }
            
            // Fall back on the old API.  Note to cancel BEFORE changing the  
            // foreground state, since we could be killed at that point.  
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        }
        else
        {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行， 
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行 */
            
            if (VERSION.SDK_INT >= 5)
            {
                stopForeground(true);
            }
            else
            {
                // Fall back on the old API.  Note to cancel BEFORE changing the  
                // foreground state, since we could be killed at that point.  
                mNM.cancel(id);
                mSetForegroundArgs[0] = Boolean.FALSE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
            }
        }
    }
    
    @SuppressLint("NewApi")
    @Override
    public void onDestroy()
    {
        stop();
        // Stop listening for incoming calls.
        mTelephonyManager.listen(mPhoneStateListener, 0);
        AlarmAlertWakeLock.releaseCpuLock();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        // 重启服务
        Intent localIntent = new Intent();
        localIntent.setClass(this, AlarmCenterAService.class);
        this.startService(localIntent);
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // No intent, tell the system not to restart us.
        if (intent == null)
        {
            stopSelf();
            return START_NOT_STICKY;
        }
        if ("com.whatime.ALARM_ALERT".equals(intent.getAction()))
        {
            final Alarm alarm = controller.getAlarmById(intent.getLongExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, -1));
            
            if (alarm == null)
            {
                Log.v("wangxianming", "AlarmKlaxon failed to parse the alarm from the intent");
                stopSelf();
                return START_NOT_STICKY;
            }
            
            if (mCurrentAlarm != null)
            {
                sendKillBroadcast(mCurrentAlarm);
            }
            
            play(alarm);
            mCurrentAlarm = alarm;
            // Record the initial call state here so that the new alarm has the
            // newest state.
            mInitialCallState = mTelephonyManager.getCallState();
            uptNot();
        }
        else if (AlarmServiceCons.UPT_NOTIFICATION.equals(intent.getAction()))
        {
            uptNot();
        }
        return START_STICKY;
    }
    
    private void sendKillBroadcast(Alarm alarm)
    {
        long millis = System.currentTimeMillis() - mStartTime;
        int minutes = (int)Math.round(millis / 60000.0);
        Intent alarmKilled = new Intent(AlarmServiceCons.ALARM_KILLED);
        alarmKilled.putExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, alarm.getId());
        alarmKilled.putExtra(AlarmServiceCons.ALARM_KILLED_TIMEOUT, minutes);
        sendBroadcast(alarmKilled);
    }
    
    // Volume suggested by media team for in-call alarms.
    private static final float IN_CALL_VOLUME = 0.125f;
    
    @SuppressLint("NewApi")
    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener()
    {
        public void onAudioFocusChange(int focusChange)
        {
            mHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };
    
    @SuppressLint("NewApi")
    private void play(Alarm alarm)
    {
        // stop() checks to see if we are already playing.
        mAudioManager.requestAudioFocus(mAudioFocusListener,
            AudioManager.STREAM_ALARM,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        stop();
        Uri alert;
        Task task = alarm.getTask();
        if (task == null)
        {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        else
        {
            String music = task.getMusic();
            if (music == null || music.length() == 0)
            {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
            else
            {
                alert = Uri.parse(music);
            }
        }
        
        // Fall back on the default alarm if the database does not have an
        // alarm stored.
        if (alert == null)
        {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Log.v("wangxianming", "Using default alarm: " + alert.toString());
        }
        
        // TODO: Reuse mMediaPlayer instead of creating a new one and/or use
        // RingtoneManager.
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new OnErrorListener()
        {
            public boolean onError(MediaPlayer mp, int what, int extra)
            {
                Log.v("wangxianming", "Error occurred while playing audio.");
                mp.stop();
                mp.release();
                mMediaPlayer = null;
                return true;
            }
        });
        
        try
        {
            // Check if we are in a call. If we are, use the in-call alarm
            // resource at a low volume to not disrupt the call.
            if (mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE)
            {
                Log.v("wangxianming", "Using the in-call alarm");
                mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
                setDataSourceFromResource(getResources(), mMediaPlayer, R.raw.in_call_alarm);
            }
            else
            {
                mMediaPlayer.setDataSource(this, alert);
            }
            startAlarm(mMediaPlayer);
        }
        catch (Exception ex)
        {
            Log.v("wangxianming", "Using the fallback ringtone");
            // The alert may be on the sd card which could be busy right
            // now. Use the fallback ringtone.
            try
            {
                // Must reset the media player to clear the error state.
                mMediaPlayer.reset();
                setDataSourceFromResource(getResources(), mMediaPlayer, R.raw.fallbackring);
                startAlarm(mMediaPlayer);
            }
            catch (Exception ex2)
            {
                // At this point we just don't play anything.
                Log.v("wangxianming", "Failed to play fallback ringtone" + ex2);
            }
        }
        
        /* Start the vibrator after everything is ok with the media player */
        if (task == null || task.getShake())
        {
            mVibrator.vibrate(sVibratePattern, 0);
        }
        else
        {
            mVibrator.cancel();
        }
        
        enableKiller(alarm);
        mPlaying = true;
        mStartTime = System.currentTimeMillis();
    }
    
    // Do the common stuff when starting the alarm.
    private void startAlarm(MediaPlayer player)
        throws java.io.IOException, IllegalArgumentException, IllegalStateException
    {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // do not play alarms if stream volume is 0
        // (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
        {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            player.start();
            new Timer().schedule(new TimerTask()
            {
                
                @Override
                public void run()
                {
                    stopSelf();
                }
            }, 1000 * 60);
        }
    }
    
    private void setDataSourceFromResource(Resources resources, MediaPlayer player, int res)
        throws java.io.IOException
    {
        AssetFileDescriptor afd = resources.openRawResourceFd(res);
        if (afd != null)
        {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        }
    }
    
    /**
     * Stops alarm audio and disables alarm if it not snoozed and not
     * repeating
     */
    public void stop()
    {
        Log.v("wangxianming", "AlarmKlaxon.stop()");
        if (mPlaying)
        {
            mPlaying = false;
            
            Intent alarmDone = new Intent(AlarmServiceCons.ALARM_DONE_ACTION);
            sendBroadcast(alarmDone);
            
            // Stop audio playing
            if (mMediaPlayer != null)
            {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            
            // Stop vibrator
            mVibrator.cancel();
        }
        disableKiller();
    }
    
    /**
     * Kills alarm audio after ALARM_TIMEOUT_SECONDS, so the alarm
     * won't run all day.
     *
     * This just cancels the audio, but leaves the notification
     * popped, so the user will know that the alarm tripped.
     */
    private void enableKiller(Alarm alarm)
    {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(KILLER, alarm), 1000 * ALARM_TIMEOUT_SECONDS);
    }
    
    private void disableKiller()
    {
        mHandler.removeMessages(KILLER);
    }
    
}
