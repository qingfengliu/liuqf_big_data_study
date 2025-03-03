package com.atguigu.controller;

import com.atguigu.service.ScheduleService;
import com.atguigu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("schedule")
@Slf4j
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    @GetMapping("/{pageSize}/{currentPage}")
    public R page(@PathVariable(value = "pageSize") int pageSize,
                  @PathVariable(value = "currentPage") int currentPage){
        R r=scheduleService.page(pageSize,currentPage);
        log.info("查询结果：{}",r);
        return r;
    }

}
