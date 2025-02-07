package com.atguigu.schedule.dao.impl;

import com.atguigu.schedule.dao.BaseDao;
import com.atguigu.schedule.dao.SysScheduleDao;
import com.atguigu.schedule.pojo.SysSchedule;

import java.util.List;

public class SysScheduleDaoImpl extends BaseDao implements SysScheduleDao {
    @Override
    public int addSchedule(SysSchedule schedule){
        String sql ="insert into sys_schedule values(DEFAULT,?,?,?)";
        int rows=baseUpdate(sql,schedule.getUid(),schedule.getTitle(),schedule.getCompleted());
        return rows;
    }

    @Override
    public List<SysSchedule> findAll(Integer uid){
        String sql="select sid,uid,title,completed from sys_schedule";
        List<SysSchedule> sysScheduleList = baseQuery(SysSchedule.class, sql, uid);
        return sysScheduleList;
    }

    @Override
    public List<SysSchedule> findItemListByuid(int uid){
        String sql="select sid,uid,title,completed from sys_schedule where uid=?";
        List<SysSchedule> sysScheduleList = baseQuery(SysSchedule.class, sql, uid);
        return sysScheduleList;
    }

    @Override
    public void addDefault(int uid){
        String sql="insert into sys_schedule values(DEFAULT,?,?,?)";
        baseUpdate(sql,uid,"",0);
    }

    @Override
    public Integer updateSchedule(SysSchedule schedule){
        String sql="update sys_schedule set title=?,completed=? where sid=?";
        int rows = baseUpdate(sql, schedule.getTitle(), schedule.getCompleted(), schedule.getSid());
        return rows;
    }

    @Override
    public Integer removeschedule(int sid){
        String sql="delete from sys_schedule where sid=?";
        return baseUpdate(sql,sid);
    }
}
