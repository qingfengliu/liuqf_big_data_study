package com.atguigu.schedule.service;

import com.atguigu.schedule.pojo.SysSchedule;

import java.util.List;

public interface SysScheduleService {
    List<SysSchedule> findItemListByuid(int uid);

    void addDefault(int uid);

    Integer updateSchedule(SysSchedule schedule);

    void removeschedule(int sid);
}
