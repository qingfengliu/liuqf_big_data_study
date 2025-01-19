package com.atguigu.servlet;

import jakarta.servlet.*;

import java.io.IOException;


/*
1 构造 构造器 项目启动
2 init 初始化方法 项目启动
3 doFilter 过滤请求的和响应的方法
    1 请求到达目标资源之前，先经过该方法
    2 该方法有能力控制请求是否继续向后到达目标资源
4 destroy 销毁方法 项目关闭
* */
public class LifeCycleFilter implements Filter {

    public LifeCycleFilter() {
        System.out.println("LifeCycleFilter's constructor invoked");
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //filterConfig.getInitParameter("name");//可以再web.xml中配置初始化参数filter.init-param.param-name/param-value
        System.out.println("LifeCycleFilter init invoked");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        System.out.println("LifeCycleFilter doFilter invoked");
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("LifeCycleFilter destroy invoked");
    }
}
