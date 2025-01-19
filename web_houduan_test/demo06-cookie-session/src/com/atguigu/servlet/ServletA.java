package com.atguigu.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servletA")
public class ServletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 创建Cookie对象
        Cookie cookie = new Cookie("name", "zhangsan");
        // 设置Cookie的有效期
        cookie.setMaxAge(60 * 60);
        // 设置Cookie的提交路径，不在这个路径下的请求不会携带这个Cookie
        cookie.setPath("/demo06");
        // 将Cookie发送给浏览器
        res.addCookie(cookie);
    }
}
