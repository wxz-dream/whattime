package com.whatime.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.whatime.controller.alarm.AlarmCons;
import com.whatime.controller.alarm.AdvanceCons;
import com.whatime.controller.alarm.PlayDelayCons;
import com.whatime.controller.alarm.RepeatCons;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.util.SysUtil;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition.StringCondition;

public class DBHelper
{
    private static Context mContext = MyApp.getInstance().getApplicationContext();
    
    private static DBHelper instance;
    
    private AlarmDao alarmDao;
    
    private TaskDao taskDao;
    
    private HolidayDao hosDao;
    
    private UserDao userDao;
    
    private CategoryDao cateDao;
    
    private DBHelper()
    {
    }
    
    public static DBHelper getInstance()
    {
        if (instance == null)
        {
            instance = new DBHelper();
            
            // 数据库对象
            DaoSession daoSession = MyApp.getDaoSession(mContext);
            instance.alarmDao = daoSession.getAlarmDao();
            instance.taskDao = daoSession.getTaskDao();
            instance.hosDao = daoSession.getHolidayDao();
            instance.userDao = daoSession.getUserDao();
            instance.cateDao = daoSession.getCategoryDao();
        }
        return instance;
    }
    
    public List<Category> getAllCateByParentId(long pare_id)
    {
        QueryBuilder<Category> qb = cateDao.queryBuilder();
        qb.where(com.whatime.db.CategoryDao.Properties.ParentId.eq(pare_id))
            .orderAsc(com.whatime.db.CategoryDao.Properties.ParentId);
        return qb.list();
    }
    
    // 添加用户
    public long addUser(User user)
    {
        user.setCreateTime(System.currentTimeMillis());
        return userDao.insert(user);
    }
    
    //更新
    public void uptUser(User user)
    {
        user.setUptTime(System.currentTimeMillis());
        userDao.update(user);
    }
    
    public User getUser()
    {
        List<User> us = userDao.loadAll();
        if (us.size() == 0)
        {
            return null;
        }
        return us.get(0);
    }
    
    /** 添加数据 
     * @param tasks */
    public long addAlarm(Alarm item)
    {
        item.setCreateTime(System.currentTimeMillis());
        return alarmDao.insert(item);
    }
    
    /**
     * 更新
     * @param alarm
     */
    public void uptAlarm(Alarm alarm)
    {
        alarm.setUptTime(System.currentTimeMillis());
        alarmDao.update(alarm);
    }
    
    public void uptAlarmIsOpen(long id, boolean isOpen)
    {
        Alarm alarm = getAlarmById(id);
        alarm.setOpen(isOpen);
        uptAlarm(alarm);
    }
    
