package com.atguigu.servlet.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class MySessionListener implements HttpSessionListener, HttpSessionAttributeListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("session被创建了");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("session被销毁了");
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        System.out.println("session增加了属性");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        System.out.println("session移除了属性");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        System.out.println("session修改了属性");
    }
}
