package com.atguigu.servlet.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyApplicationListener implements ServletContextListener, ServletContextAttributeListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext application=sce.getServletContext();
        System.out.println(application.hashCode()+"应用域对象被创建了");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext application=sce.getServletContext();
        System.out.println(application.hashCode()+"ServletContext对象被销毁了");
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent scae) {
        ServletContext application=scae.getServletContext();
        String key = scae.getName();
        Object value= scae.getValue();
        System.out.println(application.hashCode()+"应用城增加了"+key+":"+value);
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scae) {
        ServletContext application=scae.getServletContext();
        String key = scae.getName();
        Object value= scae.getValue();
        System.out.println(application.hashCode()+"移除了"+key+":"+value);
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scae) {
        ServletContext application=scae.getServletContext();
        String key = scae.getName();
        Object value= scae.getValue();  //获取修改之前的值
        Object newValue= application.getAttribute(key);// 获取最新的值
        System.out.println(application.hashCode()+"应用域修改了"+key+":"+value+"为:"+newValue);
    }





}
