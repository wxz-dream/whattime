package com.whatime.module.addcolock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePickerDialog;
import net.simonvt.numberpicker.NumberPicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AdvanceCons;
import com.whatime.controller.cons.AlarmCons;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.cons.PlayDelayCons;
import com.whatime.controller.cons.RepeatCons;
import com.whatime.controller.cons.TaskCons;
import com.whatime.db.Alarm;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.framework.ui.view.SwitchButton;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.ui.widget.ArrayWheelAdapter;
import com.whatime.framework.ui.widget.NumericWheelAdapter;
import com.whatime.framework.ui.widget.OnWheelChangedListener;
import com.whatime.framework.ui.widget.WheelView;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.linkman.ContactListView;

@EActivity(R.layout.task_add)
public class TaskAddActivity extends Activity
{
    
    @ViewById
    EditText task_title;
    
    @ViewById
    EditText task_des;
    
    @ViewById
    TextView task_calendar_bt;
    
    @ViewById
    TextView task_time_bt;
    
    @ViewById
    TextView surpervise_label;
    
    @ViewById
    TextView repeat_label;
    
    @ViewById
    TextView advance_label;
    
    @ViewById
    TextView task_music;
    
    @ViewById
    EditText address;
    
    @ViewById
    EditText notice;
    
    @ViewById
    RelativeLayout surpervise;
    
    @ViewById
    RelativeLayout repeat;
    
    @ViewById
    RelativeLayout advance;
    
    /*@ViewById
    RelativeLayout play;*/
    
    @ViewById
    RelativeLayout music;
    
    @ViewById
    SwitchButton task_shake;
    
    @ViewById
    LinearLayout address_ll;
    
    @ViewById
    LinearLayout notice_ll;
    
    private AlertDialog repeat_dialog;//重复对话框
    
    private AlertDialog detail_dialog;//重复其他对话框
    
    private AlertDialog advance_dialog;//小睡延迟对话框
    
    private ListView repeat_lv;//重复列表
    
    private View repeatView;//重复View
    
    private View advanceView;//小睡延迟View
    
    private View detailView;//小睡其他View
    
    private WheelView types;
    
    private WheelView type_day;
    
    private ListView advance_lv;//重复列表
    
    private View pupview;//时间VIew
    
    private LinearLayout l_days;
    
    private LinearLayout l_times;
    
    private NumberPicker hour;
    
    private NumberPicker min;
    
    private AlertDialog time_dialog;
    
    private Alarm mAlarm;
    
    private Task mTask;
    
    private long task_id;
    
    private Context context;
    
    private AlarmController controller = new AlarmController();
    
    private Calendar time = Calendar.getInstance(TimeZone.getDefault());
    
    private ArrayList<String> repeatLabels = new ArrayList<String>();
    
    private ArrayList<String> advanceLabels = new ArrayList<String>();
    
    final static int MUSIC_REQUEST_CODE = 1;
    
