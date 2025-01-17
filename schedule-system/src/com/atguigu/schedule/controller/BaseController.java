package com.atguigu.schedule.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //判断此次请求是 增、删、改、查
        String requestURI = req.getRequestURI();//    /schedul/adde
        String[] split = requestURI.split("/");
        String methodName = split[split.length - 1];

        Class aClass = this.getClass();
        try {
            Method declaredMethod =aClass.getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            //暴力 破解方法的访问修饰待的限制
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this,req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
