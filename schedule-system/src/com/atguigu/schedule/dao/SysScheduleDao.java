package com.atguigu.schedule.dao;

import com.atguigu.schedule.pojo.SysSchedule;

/*
* 作者:
* 时间:
*
* */
public interface SysScheduleDao {
/*
* 用于向数据中增加一条日程记录
    @param schedule 日程数据以SysSchedule实体类对象形式入参
    @return 返回影响数据库记录的行数，行数为0说明增加失败,行数大于0说明增加成功
* */
    int addSchedule(SysSchedule schedule);
}