    /** 查询 */
    public List<Alarm> getOpenAlarms()
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        qb.where(com.whatime.db.AlarmDao.Properties.Open.eq(true), com.whatime.db.AlarmDao.Properties.Del.eq(false))
            .orderAsc(com.whatime.db.AlarmDao.Properties.AlarmTime);
        return qb.list();
    }
    
    /** 查询 */
    public List<Alarm> getAllAlarmByTime(long start, long end)
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        qb.where(new StringCondition("ALARM_TIME BETWEEN " + start + " AND " + end),
            com.whatime.db.AlarmDao.Properties.Del.eq(false));
        qb.orderAsc(com.whatime.db.AlarmDao.Properties.AlarmTime);
        return qb.list();
    }
    
    /** 查询 */
    public Alarm getAlarmById(long id)
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        qb.where(com.whatime.db.AlarmDao.Properties.Id.eq(id));
        if (qb.buildCount().count() > 0)
        {
            return qb.list().get(0);
        }
        return null;
    }
    
    /** 查询 */
    public boolean isSaved(long id)
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        qb.where(com.whatime.db.AlarmDao.Properties.Id.eq(id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;// 查找收藏表
    }
    
    /** 删除 */
    public void deleteAlarmList(long Id)
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        DeleteQuery<Alarm> bd = qb.where(com.whatime.db.AlarmDao.Properties.Id.eq(Id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    /** 删除 */
    public void clearAlarm()
    {
        alarmDao.deleteAll();
    }
    
    //获取task
    public Task getTaskById(long id)
    {
        QueryBuilder<Task> qb = taskDao.queryBuilder();
        qb.where(com.whatime.db.TaskDao.Properties.Id.eq(id));
        if (qb.buildCount().count() > 0)
        {
            return qb.list().get(0);
        }
        return null;
    }
    
    //获取task
    public Task getNextTaskByAlarmId(long id)
    {
        QueryBuilder<Task> qb = taskDao.queryBuilder();
        qb.where(com.whatime.db.TaskDao.Properties.AlarmId.eq(id),
            com.whatime.db.TaskDao.Properties.AlarmTime.gt(System.currentTimeMillis()),
            com.whatime.db.TaskDao.Properties.Del.eq(false),
            com.whatime.db.TaskDao.Properties.Open.eq(true)).orderAsc(com.whatime.db.TaskDao.Properties.AlarmTime);
        if (qb.buildCount().count() > 0)
        {
            return qb.list().get(0);
        }
        return null;
    }
    
    public void uptTask(Task task)
    {
        task.setUptTime(System.currentTimeMillis());
        taskDao.update(task);
    }
    
    public void deleteTask(long key)
    {
        taskDao.deleteByKey(key);
    }
    
    public void deleteTaskByAlarmId(Long id)
    {
        QueryBuilder<Task> qb = taskDao.queryBuilder();
        qb.where(com.whatime.db.TaskDao.Properties.AlarmId.eq(id));
        if (qb.buildCount().count() > 0)
        {
            List<Task> ts = qb.list();
            Iterator<Task> it = ts.iterator();
            while (it.hasNext())
            {
                taskDao.delete(it.next());
            }
        }
    }
    
    /** 添加数据 */
    public long addTask(Task item)
    {
        item.setCreateTime(System.currentTimeMillis());
        return taskDao.insert(item);
    }
    
    //获取假日
    public long addHos(Holiday hos)
    {
        return hosDao.insert(hos);
    }
    
    //获取响铃的假日
    public List<Holiday> getAllHolidaysForAlarm()
    {
        QueryBuilder<Holiday> qb = hosDao.queryBuilder();
        qb.where(com.whatime.db.HolidayDao.Properties.Alarm.eq(true));
        return qb.list();
    }
    
    //获取休息的假日
    public List<Holiday> getAllHolidaysForRest()
    {
        QueryBuilder<Holiday> qb = hosDao.queryBuilder();
        qb.where(com.whatime.db.HolidayDao.Properties.Rest.eq(true));
        return qb.list();
    }
    
    public static void initData(SQLiteDatabase db)
    {
        insertDataFromAssert(db);
        String alarmUuid = UUID.randomUUID().toString();
        long time = SysUtil.getTime(-1, -1, -1, 7, 30, 0);
        Calendar cal = getGetupTime(time);
        String taskUuid = UUID.randomUUID().toString();
        db.execSQL("INSERT INTO TASK (UUID,ALARM_ID,ALARM_UUID,TITLE,DES,ALARM_TIME,REPEAT_TYPE,REPEAT_INFO,"
            + "ADVANCE_ORDER,PLAY_TYPE,PLAY_MINUTE,PLAY_MUSIC,SHAKE,CREATE_TIME,UPT_TIME,SYNC_TIME,OPEN,DEL) values ('"
            + taskUuid + "',1,'" + alarmUuid + "','起床了~','早起的鸟儿有虫吃'," + +cal.getTimeInMillis() + ","
            + RepeatCons.TYPE_WORK + ",''," + AdvanceCons.ORDER_DEFAULT + "," + PlayDelayCons.TYPE_DEFAULT + ",0,'',"
            + "1," + System.currentTimeMillis() + ",0,0,1,0)");
        db.execSQL("INSERT INTO ALARM (UUID,TYPE,TITLE,DES,OPEN,ALARM_TIME,CREATE_TIME,UPT_TIME,SYNC_TIME,FROMS,TASK_ID,TASK_UUID,DEL) values ('"
            + alarmUuid
            + "',"
            + AlarmCons.TYPE_GETUP
            + ",'起床闹钟','懒虫起床啦',1,"
            + cal.getTimeInMillis()
            + ","
            + System.currentTimeMillis() + ",0,0," + AlarmCons.FROMS_ANDROID + ",1,'" + taskUuid + "',0)");
        db.execSQL("INSERT INTO CATEGORY (_id,NAME,DESC) values (0,'根分类','it is gen')");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('健康生活','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('商家活动','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('结伴旅行','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('热播影视','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('热玩游戏','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('校园生活','',0)");
        db.execSQL("INSERT INTO CATEGORY (NAME,DESC,PARENT_ID) values ('其它','',0)");
    }
    
    private static void insertDataFromAssert(SQLiteDatabase db)
    {
        for (Holiday h : getDataFromAssert())
        {
            db.execSQL("INSERT INTO HOLIDAY (UUID,COUNTRY,DAY_OF_YEAR,HOLIDAY_NAME,HOLIDAY_DES,REST,ALARM)"
                + " VALUES('" + h.getUuid() + "','" + h.getCountry() + "','" + h.getDayOfYear() + "','"
                + h.getHolidayName() + "','" + h.getHolidayDes() + "'," + (h.getRest() ? 1 : 0) + ","
                + (h.getAlarm() ? 1 : 0) + ")");
        }
    }
    
    private static ArrayList<Holiday> getDataFromAssert()
    {
        ArrayList<Holiday> hos = new ArrayList<Holiday>();
        try
        {
            InputStream in = mContext.getResources().getAssets().open("holidays.txt");
            // 获取文件的字节数
            int lenght = in.available();
            if (lenght == 0)
            {
                return hos;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String data = "";
            while ((data = reader.readLine()) != null)
            {
                String[] datas = data.split("\\|");
                Holiday holiday = new Holiday();
                holiday.setUuid(UUID.randomUUID().toString());
                holiday.setCountry(datas[0]);
                holiday.setDayOfYear(datas[1]);
                holiday.setHolidayName(datas[2]);
                holiday.setHolidayDes(datas[3]);
                holiday.setRest(Integer.valueOf(datas[4]) == 1);
                holiday.setAlarm(Integer.valueOf(datas[5]) == 1);
                hos.add(holiday);
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return hos;
    }
    
    private static Calendar getGetupTime(long time)
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        judgeCalendar(cal);
        // 如果今天不需要响铃了
        if (cal.getTimeInMillis() < System.currentTimeMillis())
        {
            cal.add(Calendar.DATE, 1);
        }
        judgeCalendar(cal);
        
        return cal;
    }
    
    private static void judgeCalendar(Calendar cal)
    {
        StringBuilder date = new StringBuilder();
        date.append(cal.get(Calendar.YEAR))
            .append("-")
            .append(cal.get(Calendar.MONTH))
            .append("-")
            .append(cal.get(Calendar.DAY_OF_MONTH));
        if (isHolidayForRest(date.toString()))
        {
            cal.add(Calendar.DATE, 1);
            judgeCalendar(cal);
        }
    }
    
    private static boolean isHolidayForRest(String date)
    {
        for (Holiday hos : getDataFromAssert())
        {
            if (hos.getDayOfYear().equals(date) && hos.getRest())
            {
                return true;
            }
        }
        return false;
    }
    
    public long getAlarmMaxSyncTime()
    {
        QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
        qb.orderAsc(com.whatime.db.AlarmDao.Properties.SyncTime);
        if (qb.buildCount().count() > 0)
        {
            return qb.list().get(0).getAlarmTime();
        }
        return 0;
    }
    
    public int getCategoryCount()
    {
        return (int)cateDao.count();
    }
    
    public long getTaskMaxUptTime()
    {
        QueryBuilder<Task> qb = taskDao.queryBuilder();
        qb.orderAsc(com.whatime.db.TaskDao.Properties.UptTime);
        if (qb.buildCount().count() > 0)
        {
            Long uptTime = qb.list().get(0).getUptTime();
            if (null != uptTime)
            {
                return uptTime;
            }
        }
        return 0;
    }
    
}
