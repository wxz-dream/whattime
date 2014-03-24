package com.whatime.module.addcolock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePickerDialog;
import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.alarm.AdvanceCons;
import com.whatime.controller.alarm.PlayDelayCons;
import com.whatime.controller.alarm.RepeatCons;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.service.AlarmService;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.ui.view.GVData;
import com.whatime.framework.ui.view.MyGridView;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;

public class QuickAddActivity extends Activity
{
    private EditText title;
    
    private NumberPicker hour;
    
    private NumberPicker min;
    
    private TextView calendarBt;
    
    private TextView timeBt;
    
    private MyOnclickListener listnter;
    
    private AlertDialog time_dialog;
    
    private MyGridView grid;
    
    final Calendar time = Calendar.getInstance(TimeZone.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_quick);
        time.set(Calendar.SECOND, 0);
        initUi();
        uptUi();
        
    }
    
    private void uptUi()
    {
        calendarBt.setText(new StringBuilder().append(time.get(Calendar.YEAR))
            .append("-")
            .append(time.get(Calendar.MONTH) + 1)
            .append("-")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.DAY_OF_MONTH)))
            .append(" (")
            .append(SysUtil.getCurrentDayOfWeek(QuickAddActivity.this, time.get(Calendar.DAY_OF_WEEK)))
            .append(")")
            .toString());
        timeBt.setText(new StringBuilder().append(time.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.MINUTE)))
            .toString());
    }
    
    private void initUi()
    {
        listnter = new MyOnclickListener();
        initTitle();
        initll();
        initBt();
        initGd();
    }
    
    private void initGd()
    {
        ArrayList<GVData> ls = new ArrayList<GVData>();
        ls.add(new GVData(0, getString(R.string.quick_alarm), R.drawable.quick_alarm));
        ls.add(new GVData(1, getString(R.string.other), R.drawable.other_alarm));
        ls.add(new GVData(2, getString(R.string.birthday), R.drawable.birthday_alarm));
        ls.add(new GVData(3, getString(R.string.get_up), R.drawable.getup_alarm));
        ls.add(new GVData(4, getString(R.string.sleep), R.drawable.sleep_alarm));
        ls.add(new GVData(5, getString(R.string.drink), R.drawable.drink_alarm));
        ls.add(new GVData(6, getString(R.string.someday), R.drawable.someday_alarm));
        ls.add(new GVData(7, getString(R.string.take_pills), R.drawable.take_pill_alarm));
        grid = (MyGridView)findViewById(R.id.alarm_type_gd);
        GdAdapter adapter = new GdAdapter(ls);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int i, long l)
            {
                startActivity(new Intent(QuickAddActivity.this, AlarmAddActivity_.class)
                .putExtra(AlarmService.ALARM_ID,-1).putExtra(AlarmService.ALARM_TYPE,i));
                QuickAddActivity.this.finish();
            }
        });
        
    }

    private void initll()
    {
        View time_view = LayoutInflater.from(this).inflate(R.layout.add_time_pup, null);
        hour = (NumberPicker)time_view.findViewById(R.id.hourPicker);
        hour.setMaxValue(23);
        hour.setMinValue(0);
        hour.setFocusable(true);
        hour.setFocusableInTouchMode(true);
        hour.setValue(time.get(Calendar.HOUR_OF_DAY));
        min = (NumberPicker)time_view.findViewById(R.id.minutePicker);
        min.setMaxValue(59);
        min.setMinValue(0);
        min.setFocusable(true);
        min.setFocusableInTouchMode(true);
        min.setValue(time.get(Calendar.MINUTE));
        time_dialog = new AlertDialog.Builder(this).setView(time_view).create();
        TextView t_bt = (TextView)time_view.findViewById(R.id.time_bt);
        t_bt.setOnClickListener(listnter);
    }
    
    private void initBt()
    {
        calendarBt = (TextView)findViewById(R.id.add_quick_calendar_bt);
        timeBt = (TextView)findViewById(R.id.add_quick_time_bt);
        calendarBt.setOnClickListener(listnter);
        timeBt.setOnClickListener(listnter);
        Button save_quick = (Button)findViewById(R.id.save_quick);
        save_quick.setOnClickListener(listnter);
        Button back = (Button)findViewById(R.id.login_reback_btn);
        back.setOnClickListener(listnter);
    }
    
    private void initTitle()
    {
        title = (EditText)findViewById(R.id.quick_title);
    }
    
    class MyOnclickListener implements OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            WindowManager windowManager = QuickAddActivity.this.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            switch (v.getId())
            {
                case R.id.login_reback_btn:
                    QuickAddActivity.this.finish();
                    break;
                case R.id.add_quick_calendar_bt:
                    new DatePickerDialog(QuickAddActivity.this, mDateSetListener, time.get(Calendar.YEAR),
                        time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.add_quick_time_bt:
                    time_dialog.show();
                    WindowManager.LayoutParams lp1 = time_dialog.getWindow().getAttributes();
                    lp1.width = (int)(display.getWidth()); //设置宽度
                    time_dialog.getWindow().setAttributes(lp1);
                    break;
                case R.id.time_bt:
                    time.set(Calendar.HOUR_OF_DAY, hour.getValue());
                    time.set(Calendar.MINUTE, min.getValue());
                    time_dialog.dismiss();
                    break;
                case R.id.save_quick:
                    long poor = time.getTimeInMillis() - System.currentTimeMillis();
                    if (Math.abs(poor) < 1000 || time.getTimeInMillis() < System.currentTimeMillis())
                    {
                        Toast toast = Toast.makeText(QuickAddActivity.this, "请选择正确的时间", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        return;
                    }
                    Alarm alarm = new Alarm();
                    String alarmUuid = UUID.randomUUID().toString();
                    alarm.setDel(false);
                    alarm.setUuid(alarmUuid);
                    alarm.setTitle("快速闹钟");
                    alarm.setAlarmTime(time.getTimeInMillis());
                    alarm.setOpen(true);
                    long alarmId = DBHelper.getInstance().addAlarm(alarm);
                    //
                    Task task = new Task();
                    String taskUuid = UUID.randomUUID().toString();
                    task.setUuid(taskUuid);
                    task.setDel(false);
                    task.setAlarmTime(time.getTimeInMillis());
                    task.setTitle(title.getText().toString());
                    task.setAdvanceOrder(AdvanceCons.ORDER_DEFAULT);
                    task.setRepeatType(RepeatCons.TYPE_DEFAULT);
                    task.setPlayType(PlayDelayCons.TYPE_DEFAULT);
                    task.setAlarmUuid(alarmUuid);
                    task.setOpen(true);
                    task.setAlarm(alarm);
                    task.setShake(true);
                    task.setMusic(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
                    long taskId = DBHelper.getInstance().addTask(task);
                    Task currentTask = DBHelper.getInstance().getNextTaskByAlarmId(alarmId);
                    alarm.setTask(currentTask);
                    alarm.setTaskUuid(currentTask.getUuid());
                    DBHelper.getInstance().uptAlarm(alarm);
                    AlarmController.addAlarm(QuickAddActivity.this, alarm.getId());
                    QuickAddActivity.this.finish();
                    break;
            }
            uptUi();
        }
        
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            time.set(Calendar.YEAR, year);
            time.set(Calendar.MONTH, monthOfYear);
            time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            uptUi();
        }
    };
    class GdAdapter extends BaseAdapter
    {
        private ArrayList<GVData> data;
        
        public GdAdapter(ArrayList<GVData> data)
        {
            this.data = data;
        }
        @Override
        public int getCount()
        {
            return data.size();
        }
        
        @Override
        public Object getItem(int arg0)
        {
            return data.get(arg0);
        }
        
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup arg2)
        {
            GVData gv = data.get(position);
            
            View view = LayoutInflater.from(QuickAddActivity.this).inflate(R.layout.type_gv_item, null);
            ImageView iv = (ImageView)view.findViewById(R.id.type_gv_item_iv);
            iv.setImageResource(gv.img);
            TextView name = (TextView)view.findViewById(R.id.type_gv_item_tv);
            name.setText(gv.title);
            return view;
        }
    }
}
