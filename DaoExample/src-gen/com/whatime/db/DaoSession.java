package com.whatime.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.whatime.db.User;
import com.whatime.db.Holiday;
import com.whatime.db.Alarm;
import com.whatime.db.Category;
import com.whatime.db.Task;

import com.whatime.db.UserDao;
import com.whatime.db.HolidayDao;
import com.whatime.db.AlarmDao;
import com.whatime.db.CategoryDao;
import com.whatime.db.TaskDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig holidayDaoConfig;
    private final DaoConfig alarmDaoConfig;
    private final DaoConfig categoryDaoConfig;
    private final DaoConfig taskDaoConfig;

    private final UserDao userDao;
    private final HolidayDao holidayDao;
    private final AlarmDao alarmDao;
    private final CategoryDao categoryDao;
    private final TaskDao taskDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        holidayDaoConfig = daoConfigMap.get(HolidayDao.class).clone();
        holidayDaoConfig.initIdentityScope(type);

        alarmDaoConfig = daoConfigMap.get(AlarmDao.class).clone();
        alarmDaoConfig.initIdentityScope(type);

        categoryDaoConfig = daoConfigMap.get(CategoryDao.class).clone();
        categoryDaoConfig.initIdentityScope(type);

        taskDaoConfig = daoConfigMap.get(TaskDao.class).clone();
        taskDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        holidayDao = new HolidayDao(holidayDaoConfig, this);
        alarmDao = new AlarmDao(alarmDaoConfig, this);
        categoryDao = new CategoryDao(categoryDaoConfig, this);
        taskDao = new TaskDao(taskDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Holiday.class, holidayDao);
        registerDao(Alarm.class, alarmDao);
        registerDao(Category.class, categoryDao);
        registerDao(Task.class, taskDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        holidayDaoConfig.getIdentityScope().clear();
        alarmDaoConfig.getIdentityScope().clear();
        categoryDaoConfig.getIdentityScope().clear();
        taskDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public HolidayDao getHolidayDao() {
        return holidayDao;
    }

    public AlarmDao getAlarmDao() {
        return alarmDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

}
