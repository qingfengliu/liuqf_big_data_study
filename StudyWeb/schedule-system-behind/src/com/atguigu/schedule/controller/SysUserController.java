package com.atguigu.schedule.controller;

import com.atguigu.schedule.common.Result;
import com.atguigu.schedule.common.ResultCodeEnum;
import com.atguigu.schedule.pojo.SysUser;
import com.atguigu.schedule.service.SysUserService;
import com.atguigu.schedule.service.impl.SysUserServiceImpl;
import com.atguigu.schedule.util.MD5Util;
import com.atguigu.schedule.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        //接收post请求体的参数，并封装到SysUser对象中
        SysUser registUser = WebUtil.readJson(req, SysUser.class);

        int rows = userService.regist(registUser);
        Result result = Result.ok(null);
        if (rows < 1) {
            result = Result.build(null, ResultCodeEnum.USERNAME_USED);
            WebUtil.writeJson(resp, result);
        }else {
            result = Result.ok(null);
            WebUtil.writeJson(resp, result);
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
        //接收post请求体的参数，并封装到SysUser对象中
        SysUser loginUser = WebUtil.readJson(req, SysUser.class);
        //将用户的明文密码转换为密文密码
        loginUser.setUserPwd(MD5Util.encrypt(loginUser.getUserPwd()));
        SysUser sysUser = userService.findByUserName(loginUser.getUsername());
        Result result = Result.ok(null);
        if(null == sysUser){
            result = Result.build(null, ResultCodeEnum.USERNAEM_ERROR);
        }else if(!sysUser.getUserPwd().equals(loginUser.getUserPwd())){
            result = Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }else {
            result = Result.ok(sysUser);
            //密码清空
            sysUser.setUserPwd(null);
            req.getSession().setAttribute("sysUser",sysUser);
        }
        WebUtil.writeJson(resp,result);
    }

    /*
    *按收要注册的用户名，校验用户名是否被占用的业务接口
    @param req
    * @param resp
    * @throws IOException
    *
    * 存在问题



     * */
    protected void checkUsername(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        SysUser sysUser = userService.findByUserName(username);
        Result result =Result.ok(null);
        if(null != sysUser){
            result=Result.build(null, ResultCodeEnum.USERNAME_USED);
        }

        //将result对象转换为json字符串
        WebUtil.writeJson(resp,result);

    }
}
