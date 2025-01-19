package com.atguigu.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Servlet1 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取应用域对象
        ServletContext application = this.getServletContext();
        application.setAttribute("keya", "valuea");

        //获取session对象
        req.getSession().setAttribute("keyb", "valueb");

        //关闭session
        req.getSession().invalidate();

    }
}
