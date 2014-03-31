/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.whatime.framework.receive;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AlarmCons;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.service.AlarmAlertWakeLock;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.module.addcolock.QuickAddActivity;
import com.whatime.module.alert.AlarmAlertActivity;
import com.whatime.module.alert.AlarmAlertFullScreenActivity;

/**
 * Glue class: connects AlarmAlert IntentReceiver to AlarmAlert
 * activity.  Passes through Alarm ID.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /** If the alarm is older than STALE_WINDOW, ignore.  It
        is probably the result of a time or timezone change */
    private final static int STALE_WINDOW = 30 * 60 * 1000;
    private AlarmController controller = new AlarmController();

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (AlarmServiceCons.ALARM_KILLED.equals(intent.getAction())) {
            Alarm alarm = DBHelper.getInstance().getAlarmById(intent.getLongExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, -1));
            // The alarm has been killed, update the notification
            updateNotification(context, alarm,
                    intent.getIntExtra(AlarmServiceCons.ALARM_KILLED_TIMEOUT, -1));
            return;
        } else if (AlarmServiceCons.CANCEL_SNOOZE.equals(intent.getAction())) {
            return;
        } else if (!AlarmCons.ALARM_ALERT_ACTION.equals(intent.getAction())) {
            // Unknown intent, bail.
            return;
        }
        Alarm alarm = null;
        alarm = DBHelper.getInstance().getAlarmById(intent.getLongExtra(AlarmCons.ALARM_RAW_DATA, -1));
        if (alarm == null||alarm.getDel()||alarm.getAlarmTime()>System.currentTimeMillis()) {
            Log.v("wangxianming", "Failed to parse the alarm from the intent");
            // Make sure we set the next alert if needed.
            controller.setNextAlert();
            return;
        }
        // Disable this alarm if it does not repeat.
        long alertTime = alarm.getAlarmTime();
        Log.e("xpf", "1="+alertTime);
        controller.setNextAlert();
        
        // Intentionally verbose: always log the alarm time to provide useful
        // information in bug reports.
        long now = System.currentTimeMillis();
        // Always verbose to track down time change problems.
        Log.e("xpf", "3="+alertTime);
        if (now > alertTime + STALE_WINDOW) {
            Log.v("wangxianming", "Ignoring stale alarm");
            return;
        }

        // Maintain a cpu wake lock until the AlarmAlert and AlarmKlaxon can
        // pick it up.
        AlarmAlertWakeLock.acquireCpuWakeLock(context);

        /* Close dialogs and window shade */
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);

        // Decide which activity to start based on the state of the keyguard.
        Class c = AlarmAlertActivity.class;
        KeyguardManager km = (KeyguardManager) context.getSystemService(
                Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            // Use the full screen activity for security.
            c = AlarmAlertFullScreenActivity.class;
        }

        // Play the alarm alert and vibrate the device.
        Intent playAlarm = new Intent(AlarmCons.ALARM_ALERT_ACTION);
        playAlarm.putExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, alarm.getId());
        context.startService(playAlarm);

        // Trigger a notification that, when clicked, will show the alarm alert
        // dialog. No need to check for fullscreen since this will always be
        // launched from a user action.
        Intent notify = new Intent(context, AlarmAlertActivity.class);
        notify.putExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, alarm.getId());
        PendingIntent pendingNotify = PendingIntent.getActivity(context,
            alarm.getId().intValue(), notify, 0);

        // Use the alarm's label or the default label as the ticker text and
        // main text of the notification.
        Task task = alarm.getTask();
        String label = "";
        if(task!=null&&task.getDes()!=null)
        {
            label = task.getDes();
        }
        Notification n = new Notification(R.drawable.stat_notify_alarm,
                label, alertTime);
        n.setLatestEventInfo(context, label,
                context.getString(R.string.alarm_notify_text),
                pendingNotify);
        n.flags |= Notification.FLAG_SHOW_LIGHTS
                | Notification.FLAG_ONGOING_EVENT;
        n.defaults |= Notification.DEFAULT_LIGHTS;

        // NEW: Embed the full-screen UI here. The notification manager will
        // take care of displaying it if it's OK to do so.
        Intent alarmAlert = new Intent(context, c);
        alarmAlert.putExtra(AlarmServiceCons.ALARM_INTENT_EXTRA, alarm.getId());
        alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        n.fullScreenIntent = PendingIntent.getActivity(context, alarm.getId().intValue(), alarmAlert, 0);

        // Send the notification using the alarm id to easily identify the
        // correct notification.
        NotificationManager nm = getNotificationManager(context);
        nm.notify(0, n);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void updateNotification(Context context, Alarm alarm, int timeout) {
        NotificationManager nm = getNotificationManager(context);

        // If the alarm is null, just cancel the notification.
        if (alarm == null) {
            if (true) {
                Log.v("wangxianming", "Cannot update notification for killer callback");
            }
            return;
        }

        // Launch SetAlarm when clicked.
        Intent viewAlarm = new Intent(context, QuickAddActivity.class);
        viewAlarm.putExtra(AlarmServiceCons.ALARM_ID, alarm.getId());
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, viewAlarm, 0);

        // Update the notification to indicate that the alert has been
        // silenced.
        Task task = alarm.getTask();
        String label = "";
        if(task!=null&&task.getDes()!=null)
        {
            label = task.getDes();
        }
        Notification n = new Notification(R.drawable.stat_notify_alarm,
                label, alarm.getAlarmTime());
        n.setLatestEventInfo(context, label,
                context.getString(R.string.alarm_alert_alert_silenced, timeout),
                intent);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        // We have to cancel the original notification since it is in the
        // ongoing section and we want the "killed" notification to be a plain
        // notification.
        nm.cancel(0);
        nm.notify(0, n);
    }
}
