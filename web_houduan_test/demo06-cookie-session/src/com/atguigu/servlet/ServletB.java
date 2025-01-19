package com.atguigu.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servletB")
public class ServletB extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 获取请求中携带的cookie
        Cookie[] cookies =req.getCookies();
        // 请求中的多个cookie会进入该数组 请求中如果没有cookiecookies数组是为null 还是长度为0?
        for (Cookie cookie :cookies){
            System.out.println(cookie.getName()+"="+cookie.getValue());
        }
    }
}
