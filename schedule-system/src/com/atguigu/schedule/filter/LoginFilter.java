package com.atguigu.schedule.filter;

import com.atguigu.schedule.pojo.SysUser;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns={"/showSchedule.htm1","/schedule/*"},servletNames="scheduleServlet")
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //初始化方法
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        //过滤方法
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse resp=(HttpServletResponse)servletResponse;

        //获得session域对象
        SysUser loginUser = (SysUser)request.getSession().getAttribute("sysUser");

        //判断是否登录
        if(loginUser==null) {
            //未登录
            resp.sendRedirect("/login.html");
        }else {
            //已登录
            chain.doFilter(servletRequest,servletResponse);
        }

    }
}
