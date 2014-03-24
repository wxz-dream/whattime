package com.whatime.module.schedule.adapter;

import java.util.List;

import com.whatime.db.Alarm;

/**
 * 一级状态实体类
 * @author 三人行技术开发团队
 */
public class OneBar
{
    
    public OneBar(String name, List<Alarm> alarms)
    {
        this.name = name;
        this.alarms = alarms;
    }
    
    /* 状态名称 */
    public String name;
    
    /* 二级状态list */
    public List<Alarm> alarms;
    
}
