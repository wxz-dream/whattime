package com.whatime.module.addcolock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.controller.cons.AdvanceCons;
import com.whatime.controller.cons.AlarmCons;
import com.whatime.controller.cons.AlarmServiceCons;
import com.whatime.controller.cons.PlayDelayCons;
import com.whatime.controller.cons.RepeatCons;
import com.whatime.controller.cons.TaskCons;
import com.whatime.controller.service.AlarmUtil;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.db.Task;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.ui.view.MyListView;
import com.whatime.framework.ui.view.SwitchButton;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.framework.util.SysUtil;
import com.whatime.module.linkman.ContactListView;
import com.whatime.module.thirdLogin.AuthActivity;

@EActivity(R.layout.alarm_add)
public class AlarmAddActivity extends Activity
{
    @ViewById
    EditText alarm_title;
    
    @ViewById
    EditText alarm_des;
    
    @ViewById
    EditText maxJoinNum_label;
    
    @ViewById
    LinearLayout alarm_des_ll;
    
    @ViewById
    RelativeLayout alarm_state;
    
    @ViewById
    RelativeLayout alarm_cate;
    
    @ViewById
    RelativeLayout alarm_linkman;
    
    @ViewById
    LinearLayout maxJoinNum_ll;
    
    @ViewById
    LinearLayout endtime_ll;
    
    @ViewById
    LinearLayout isend_ll;
    
    @ViewById
    RelativeLayout alarm_scope;
    
    @ViewById
    TextView alarm_share_label;
    
    @ViewById
    TextView alarm_linkman_label;
    
    @ViewById
    TextView endtime_calendar_bt;
    
    @ViewById
    TextView endtime_time_bt;
    
    @ViewById
    TextView alarm_scope_label;
    
    @ViewById
    TextView alarm_cate_label;
    
    @ViewById
    MyListView add_common_lv;
    
    @ViewById
    SwitchButton isend_bt;
    
    private Spinner province_spinner;
    
    private Spinner city_spinner;
    
    private Spinner parent_spinner;
    
    private Spinner child_spinner;
    
    private CheckBox no_scope;
    
    private CheckBox pro;
    
    private CheckBox ct;
    
    private MyAdapter adapter;
    
    private Context context;
    
    private long alarm_id;
    
    private int alarm_type;
    
    private Alarm mAlarm;
    
    private NumberPicker hour;
    
    private NumberPicker min;
    
    private AlertDialog time_dialog;
    
    private Calendar time = Calendar.getInstance(TimeZone.getDefault());
    
    private boolean isNew;
    
    private AlarmController controller = new AlarmController();
    
    private HashMap<String, String> pros;
    
    private List<String> provinces;
    
    private HashMap<String, String> cts;
    
    private List<String> citys;
    
    private List<Category> parentCates;
    
    private List<String> parentCatesNames;
    
    private List<Category> childCates;
    
    private List<String> childCatesNames;
    
    private ArrayList<String> repeatLabels = new ArrayList<String>();
    
    final String[] langs = {"朋友圈", "网络", "新浪微博", "QQ空间", "人人网", "腾讯微博"};
    
    final boolean[] selected = new boolean[] {false, false, false, false, false, false};// 一个存放Boolean值的数组
    
    final static int CONTACT_REQUEST_CODE = 2;
    
    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x001:
                    int state = msg.getData().getInt(ResponseCons.STATE);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        Toast toast = Toast.makeText(AlarmAddActivity.this, "同步成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(AlarmAddActivity.this, "同步失败", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context = this;
        ShareSDK.initSDK(this);
        initData();
    }
    
    @Override
    protected void onDestroy()
    {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }
    
    @AfterViews
    void initView()
    {
        initAlarm();
        initUi();
        uptUi();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mAlarm.resetTasks();
        Collections.sort(mAlarm.getTasks(), new Comparator<Task>()
        {
            @Override
            public int compare(Task arg0, Task arg1)
            {
                if (arg0.getAlarmTime() >= arg1.getAlarmTime())
                {
                    return 1;
                }
                return -1;
            }
        });
        adapter.notifyDataSetChanged();
    }
    
