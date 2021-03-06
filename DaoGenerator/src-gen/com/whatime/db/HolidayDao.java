package com.whatime.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.whatime.db.Holiday;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table HOLIDAY.
*/
public class HolidayDao extends AbstractDao<Holiday, Long> {

    public static final String TABLENAME = "HOLIDAY";

    /**
     * Properties of entity Holiday.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property Country = new Property(2, String.class, "country", false, "COUNTRY");
        public final static Property DayOfYear = new Property(3, String.class, "dayOfYear", false, "DAY_OF_YEAR");
        public final static Property HolidayName = new Property(4, String.class, "holidayName", false, "HOLIDAY_NAME");
        public final static Property HolidayDes = new Property(5, String.class, "holidayDes", false, "HOLIDAY_DES");
        public final static Property Rest = new Property(6, Boolean.class, "rest", false, "REST");
        public final static Property Alarm = new Property(7, Boolean.class, "alarm", false, "ALARM");
    };


    public HolidayDao(DaoConfig config) {
        super(config);
    }
    
    public HolidayDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'HOLIDAY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UUID' TEXT," + // 1: uuid
                "'COUNTRY' TEXT," + // 2: country
                "'DAY_OF_YEAR' TEXT," + // 3: dayOfYear
                "'HOLIDAY_NAME' TEXT," + // 4: holidayName
                "'HOLIDAY_DES' TEXT," + // 5: holidayDes
                "'REST' INTEGER," + // 6: rest
                "'ALARM' INTEGER);"); // 7: alarm
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'HOLIDAY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Holiday entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String country = entity.getCountry();
        if (country != null) {
            stmt.bindString(3, country);
        }
 
        String dayOfYear = entity.getDayOfYear();
        if (dayOfYear != null) {
            stmt.bindString(4, dayOfYear);
        }
 
        String holidayName = entity.getHolidayName();
        if (holidayName != null) {
            stmt.bindString(5, holidayName);
        }
 
        String holidayDes = entity.getHolidayDes();
        if (holidayDes != null) {
            stmt.bindString(6, holidayDes);
        }
 
        Boolean rest = entity.getRest();
        if (rest != null) {
            stmt.bindLong(7, rest ? 1l: 0l);
        }
 
        Boolean alarm = entity.getAlarm();
        if (alarm != null) {
            stmt.bindLong(8, alarm ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Holiday readEntity(Cursor cursor, int offset) {
        Holiday entity = new Holiday( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // country
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // dayOfYear
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // holidayName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // holidayDes
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // rest
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0 // alarm
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Holiday entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCountry(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDayOfYear(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHolidayName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setHolidayDes(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setRest(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setAlarm(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Holiday entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Holiday entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
