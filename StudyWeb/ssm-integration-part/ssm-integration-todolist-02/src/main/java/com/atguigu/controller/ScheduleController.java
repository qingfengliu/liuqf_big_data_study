package com.atguigu.controller;

import com.atguigu.pojo.Schedule;
import com.atguigu.service.ScheduleService;
import com.atguigu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin    // 允许跨域
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

    @DeleteMapping("/{id}")
    public R remove(@PathVariable(value = "id") int id){
        R r=scheduleService.remove(id);
        log.info("删除结果：{}",r);
        return r;
    }

    @PostMapping("/add")
    public R save(@Validated @RequestBody Schedule schedule, BindingResult result){
        if(result.hasErrors()){
            return R.fail(result.getFieldError().getDefaultMessage());
        }
        R r=scheduleService.save(schedule);
        log.info("添加结果：{}",r);
        return r;
    }

    @PutMapping
    public R update(@Validated @RequestBody Schedule schedule, BindingResult result){
        if(result.hasErrors()){
            return R.fail(result.getFieldError().getDefaultMessage());
        }
        R r=scheduleService.update(schedule);
        log.info("更新结果：{}",r);
        return r;
    }
}
