package com.whatime.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.whatime.db.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table USER.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property AuthType = new Property(2, Integer.class, "authType", false, "AUTH_TYPE");
        public final static Property Available = new Property(3, Boolean.class, "available", false, "AVAILABLE");
        public final static Property City = new Property(4, String.class, "city", false, "CITY");
        public final static Property CreateTime = new Property(5, Long.class, "createTime", false, "CREATE_TIME");
        public final static Property Email = new Property(6, String.class, "email", false, "EMAIL");
        public final static Property IdentityCard = new Property(7, String.class, "identityCard", false, "IDENTITY_CARD");
        public final static Property Del = new Property(8, Boolean.class, "del", false, "DEL");
        public final static Property LevelUuid = new Property(9, String.class, "levelUuid", false, "LEVEL_UUID");
        public final static Property LoginTime = new Property(10, Long.class, "loginTime", false, "LOGIN_TIME");
        public final static Property Mime = new Property(11, String.class, "mime", false, "MIME");
        public final static Property NickName = new Property(12, String.class, "nickName", false, "NICK_NAME");
        public final static Property Password = new Property(13, String.class, "password", false, "PASSWORD");
        public final static Property PhoneInfo = new Property(14, String.class, "phoneInfo", false, "PHONE_INFO");
        public final static Property Qq = new Property(15, String.class, "qq", false, "QQ");
        public final static Property RealName = new Property(16, String.class, "realName", false, "REAL_NAME");
        public final static Property Telphone = new Property(17, String.class, "telphone", false, "TELPHONE");
        public final static Property UptTime = new Property(18, Long.class, "uptTime", false, "UPT_TIME");
        public final static Property UserName = new Property(19, String.class, "userName", false, "USER_NAME");
        public final static Property UserphotoUri = new Property(20, String.class, "userphotoUri", false, "USERPHOTO_URI");
        public final static Property Sex = new Property(21, Integer.class, "sex", false, "SEX");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'USER' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UUID' TEXT," + // 1: uuid
                "'AUTH_TYPE' INTEGER," + // 2: authType
                "'AVAILABLE' INTEGER," + // 3: available
                "'CITY' TEXT," + // 4: city
                "'CREATE_TIME' INTEGER," + // 5: createTime
                "'EMAIL' TEXT," + // 6: email
                "'IDENTITY_CARD' TEXT," + // 7: identityCard
                "'DEL' INTEGER," + // 8: del
                "'LEVEL_UUID' TEXT," + // 9: levelUuid
                "'LOGIN_TIME' INTEGER," + // 10: loginTime
                "'MIME' TEXT," + // 11: mime
                "'NICK_NAME' TEXT," + // 12: nickName
                "'PASSWORD' TEXT," + // 13: password
                "'PHONE_INFO' TEXT," + // 14: phoneInfo
                "'QQ' TEXT," + // 15: qq
                "'REAL_NAME' TEXT," + // 16: realName
                "'TELPHONE' TEXT," + // 17: telphone
                "'UPT_TIME' INTEGER," + // 18: uptTime
                "'USER_NAME' TEXT," + // 19: userName
                "'USERPHOTO_URI' TEXT," + // 20: userphotoUri
                "'SEX' INTEGER);"); // 21: sex
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'USER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        Integer authType = entity.getAuthType();
        if (authType != null) {
            stmt.bindLong(3, authType);
        }
 
        Boolean available = entity.getAvailable();
        if (available != null) {
            stmt.bindLong(4, available ? 1l: 0l);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(5, city);
        }
 
        Long createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(6, createTime);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(7, email);
        }
 
        String identityCard = entity.getIdentityCard();
        if (identityCard != null) {
            stmt.bindString(8, identityCard);
        }
 
        Boolean del = entity.getDel();
        if (del != null) {
            stmt.bindLong(9, del ? 1l: 0l);
        }
 
        String levelUuid = entity.getLevelUuid();
        if (levelUuid != null) {
            stmt.bindString(10, levelUuid);
        }
 
        Long loginTime = entity.getLoginTime();
        if (loginTime != null) {
            stmt.bindLong(11, loginTime);
        }
 
        String mime = entity.getMime();
        if (mime != null) {
            stmt.bindString(12, mime);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(13, nickName);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(14, password);
        }
 
        String phoneInfo = entity.getPhoneInfo();
        if (phoneInfo != null) {
            stmt.bindString(15, phoneInfo);
        }
 
        String qq = entity.getQq();
        if (qq != null) {
            stmt.bindString(16, qq);
        }
 
        String realName = entity.getRealName();
        if (realName != null) {
            stmt.bindString(17, realName);
        }
 
        String telphone = entity.getTelphone();
        if (telphone != null) {
            stmt.bindString(18, telphone);
        }
 
        Long uptTime = entity.getUptTime();
        if (uptTime != null) {
            stmt.bindLong(19, uptTime);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(20, userName);
        }
 
        String userphotoUri = entity.getUserphotoUri();
        if (userphotoUri != null) {
            stmt.bindString(21, userphotoUri);
        }
 
        Integer sex = entity.getSex();
        if (sex != null) {
            stmt.bindLong(22, sex);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // authType
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // available
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // city
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // createTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // email
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // identityCard
            cursor.isNull(offset + 8) ? null : cursor.getShort(offset + 8) != 0, // del
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // levelUuid
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // loginTime
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // mime
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // nickName
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // password
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // phoneInfo
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // qq
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // realName
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // telphone
            cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18), // uptTime
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // userName
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // userphotoUri
            cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21) // sex
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAuthType(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setAvailable(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setCity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreateTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setEmail(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIdentityCard(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDel(cursor.isNull(offset + 8) ? null : cursor.getShort(offset + 8) != 0);
        entity.setLevelUuid(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLoginTime(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setMime(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setNickName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setPassword(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setPhoneInfo(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setQq(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setRealName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setTelphone(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setUptTime(cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18));
        entity.setUserName(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setUserphotoUri(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setSex(cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
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