    private void initUi()
    {
        initTime();
        alarm_des.setOnTouchListener(new EditOntouchListener());
        if (mAlarm.getTitle() != null)
        {
            alarm_title.setText(mAlarm.getTitle());
        }
        if (mAlarm.getDes() != null)
        {
            alarm_des.setText(mAlarm.getDes());
        }
        if (mAlarm.getMaxJoinNum() != null)
        {
            maxJoinNum_label.setText(mAlarm.getMaxJoinNum().toString());
        }
        if (mAlarm.getEndJoin() != null)
        {
            isend_bt.setChecked(mAlarm.getEndJoin());
        }
        
        adapter = new MyAdapter();
        add_common_lv.setAdapter(adapter);
        add_common_lv.setOnItemClickListener(new OnItemClickListener()
        {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                startActivity(new Intent(AlarmAddActivity.this, TaskAddActivity_.class).putExtra(AlarmServiceCons.ALARM_ID,
                    mAlarm.getId())
                    .putExtra(TaskCons.TASK_ID, mAlarm.getTasks().get(arg2).getId()));
            }
            
        });
        add_common_lv.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3)
            {
                new AlertDialog.Builder(context).setTitle("删除子活动")
                    .setMessage("确认删除此子活动？")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface d, int w)
                        {
                            DBHelper.getInstance().deleteTask(mAlarm.getTasks().get(arg2).getId());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
                return true;
            }
            
        });
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
                mAlarm.setEndTime(time.getTimeInMillis());
                time_dialog.dismiss();
                uptUi();
            }
        });
    }
    
    @UiThread
    void uptUi()
    {
        endtime_calendar_bt.setText(new StringBuilder().append(time.get(Calendar.YEAR))
            .append("-")
            .append(time.get(Calendar.MONTH) + 1)
            .append("-")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.DAY_OF_MONTH)))
            .append(" (")
            .append(SysUtil.getCurrentDayOfWeek(context, time.get(Calendar.DAY_OF_WEEK)))
            .append(")")
            .toString());
        endtime_time_bt.setText(new StringBuilder().append(time.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(SysUtil.doubleDataFormat(time.get(Calendar.MINUTE)))
            .toString());
        if (mAlarm.getScope() == null)
        {
            alarm_scope_label.setText("不限");
        }
        else
        {
            alarm_scope_label.setText(mAlarm.getScope());
        }
        if(mAlarm.getCategory()==null)
        {
            alarm_cate_label.setText("未选择");
        }
        else
        {
            alarm_cate_label.setText(mAlarm.getCategory().getName());
        }
        if (mAlarm.getShare() != null && mAlarm.getShare().length() > 0)
        {
            alarm_des_ll.setVisibility(View.VISIBLE);
            alarm_state.setVisibility(View.VISIBLE);
            alarm_cate.setVisibility(View.VISIBLE);
            alarm_linkman.setVisibility(View.VISIBLE);
            maxJoinNum_ll.setVisibility(View.VISIBLE);
            endtime_ll.setVisibility(View.VISIBLE);
            isend_ll.setVisibility(View.VISIBLE);
            alarm_scope.setVisibility(View.VISIBLE);
            
            String share = mAlarm.getShare();
            String[] s = share.split(",");
            StringBuilder sb = new StringBuilder();
            for (String str : s)
            {
                selected[Integer.valueOf(str)] = true;
                sb.append(langs[Integer.valueOf(str)]).append(",");
            }
            String shareLabel = sb.toString();
            if (shareLabel.endsWith(","))
            {
                shareLabel = shareLabel.substring(0, shareLabel.length() - 1);
            }
            alarm_share_label.setText(shareLabel);
        }
        else
        {
            alarm_des_ll.setVisibility(View.GONE);
            alarm_state.setVisibility(View.GONE);
            alarm_cate.setVisibility(View.GONE);
            alarm_linkman.setVisibility(View.GONE);
            maxJoinNum_ll.setVisibility(View.GONE);
            endtime_ll.setVisibility(View.GONE);
            isend_ll.setVisibility(View.GONE);
            alarm_scope.setVisibility(View.GONE);
            
            alarm_share_label.setText("不分享");
        }
    }
    
    private void initAlarm()
    {
        alarm_id = getIntent().getLongExtra(AlarmServiceCons.ALARM_ID, -1);
        alarm_type = getIntent().getIntExtra(AlarmServiceCons.ALARM_TYPE, 0);
        if (alarm_id == -1)
        {
            isNew = true;
            time.set(Calendar.DAY_OF_YEAR, time.get(Calendar.DAY_OF_YEAR) + 7);
            time.set(Calendar.SECOND, 0);
            mAlarm = new Alarm();
            mAlarm.setUuid(UUID.randomUUID().toString());
            mAlarm.setDel(false);
            mAlarm.setOpen(true);
            mAlarm.setEndJoin(false);
            mAlarm.setFroms(AlarmCons.FROMS_ANDROID);
            mAlarm.setType(alarm_type);
            mAlarm.setEndTime(time.getTimeInMillis());
            mAlarm.setJoinNum(1l);
            User user = MyApp.getInstance().getUser();
            if (user != null)
            {
                mAlarm.setUserUuid(user.getUuid());
            }
            DBHelper.getInstance().addAlarm(mAlarm);
            initTypeData();
        }
        else
        {
            mAlarm = controller.getAlarmById(alarm_id);
            if (mAlarm.getEndTime() != null)
            {
                time.setTimeInMillis(mAlarm.getEndTime());
            }
            
        }
    }
    
    private Task getDrinkTask()
    {
        Task t1 = new Task();
        t1.setUuid(UUID.randomUUID().toString());
        t1.setAlarmUuid(mAlarm.getUuid());
        t1.setAlarm(mAlarm);
        t1.setRepeatType(RepeatCons.TYPE_DAY);
        t1.setAdvanceOrder(AdvanceCons.ORDER_DEFAULT);
        t1.setPlayType(PlayDelayCons.TYPE_DEFAULT);
        t1.setOpen(true);
        t1.setDel(false);
        t1.setShake(true);
        t1.setMusic(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
        return t1;
    }
    
    private void initTypeData()
    {
        
        switch (alarm_type)
        {
            case AlarmCons.TYPE_DRINK:
                //9:30
                Task t1 = getDrinkTask();
                t1.setTitle("喝牛奶");
                t1.setDes("早餐后喝杯奶，不但能补充蛋白质，还能构建强壮的骨骼。");
                t1.setAlarmTime(AlarmUtil.getDrinkTime(9, 30));
                DBHelper.getInstance().addTask(t1);
                //11:00
                Task t2 = getDrinkTask();
                t2.setTitle("喝水");
                t2.setDes("休息一会，补充下水分吧。");
                t2.setAlarmTime(AlarmUtil.getDrinkTime(11, 0));
                DBHelper.getInstance().addTask(t2);
                //12:00
                Task t3 = getDrinkTask();
                t3.setTitle("饭后喝水");
                t3.setDes("如果无糖苏打水是你午餐的配搭饮料，那最好把它换成纯水。常喝无糖苏打水会增加患心脏疾病（包括中风）的几率。想要增加一些风味？可以往水里加几粒葡萄和薄荷，或者新鲜的姜和桃子。");
                t3.setAlarmTime(AlarmUtil.getDrinkTime(12, 0));
                DBHelper.getInstance().addTask(t3);
                //13:00
                Task t4 = getDrinkTask();
                t4.setTitle("小憩后喝水");
                t4.setDes("中午小憩后，最好补充下水分。");
                t4.setAlarmTime(AlarmUtil.getDrinkTime(13, 0));
                DBHelper.getInstance().addTask(t4);
                //15:00
                Task t5 = getDrinkTask();
                t5.setTitle("喝水");
                t5.setDes("想要撞墙，或感到困意绵绵？你的身体因为缺水而有些疲劳。当你缺水的时候，你会感到疲惫、犯困，在你喝咖啡之前，最好先到饮水机前接一大杯水喝——它会让你重新焕发活力。");
                t5.setAlarmTime(AlarmUtil.getDrinkTime(15, 0));
                DBHelper.getInstance().addTask(t5);
                //17:00
                Task t6 = getDrinkTask();
                t6.setTitle("肌肉补水");
                t6.setDes("想要完成傍晚的健身课程，发现有些吃力，很有可能是没有喝够水。健身后的补水更加重要。在你开始健身前，建议你先称一下体重，健身完后再称一次。如果健身后每少半磅体重，喝两杯水，这才能弥补你因为流汗而丢失的体液。");
                t6.setAlarmTime(AlarmUtil.getDrinkTime(17, 0));
                DBHelper.getInstance().addTask(t6);
                //19:00
                Task t7 = getDrinkTask();
                t7.setTitle("欢乐补水");
                t7.setDes("如果下班后喝上两杯酒是你放慢节奏享受生活的方式，那就多往杯子里加些冰块，身体为了排出酒精中的有害物质，会增加水分的流失。多补充水分能防止你第二天早上起来头疼。");
                t7.setAlarmTime(AlarmUtil.getDrinkTime(19, 0));
                DBHelper.getInstance().addTask(t7);
                break;
            case AlarmCons.TYPE_TAKE_PILLS:
                //9:30
                Task t8 = getDrinkTask();
                t8.setTitle("早晨服药");
                t8.setDes("谨遵医嘱，按时服药。");
                t8.setAlarmTime(AlarmUtil.getDrinkTime(9, 30));
                DBHelper.getInstance().addTask(t8);
                //12:30
                Task t9 = getDrinkTask();
                t9.setTitle("中午服药");
                t9.setDes("谨遵医嘱，按时服药。");
                t9.setAlarmTime(AlarmUtil.getDrinkTime(12, 30));
                DBHelper.getInstance().addTask(t9);
                //18:30
                Task t11 = getDrinkTask();
                t11.setTitle("傍晚服药");
                t11.setDes("谨遵医嘱，按时服药。");
                t11.setAlarmTime(AlarmUtil.getDrinkTime(18, 30));
                DBHelper.getInstance().addTask(t11);
            default:
                break;
        }
        
    }
    
    @Click
    void add_common_add()
    {
        startActivity(new Intent(AlarmAddActivity.this, TaskAddActivity_.class).putExtra(AlarmServiceCons.ALARM_ID,
            mAlarm.getId()).putExtra(TaskCons.TASK_ID, -1));
    }
    
    @Click
    void add_save()
    {
        if (mAlarm.getTasks().size() == 0)
        {
            Toast toast = Toast.makeText(context, "至少含有一项子活动", Toast.LENGTH_SHORT);
            ToastMaster.setToast(toast);
            toast.show();
        }
        else
        {
            mAlarm.setTitle(alarm_title.getText().toString());
            mAlarm.setDes(alarm_des.getText().toString());
            if (maxJoinNum_label.getText().toString().equals(""))
            {
                long num = 10000;
                mAlarm.setMaxJoinNum(num);
            }
            else
            {
                mAlarm.setMaxJoinNum(Long.parseLong(maxJoinNum_label.getText().toString()));
            }
            
            mAlarm.setEndJoin(isend_bt.isChecked());
            Task currentTask = DBHelper.getInstance().getNextTaskByAlarmId(mAlarm.getId());
            if (currentTask != null)
            {
                mAlarm.setTask(currentTask);
                mAlarm.setTaskUuid(currentTask.getUuid());
                mAlarm.setAlarmTime(currentTask.getAlarmTime());
                DBHelper.getInstance().uptAlarm(mAlarm);
                controller.setNextAlert();
                if (alarm_id == -1)
                {
                    controller.addAlarm(mAlarm, myHandler);
                }
                else
                {
                    controller.uptAlarm(mAlarm, myHandler);
                }
            }
            this.finish();
        }
    }
    
    @Click
    void alarm_linkman()
    {
        //页面传值并获取回传值
        Intent intent = new Intent();
        intent.setClass(context, ContactListView.class);
        Bundle bundle = new Bundle();
        String wNumberStr = mAlarm.getLinkman();
        
        if (wNumberStr == null || wNumberStr.equals(""))
        {
            wNumberStr = "";
        }
        bundle.putString("wNumberStr", wNumberStr);
        intent.putExtras(bundle);
        startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }
    
    @Click
    void endtime_calendar_bt()
    {
        new DatePickerDialog(context, mDateSetListener, time.get(Calendar.YEAR), time.get(Calendar.MONTH),
            time.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            time.set(Calendar.YEAR, year);
            time.set(Calendar.MONTH, monthOfYear);
            time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mAlarm.setEndTime(time.getTimeInMillis());
            uptUi();
        }
    };
    
    @Click
    void endtime_time_bt()
    {
        WindowManager windowManager = AlarmAddActivity.this.getWindowManager();
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
    void alarm_scope()
    {
        // 取得city_layout.xml中的视图
        final View view = LayoutInflater.from(this).inflate(R.layout.alarm_scope, null);
        
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
        no_scope = (CheckBox)view.findViewById(R.id.no_scope);
        pro = (CheckBox)view.findViewById(R.id.pro);
        ct = (CheckBox)view.findViewById(R.id.ct);
        
        no_scope.setOnCheckedChangeListener(new MyOncheckListener());
        pro.setOnCheckedChangeListener(new MyOncheckListener());
        ct.setOnCheckedChangeListener(new MyOncheckListener());
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
                ArrayAdapter adapter1 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, citys);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                city_spinner.setAdapter(adapter1);
                
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
                String scope = "";
                if (no_scope.isChecked())
                {
                    scope = null;
                }
                if (pro.isChecked())
                {
                    scope = provinces.get(province_spinner.getSelectedItemPosition());
                }
                if (ct.isChecked())
                {
                    scope =
                        provinces.get(province_spinner.getSelectedItemPosition()) + "|"
                            + citys.get(city_spinner.getSelectedItemPosition());
                }
                mAlarm.setScope(scope);
                uptUi();
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
        String scope = mAlarm.getScope();
        if (scope == null)
        {
            no_scope.setChecked(true);
        }
        else if (!scope.contains("|"))
        {
            pro.setChecked(true);
            province_spinner.setSelection(provinces.indexOf(scope));
        }
        else
        {
            pro.setChecked(true);
            province_spinner.setSelection(provinces.indexOf(scope.split("\\|")[0]));
            ct.setChecked(true);
        }
        
        dialog.show();
    }
    
    @Click
    void alarm_cate()
    {
        final View view = LayoutInflater.from(this).inflate(R.layout.alarm_cate, null);
        parent_spinner = (Spinner)view.findViewById(R.id.parent_cate_spinner);
        child_spinner = (Spinner)view.findViewById(R.id.child_cate_spinner);
        parentCates = DBHelper.getInstance().getcateByParentId(0);
        parentCatesNames = new ArrayList<String>();
        for (Category cate : parentCates)
        {
            parentCatesNames.add(cate.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, parentCatesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parent_spinner.setAdapter(adapter);
        parent_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterview, View view, int i, long l)
            {
                childCates = DBHelper.getInstance().getcateByParentId(i+1);
                childCatesNames = new ArrayList<String>();
                for (Category cate : childCates)
                {
                    childCatesNames.add(cate.getName());
                }
                ArrayAdapter adapter1 =
                    new ArrayAdapter(context, android.R.layout.simple_spinner_item, childCatesNames);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                child_spinner.setAdapter(adapter1);
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterview)
            {
            }
        });
        // 选择分类对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("请选择所属分类");
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mAlarm.setCategory(childCates.get(child_spinner.getSelectedItemPosition()));
                uptUi();
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
    }
    
    @Override
    public void onBackPressed()
    {
        
        if (isNew)
        {
            new AlertDialog.Builder(context).setTitle("退出编辑")
                .setMessage("活动未保存，确认要退出编辑状态？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface d, int w)
                    {
                        
                        DBHelper.getInstance().deleteTaskByAlarmId(mAlarm.getId());
                        DBHelper.getInstance().deleteAlarmList(mAlarm.getId());
                        AlarmAddActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        }
        else
        {
            long taskUptTime = DBHelper.getInstance().getTaskMaxUptTime();
            if (0 == mAlarm.getUptTime() || taskUptTime > mAlarm.getUptTime())
            {
                add_save();
            }
            AlarmAddActivity.super.onBackPressed();
        }
        
    }
    
    @Click
    void alarm_share()
    {
        new AlertDialog.Builder(context).setTitle("分享到圈子")
        // 标题
            .setMultiChoiceItems(langs, selected, new DialogInterface.OnMultiChoiceClickListener()
            {// 设置多选条目
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        if (isChecked)
                        {
                            List<Integer> ls = getShareMap();
                            switch (which)
                            {
                                case AlarmCons.SHARE_SINA:
                                    if (!ls.contains(AlarmCons.SHARE_SINA))
                                    {
                                        startActivity(new Intent(context, AuthActivity.class));
                                    }
                                    break;
                                case AlarmCons.SHARE_QQ:
                                    if (!ls.contains(AlarmCons.SHARE_QQ))
                                    {
                                        startActivity(new Intent(context, AuthActivity.class));
                                    }
                                    break;
                                case AlarmCons.SHARE_RENREN:
                                    if (!ls.contains(AlarmCons.SHARE_RENREN))
                                    {
                                        startActivity(new Intent(context, AuthActivity.class));
                                    }
                                    break;
                                case AlarmCons.SHARE_TECENT:
                                    if (!ls.contains(AlarmCons.SHARE_TECENT))
                                    {
                                        startActivity(new Intent(context, AuthActivity.class));
                                    }
                                    break;
                            
                            }
                        }
                        selected[which] = isChecked;
                        
                    }
                })
            .setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    List<Integer> retLs = getShareMap();
                    for (int i = 2; i <= 5; i++)
                    {
                        if (retLs.contains(i))
                        {
                            selected[i] = true;
                        }
                        else
                        {
                            selected[i] = false;
                        }
                        
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    StringBuilder show = new StringBuilder();
                    for (int i = 0; i < selected.length; i++)
                    {
                        if (selected[i])
                        {
                            sb.append(i).append(",");
                            show.append(langs[i]).append(",");
                        }
                    }
                    String shareLabel = sb.toString();
                    String showLabel = show.toString();
                    if (shareLabel.endsWith(","))
                    {
                        shareLabel = shareLabel.substring(0, shareLabel.length() - 1);
                    }
                    if (showLabel.endsWith(","))
                    {
                        showLabel = showLabel.substring(0, showLabel.length() - 1);
                    }
                    if (showLabel.length() == 0)
                    {
                        alarm_share_label.setText("不分享");
                    }
                    else
                    {
                        alarm_share_label.setText(showLabel);
                    }
                    mAlarm.setShare(shareLabel);
                    uptUi();
                    ShareSDK.stopSDK(context);
                }
            })
            .show();
    }
    
    private List<Integer> getShareMap()
    {
        ShareSDK.initSDK(this);
        List<Integer> shareLs = new ArrayList<Integer>();
        Platform[] weibos = ShareSDK.getPlatformList(context);
        for (Platform wb : weibos)
        {
            if (wb.isValid())
            {
                String name = wb.getName();
                if (SinaWeibo.NAME.equals(name))
                {
                    shareLs.add(2);
                }
                if (QZone.NAME.equals(name))
                {
                    shareLs.add(3);
                }
                if (Renren.NAME.equals(name))
                {
                    shareLs.add(4);
                }
                if (TencentWeibo.NAME.equals(name))
                {
                    shareLs.add(5);
                }
            }
        }
        return shareLs;
    }
    
    @OnActivityResult(CONTACT_REQUEST_CODE)
    void activityToResultOne(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            String numberStr = null;
            Bundle bundle = data.getExtras();
            if (bundle != null)
            {
                numberStr = bundle.getString("numberStr");
            }
            
            mAlarm.setLinkman(numberStr);
            alarm_linkman_label.setText(numberStr);
        }
    }
    
    class MyAdapter extends BaseAdapter
    {
        
        @Override
        public int getCount()
        {
            return mAlarm.getTasks().size();
        }
        
        @Override
        public Object getItem(int arg0)
        {
            return mAlarm.getTasks().get(arg0);
        }
        
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }
        
        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2)
        {
            Task task = mAlarm.getTasks().get(arg0);
            View v = LayoutInflater.from(context).inflate(R.layout.add_common_item, null);
            TextView title = (TextView)v.findViewById(R.id.item_title);
            if (task.getTitle() != null)
            {
                title.setText(task.getTitle());
            }
            TextView time = (TextView)v.findViewById(R.id.item_time);
            Calendar c = Calendar.getInstance(TimeZone.getDefault());
            if (task.getAdvanceOrder() != AdvanceCons.ORDER_DEFAULT || task.getSetTime() != null)
            {
                c.setTimeInMillis(task.getSetTime());
            }
            else
            {
                c.setTimeInMillis(task.getAlarmTime());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(c.get(Calendar.YEAR))
                .append("-")
                .append(c.get(Calendar.MONTH) + 1)
                .append("-")
                .append(c.get(Calendar.DAY_OF_MONTH))
                .append("(")
                .append(SysUtil.getCurrentDayOfWeek(context, c.get(Calendar.DAY_OF_WEEK)))
                .append(")   ")
                .append(c.get(Calendar.HOUR_OF_DAY))
                .append(":")
                .append(SysUtil.doubleDataFormat(c.get(Calendar.MINUTE)))
                .append("  |")
                .append(repeatLabels.get(task.getRepeatType()));
            time.setText(sb.toString());
            LinearLayout desLL = (LinearLayout)v.findViewById(R.id.item_des_ll);
            TextView des = (TextView)v.findViewById(R.id.item_des);
            if (task.getDes() != null && task.getDes().length() > 0)
            {
                des.setText("☆  " + task.getDes());
                desLL.setVisibility(View.VISIBLE);
            }
            else
            {
                desLL.setVisibility(View.GONE);
            }
            LinearLayout addressLL = (LinearLayout)v.findViewById(R.id.item_address_ll);
            TextView address = (TextView)v.findViewById(R.id.item_address);
            if (task.getAddress() != null && task.getAddress().length() > 0)
            {
                address.setText("☆  " + task.getAddress());
                addressLL.setVisibility(View.VISIBLE);
            }
            else
            {
                addressLL.setVisibility(View.GONE);
            }
            LinearLayout noticeLL = (LinearLayout)v.findViewById(R.id.item_notice_ll);
            TextView notice = (TextView)v.findViewById(R.id.item_notice);
            if (task.getNotice() != null && task.getNotice().length() > 0)
            {
                notice.setText("☆  " + task.getNotice());
                noticeLL.setVisibility(View.VISIBLE);
            }
            else
            {
                noticeLL.setVisibility(View.GONE);
            }
            
            return v;
        }
        
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
        
    }
    
    class MyOncheckListener implements OnCheckedChangeListener
    {
        
        @Override
        public void onCheckedChanged(CompoundButton check, boolean flag)
        {
            switch (check.getId())
            {
                case R.id.no_scope:
                    if (flag)
                    {
                        ct.setChecked(false);
                        pro.setChecked(false);
                        province_spinner.setVisibility(View.GONE);
                        city_spinner.setVisibility(View.GONE);
                    }
                    break;
                case R.id.pro:
                    if (flag)
                    {
                        no_scope.setChecked(false);
                        province_spinner.setVisibility(View.VISIBLE);
                        city_spinner.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (ct.isChecked())
                        {
                            pro.setChecked(true);
                        }
                        else
                        {
                            province_spinner.setVisibility(View.GONE);
                            city_spinner.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.ct:
                    if (flag)
                    {
                        no_scope.setChecked(false);
                        pro.setChecked(true);
                        province_spinner.setVisibility(View.VISIBLE);
                        city_spinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if (!pro.isChecked())
                        {
                            province_spinner.setVisibility(View.GONE);
                        }
                        city_spinner.setVisibility(View.GONE);
                        
                    }
                    break;
            }
        }
    }
}
