package com.atguigu.schedule.controller;

import com.atguigu.schedule.pojo.SysUser;
import com.atguigu.schedule.service.SysUserService;
import com.atguigu.schedule.service.impl.SysUserServiceImpl;
import com.atguigu.schedule.util.MD5Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/user/*")
/*
* 增加日程的请求   /user/add
* 查询日程的请求   /user/find
* 修改日程的请求   /user/update
* 删除日程的请求   /user/remove
* */
public class SysUserController extends BaseController {

/*
* 接收用户注册请求的业务处理方法(业务接口不是java中的interface
    @param req
    @param resp
    @throwsServletException
    @throwsIOException
*
* */
    SysUserService userService = new SysUserServiceImpl();

    protected void regist(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        //1接收客户端提交的参数
        // 2 调用服务层方法,完成注册功能
        // 3 根据注册结果(成功 失败)做页面跳转
        //1按收客户端提交的参数
        String username =req.getParameter("username");
        String userPwd =req.getParameter("userPwd");

        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setUserPwd(userPwd);
        int rows=userService.regist(sysUser);
        if(rows>0) {
            //注册成功
            //跳转到登录页面
            resp.sendRedirect("/registSuccess.html");
        }else {
            //注册失败
            //跳转到注册页面
            resp.sendRedirect("/registFail.html");
        }
    }

    /*
    *
        接收用登录请求，完成的登录业务接口
        @param req
        @param resp
        @throws ServletException
        @throws IOException
    * */
    protected void login(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        //1接收客户端提交的参数
        // 2 调用服务层方法,完成注册功能
        // 3 根据注册结果(成功 失败)做页面跳转
        //1按收客户端提交的参数
        String username =req.getParameter("username");
        String userPwd =req.getParameter("userPwd");


//        SysUser sysUser = new SysUser();
        SysUser loginUser=userService.findByUserName(username);
        if(null == loginUser) {
            resp.sendRedirect("/loginUsernameError.html");
        }else if(!MD5Util.encrypt(userPwd).equals(loginUser.getUserPwd())) {
            resp.sendRedirect("/loginUserPwdError.html");
        }else {
            resp.sendRedirect("/showSchedule.html");
        }
    }
}
