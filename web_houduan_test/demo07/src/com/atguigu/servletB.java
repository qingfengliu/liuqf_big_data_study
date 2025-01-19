package com.atguigu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servletB")
public class servletB extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求域中的数据
        String reqMessage=(String)req.getAttribute("request");
        System.out.println("请求域:"+reqMessage);

        //获取会话域中的数据

        String sessionMessage=(String)req.getSession().getAttribute("session");
        System.out.println("会话域:"+sessionMessage);

        //获取应用域中的数据
        String applicationMessage=(String)getServletContext().getAttribute("application");
        System.out.println("应用域:"+applicationMessage);
    }
}
