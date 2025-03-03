package com.atguigu.service.impl;

import com.atguigu.mapper.ScheduleMapper;
import com.atguigu.pojo.Schedule;
import com.atguigu.service.ScheduleService;
import com.atguigu.utils.PageBean;
import com.atguigu.utils.R;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public R page(int pageSize, int currentPage) {
        //分页
        PageHelper.startPage(currentPage,pageSize);
        List<Schedule> scheduleList= scheduleMapper.querylist();
        //分页数据装配
        PageInfo<Schedule>info =new PageInfo<>(scheduleList);
        //装配到PageBean中
        PageBean<Schedule>pagebean = new PageBean<>(currentPage,pageSize,info.getTotal(),info.getList());
        R ok= R.ok(pagebean);
        return ok;
    }
}
