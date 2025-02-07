package com.atguigu.schedule.controller;

import com.atguigu.schedule.common.Result;
import com.atguigu.schedule.pojo.SysSchedule;
import com.atguigu.schedule.service.SysScheduleService;
import com.atguigu.schedule.service.impl.SysScheduleServiceImpl;
import com.atguigu.schedule.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(value = "/schedule/*",name="scheduleServlet")
/*
* 增加日程的请求   /schedule/add
* 查询日程的请求   /schedule/find
* 修改日程的请求   /schedule/update
* 删除日程的请求   /schedule/remove
* */
public class SysScheduleController extends BaseController {
    private SysScheduleService scheduleService = new SysScheduleServiceImpl();

    protected void findAllSchedule(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        //接收请求参数。

        int uid =Integer.parseInt(req.getParameter("uid"));


        // 查询所有的日程信息。
        List<SysSchedule> itemList =scheduleService.findItemListByuid(uid);
        // 将日程信息存储到Result对象中
        Map data = new HashMap();
        data.put("itemList",itemList);
        Result result = Result.ok(data);
        // 将Result对象转换为json相应给客户端。
        WebUtil.writeJson(resp,result);
    }

    protected void addSchedule(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
        // 接收请求中的uid参数
        int uid =Integer.parseInt(req.getParameter("uid"));
        // 调用服务层方法,向数据库中 增加一条 空记录
        scheduleService.addDefault(uid);

        WebUtil.writeJson(resp,Result.ok(null));
    }

    protected void updateSchedule(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
        //接收请求体中的ISON串 转换成一个sysSchedule
        SysSchedule schedule = WebUtil.readJson(req,SysSchedule.class);

        // 调用服务层方法,向数据库中 增加一条 空记录
        scheduleService.updateSchedule(schedule);
        WebUtil.writeJson(resp,Result.ok(null));
    }

    protected void add(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        System.out.println("add");
    }

    protected void deleteSchedule(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        int sid = Integer.parseInt(req.getParameter("sid"));
        // 调用服务层方法 删除数据
        scheduleService.removeschedule(sid);
        // 响应 成功信息
        WebUtil.writeJson(resp,Result.ok(null));
    }

}
