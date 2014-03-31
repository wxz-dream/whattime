/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.whatime.db");

        addUser(schema);
        addHoliday(schema);
        addAlarm(schema);
        //../DaoGenerator
        new DaoGenerator().generateAll(schema, "./src-gen");
    }

    private static void addHoliday(Schema schema) {
    	Entity hos = schema.addEntity("Holiday");
    	hos.addIdProperty();
    	hos.addStringProperty("uuid");
    	hos.addStringProperty("country");
    	hos.addStringProperty("dayOfYear");
    	hos.addStringProperty("holidayName");
    	hos.addStringProperty("holidayDes");
    	hos.addBooleanProperty("rest");
    	hos.addBooleanProperty("alarm");
        
	}

	private static void addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.addStringProperty("uuid");
        user.addIntProperty("authType");
        user.addBooleanProperty("available");
        user.addStringProperty("city");
        user.addLongProperty("createTime");
        user.addStringProperty("email");
        user.addStringProperty("identityCard");
        user.addBooleanProperty("del");
        user.addStringProperty("levelUuid");
        user.addLongProperty("loginTime");
        user.addStringProperty("mime");
        user.addStringProperty("nickName");
        user.addStringProperty("password");
        user.addStringProperty("phoneInfo");
        user.addStringProperty("qq");
        user.addStringProperty("realName");
        user.addStringProperty("telphone");
        user.addLongProperty("uptTime");
        user.addStringProperty("userName");
        user.addStringProperty("userphotoUri");
        user.addIntProperty("sex");
    }

    private static void addAlarm(Schema schema) {
        Entity alarm = schema.addEntity("Alarm");
        alarm.addIdProperty();
        alarm.addStringProperty("uuid");
        alarm.addLongProperty("alarmTime");
        Property cateUuid = alarm.addLongProperty("cateId").getProperty();
        Entity cate = schema.addEntity("Category");
        cate.addIdProperty();
        cate.addStringProperty("name");
        cate.addStringProperty("des");
        cate.addLongProperty("parentId");
        cate.addStringProperty("imgUri");
        cate.addBooleanProperty("del");
        alarm.addToOne(cate, cateUuid);
        alarm.addLongProperty("createTime");
        alarm.addStringProperty("des");
        alarm.addLongProperty("endTime");
        alarm.addIntProperty("froms");
        alarm.addBooleanProperty("del");
        alarm.addBooleanProperty("endJoin");
        alarm.addBooleanProperty("open");
        alarm.addLongProperty("joinNum");
        alarm.addStringProperty("linkman");
        alarm.addLongProperty("maxJoinNum");
        alarm.addStringProperty("scope");
        alarm.addStringProperty("share");
        alarm.addLongProperty("syncTime");
        Property alarmTaskUuid = alarm.addLongProperty("taskId").getProperty();
        alarm.addStringProperty("taskUuid");
        alarm.addStringProperty("title");
        alarm.addIntProperty("type");
        alarm.addLongProperty("uptTime");
        alarm.addStringProperty("userUuid");
        alarm.addBooleanProperty("allowChange");
        alarm.addStringProperty("owerUuid");
        alarm.addStringProperty("owerUserUuid");
        Entity task = schema.addEntity("Task");
        task.addIdProperty();
        task.addStringProperty("uuid");
        Property alarmUuid = task.addLongProperty("alarmId").getProperty();
        task.addStringProperty("address");
        task.addLongProperty("alarmTime");
        task.addStringProperty("alarmUuid");
        task.addStringProperty("backgroundUri");
        task.addIntProperty("clockType");
        task.addLongProperty("createTime");
        task.addLongProperty("setTime");
        task.addIntProperty("advanceOrder");
        task.addStringProperty("des");
        task.addStringProperty("gpsAddress");
        task.addBooleanProperty("del");
        task.addBooleanProperty("open");
        task.addStringProperty("music");
        task.addStringProperty("notice");
        task.addIntProperty("playMinute");
        task.addStringProperty("playMusic");
        task.addIntProperty("playType");
        task.addStringProperty("repeatInfo");
        task.addIntProperty("repeatType");
        task.addBooleanProperty("shake");
        task.addBooleanProperty("skip");
        task.addStringProperty("surpervise");
        task.addLongProperty("syncTime");
        task.addStringProperty("title");
        task.addLongProperty("uptTime");
        //task添加一对一alarm
        task.addToOne(alarm, alarmUuid);
        //alarm添加一对一task
        alarm.addToOne(task, alarmTaskUuid);
        //alarm添加一对多tasks
        ToMany tasks = alarm.addToMany(task, alarmUuid);
        tasks.setName("tasks");
    }

}
