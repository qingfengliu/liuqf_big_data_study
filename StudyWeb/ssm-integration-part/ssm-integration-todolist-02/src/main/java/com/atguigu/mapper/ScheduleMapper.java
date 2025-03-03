package com.atguigu.mapper;

import com.atguigu.pojo.Schedule;

import java.util.List;

public interface ScheduleMapper {
    //mapper接口
    //mybatis会自动实现这个接口
    //需要在xml文件中配置
    List<Schedule> querylist();
}
