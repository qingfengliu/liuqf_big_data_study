package com.atguigu.servlet.listener;

import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyRequestListener implements ServletRequestListener, ServletRequestAttributeListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("request被销毁了");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("request被创建了");
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae){

    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae){

    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae){

    }





}