    final static int CONTACT_REQUEST_CODE = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context = this;
        initData();
    }
    
    @AfterViews
    void initView()
    {
        initTask();
        initUi();
        uptUi();
    }
    
    private void initUi()
    {
        if (mTask.getTitle() != null)
        {
            task_title.setText(mTask.getTitle());
        }
        if (mTask.getDes() != null)
        {
            task_des.setText(mTask.getDes());
        }
        initTime();
        initwheel();
        initRepeat();
        initadvance();
        if (mTask.getShake() != null)
        {
            task_shake.setChecked(mTask.getShake());
        }
        task_des.setOnTouchListener(new EditOntouchListener());
        address.setOnTouchListener(new EditOntouchListener());
        notice.setOnTouchListener(new EditOntouchListener());
    }
    
    private void initwheel()
    {
        pupview = LayoutInflater.from(context).inflate(R.layout.pup_write_tab, null);
        l_days = (LinearLayout)pupview.findViewById(R.id.time_left);
        l_times = (LinearLayout)pupview.findViewById(R.id.time_right);
    }
    
    private void initRepeat()
    {
        repeatView = LayoutInflater.from(context).inflate(R.layout.pup_repeat_tab, null);
        repeat_dialog = new AlertDialog.Builder(context).setView(repeatView).create();
        repeat_lv = (ListView)repeatView.findViewById(R.id.repeat_lv);
        repeat_lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
            repeatLabels));
        repeat_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        repeat_lv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                mTask.setRepeatType(arg2);
                repeat_dialog.dismiss();
                if (arg2 == RepeatCons.TYPE_OTHER)
                {
                    detail_dialog.show();
                    l_days.setVisibility(View.GONE);
                    l_times.setVisibility(View.VISIBLE);
                    WindowManager windowManager = ((Activity)context).getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = detail_dialog.getWindow().getAttributes();
                    lp.width = (int)(display.getWidth()); //设置宽度
                    detail_dialog.getWindow().setAttributes(lp);
                }
                else
                {
                    uptUi();
                }
            }
        });
        detailView = LayoutInflater.from(context).inflate(R.layout.pup_repeat_detail_tab, null);
        detail_dialog = new AlertDialog.Builder(context).setView(detailView).create();
        types = (WheelView)detailView.findViewById(R.id.type);
        types.setAdapter(new ArrayWheelAdapter<String>(getResources().getStringArray(R.array.repeat_type)));
        types.setCyclic(true);
        types.addChangingListener(new OnWheelChangedListener()
        {
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                switch (newValue)
                {
                    case 0:
                        type_day = (WheelView)detailView.findViewById(R.id.day);
                        type_day.setAdapter(new NumericWheelAdapter(1, 10, "%02d"));
                        type_day.setCyclic(true);
                        type_day.setVisibility(View.VISIBLE);
                        type_day.setEnabled(true);
                        break;
                    case 1:
                        type_day = (WheelView)detailView.findViewById(R.id.day);
                        type_day.setAdapter(new NumericWheelAdapter(1, 24, "%02d"));
                        type_day.setCyclic(true);
                        type_day.setVisibility(View.VISIBLE);
                        type_day.setEnabled(true);
                        break;
                    case 2:
                        type_day = (WheelView)detailView.findViewById(R.id.day);
                        type_day.setAdapter(new NumericWheelAdapter(1, 52, "%02d"));
                        type_day.setCyclic(true);
                        type_day.setVisibility(View.VISIBLE);
                        type_day.setEnabled(true);
                        break;
                }
            }
        });
        type_day = (WheelView)detailView.findViewById(R.id.day);
        type_day.setAdapter(new NumericWheelAdapter(1, 10, "%02d"));
        type_day.setCyclic(true);
        type_day.setVisibility(View.VISIBLE);
        type_day.setEnabled(true);
        Button detail_commit = (Button)detailView.findViewById(R.id.alarm_save);
        detail_commit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                detail_dialog.dismiss();
                StringBuilder sb = new StringBuilder();
                sb.append(types.getCurrentItem()).append(",").append(type_day.getCurrentItem() + 1);
                mTask.setRepeatInfo(sb.toString());
                uptUi();
            }
        });
        Button detail_cancel = (Button)detailView.findViewById(R.id.alarm_cancel);
        detail_cancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                detail_dialog.dismiss();
            }
        });
    }
    
    private void initadvance()
    {
        advanceView = LayoutInflater.from(context).inflate(R.layout.pup_advance_tab, null);
        advance_dialog = new AlertDialog.Builder(context).setView(advanceView).create();
        advance_lv = (ListView)advanceView.findViewById(R.id.advance_lv);
        advance_lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
            advanceLabels));
        advance_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        advance_lv.setOnItemClickListener(new OnItemClickListener()
        {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                advance_dialog.dismiss();
                mTask.setAdvanceOrder(arg2);
                parseAdvanceData();
                uptUi();
            }
        });
    }
    private void parseAdvanceData()
    {
        mTask.setSetTime(time.getTimeInMillis());
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(time.getTimeInMillis());
        switch (mTask.getAdvanceOrder())
        {
            case AdvanceCons.ORDER_TEN_MINUTE:
                c.add(Calendar.MINUTE, -10);
                break;
            case AdvanceCons.ORDER_THIRTY_MINUTE:
                c.add(Calendar.MINUTE, -30);
                break;
            case AdvanceCons.ORDER_ONE_HOUR:
                c.add(Calendar.HOUR_OF_DAY, -1);
                break;
            case AdvanceCons.ORDER_TOW_HOUR:
                c.add(Calendar.HOUR_OF_DAY, -2);
                break;
            case AdvanceCons.ORDER_ONE_DAY:
                c.add(Calendar.DATE, -1);
                break;
            case AdvanceCons.ORDER_TWO_DAY:
                c.add(Calendar.DATE, -2);
                break;
            case AdvanceCons.ORDER_SEVEN_DAY:
                c.add(Calendar.DATE, -7);
                break;
        
        }
        mTask.setAlarmTime(c.getTimeInMillis());
    }
    private void initTime()
    {
        View time_view = LayoutInflater.from(context).inflate(R.layout.add_time_pup, null);
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
        time_dialog = new AlertDialog.Builder(context).setView(time_view).create();
        TextView t_bt = (TextView)time_view.findViewById(R.id.time_bt);
        t_bt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                time.set(Calendar.HOUR_OF_DAY, hour.getValue());
                time.set(Calendar.MINUTE, min.getValue());
                mTask.setAlarmTime(time.getTimeInMillis());
                time_dialog.dismiss();
                uptUi();
            }
        });
    }
    
    private void initTask()
    {
        mAlarm = controller.getAlarmById(getIntent().getLongExtra(AlarmServiceCons.ALARM_ID, -1));
        task_id = getIntent().getLongExtra(TaskCons.TASK_ID, -1);
        if (task_id == -1)
        {
            mTask = new Task();
            mTask.setUuid(UUID.randomUUID().toString());
            mTask.setAlarmUuid(mAlarm.getUuid());
            mTask.setAlarm(mAlarm);
            mTask.setRepeatType(RepeatCons.TYPE_DEFAULT);
            mTask.setAdvanceOrder(AdvanceCons.ORDER_DEFAULT);
            mTask.setPlayType(PlayDelayCons.TYPE_DEFAULT);
            time = Calendar.getInstance(TimeZone.getDefault());
            time.set(Calendar.SECOND, 0);
            mTask.setAlarmTime(time.getTimeInMillis());
            mTask.setOpen(true);
            mTask.setDel(false);
            mTask.setShake(true);
            mTask.setMusic(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
        }
        else
        {
            mTask = DBHelper.getInstance().getTaskById(task_id);
            if(mTask.getAdvanceOrder()!=AdvanceCons.ORDER_DEFAULT||mTask.getSetTime()!=null)
            {
                time.setTimeInMillis(mTask.getSetTime());
            }
            else
            {
                time.setTimeInMillis(mTask.getAlarmTime());
            }
        }
    }
    
    @UiThread
    void uptUi()
    {
        task_calendar_bt.setText(new StringBuilder().append(time.get(Calendar.YEAR))
            .append("-")
            .append(time.get(Calendar.MONTH) + 1)
            .append("-")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.DAY_OF_MONTH)))
            .append(" (")
            .append(SysUtil.getCurrentDayOfWeek(context, time.get(Calendar.DAY_OF_WEEK)))
            .append(")")
            .toString());
        task_time_bt.setText(new StringBuilder().append(time.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.MINUTE)))
            .toString());
        repeat_label.setText(repeatLabels.get(mTask.getRepeatType()));
        advance_label.setText(advanceLabels.get(mTask.getAdvanceOrder()));
        if (mTask.getMusic() != null)
        {
            task_music.setText(RingtoneManager.getRingtone(context, Uri.parse(mTask.getMusic())).getTitle(context));
        }
        show();
        
    }
    
    private void show()
    {
        if (mAlarm.getShare() != null && mAlarm.getShare().length() > 0)
        {
            address_ll.setVisibility(View.VISIBLE);
            notice_ll.setVisibility(View.VISIBLE);
        }
        else
        {
            address_ll.setVisibility(View.GONE);
            notice_ll.setVisibility(View.GONE);
        }
        if (mAlarm.getType() == AlarmCons.TYPE_GETUP)
        {
            surpervise.setVisibility(View.VISIBLE);
        }
        else
        {
            surpervise.setVisibility(View.GONE);
        }
        if (mAlarm.getType() == AlarmCons.TYPE_SLEEP)
        {
            //play.setVisibility(View.VISIBLE);
            advance.setVisibility(View.GONE);
        }
        else
        {
            //play.setVisibility(View.GONE);
            advance.setVisibility(View.VISIBLE);
        }
    }
    
    @Click
    void task_calendar_bt()
    {
        new DatePickerDialog(context, mDateSetListener, time.get(Calendar.YEAR), time.get(Calendar.MONTH),
            time.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    @Click
    void surpervise()
    {
        //页面传值并获取回传值
        Intent intent = new Intent();
        intent.setClass(context, ContactListView.class);
        Bundle bundle = new Bundle();
        String wNumberStr = mTask.getSurpervise();
        
        if (wNumberStr == null || wNumberStr.equals(""))
        {
            wNumberStr = "";
        }
        bundle.putString("wNumberStr", wNumberStr);
        intent.putExtras(bundle);
        startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }
    
    @OnActivityResult(CONTACT_REQUEST_CODE)
    void activityToResultSurpervise(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            String numberStr = null;
            Bundle bundle = data.getExtras();
            if (bundle != null)
            {
                numberStr = bundle.getString("numberStr");
            }
            
            mTask.setSurpervise(numberStr);
            surpervise_label.setText(numberStr);
        }
    }
    
    @OnActivityResult(MUSIC_REQUEST_CODE)
    void activityToResultMusic(int resultCode, Intent data)
    {
        try
        {
            Uri myUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            final Ringtone r = RingtoneManager.getRingtone(this, myUri);
            if (r != null)
            {
                mTask.setMusic(myUri.toString());
                uptUi();
            }
        }
        catch (Exception e)
        {
        }
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            time.set(Calendar.YEAR, year);
            time.set(Calendar.MONTH, monthOfYear);
            time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(mTask.getAdvanceOrder()!=AdvanceCons.ORDER_DEFAULT||mTask.getSetTime()!=null)
            {
                mTask.setSetTime(time.getTimeInMillis());
            }
            else
            {
                mTask.setAlarmTime(time.getTimeInMillis());
            }
            
            uptUi();
        }
    };
    
    @Click
    void task_time_bt()
    {
        WindowManager windowManager = TaskAddActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        time_dialog.show();
        WindowManager.LayoutParams lp1 = time_dialog.getWindow().getAttributes();
        lp1.width = (int)(display.getWidth()); //设置宽度
        time_dialog.getWindow().setAttributes(lp1);
    }
    
    @Click
    void login_reback_btn()
    {
        onBackPressed();
    }
    
    @Click
    void repeat()
    {
        repeat_lv.setItemChecked(mTask.getRepeatType(), true);
        
        if (mTask.getRepeatInfo() != null && mTask.getRepeatInfo().length() > 0)
        {
            String[] strs = mTask.getRepeatInfo().split(",");
            types.setCurrentItem(Integer.valueOf(strs[0]));
            type_day.setCurrentItem(Integer.valueOf(strs[1]) - 1);
        }
        repeat_dialog.show();
        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = repeat_dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        repeat_dialog.getWindow().setAttributes(lp);
    }
    
    @Click
    void advance()
    {
        advance_lv.setItemChecked(mTask.getAdvanceOrder(), true);
        advance_dialog.show();
        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = advance_dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        advance_dialog.getWindow().setAttributes(lp);
    }
    
    @Click
    void music()
    {
        Intent intent = new Intent();
        intent.setAction(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        
        Uri myUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (myUri != null)
        {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, myUri);
        }
        startActivityForResult(intent, MUSIC_REQUEST_CODE);
    }
    
    private void toastTime()
    {
        Toast toast = Toast.makeText(context, "请选择正确的时间", Toast.LENGTH_SHORT);
        ToastMaster.setToast(toast);
        toast.show();
    }
    
    @Click
    void add_save()
    {
        if(mTask.getAdvanceOrder()!=AdvanceCons.ORDER_DEFAULT)
        {
            parseAdvanceData();
        }
        long setTime = time.getTimeInMillis();
        if(mTask.getAdvanceOrder()!=AdvanceCons.ORDER_DEFAULT||mTask.getSetTime()!=null)
        {
            setTime = mTask.getAlarmTime();
        }
        long poor = setTime - System.currentTimeMillis();
        if (Math.abs(poor) < 1000 || setTime < System.currentTimeMillis())
        {
            toastTime();
            return;
        }
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(setTime);
        switch (mTask.getRepeatType())
        {
            case RepeatCons.TYPE_M_F:
                switch (c.get(Calendar.DAY_OF_WEEK))
                {
                    case Calendar.SATURDAY:
                    case Calendar.SUNDAY:
                        toastTime();
                        return;
                }
                
                break;
            case RepeatCons.TYPE_S_S:
                
                switch (c.get(Calendar.DAY_OF_WEEK))
                {
                    case Calendar.MONDAY:
                    case Calendar.TUESDAY:
                    case Calendar.WEDNESDAY:
                    case Calendar.THURSDAY:
                    case Calendar.FRIDAY:
                        toastTime();
                        return;
                }
                break;
        }
        mTask.setTitle(task_title.getText().toString());
        mTask.setDes(task_des.getText().toString());
        mTask.setShake(task_shake.isChecked());
        mTask.setAddress(address.getText().toString());
        mTask.setNotice(notice.getText().toString());
        mTask.setAlarmUuid(mAlarm.getUuid());
        mTask.setAlarm(mAlarm);
        if (task_id == -1)
        {
            DBHelper.getInstance().addTask(mTask);
        }
        else
        {
            DBHelper.getInstance().uptTask(mTask);
        }
        this.finish();
    }
    
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(context).setTitle("退出编辑")
            .setMessage("活动未保存，确认要退出编辑状态？")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface d, int w)
                {
                    TaskAddActivity.super.onBackPressed();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }
    
    private void initData()
    {
        repeatLabels.add(getString(R.string.repeat_default));
        repeatLabels.add(getString(R.string.repeat_work));
        repeatLabels.add(getString(R.string.repeat_m_f));
        repeatLabels.add(getString(R.string.repeat_s_s));
        repeatLabels.add(getString(R.string.repeat_everyyear));
        repeatLabels.add(getString(R.string.repeat_everymonth));
        repeatLabels.add(getString(R.string.repeat_everyweekday));
        repeatLabels.add(getString(R.string.repeat_everyday));
        repeatLabels.add(getString(R.string.other));
        advanceLabels.add("不提前");
        advanceLabels.add("10分钟");
        advanceLabels.add("30分钟");
        advanceLabels.add("1小时");
        advanceLabels.add("2小时");
        advanceLabels.add("1天");
        advanceLabels.add("2天");
        advanceLabels.add("7天");
        
    }
    
    class EditOntouchListener implements View.OnTouchListener
    {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
        
    }
}
