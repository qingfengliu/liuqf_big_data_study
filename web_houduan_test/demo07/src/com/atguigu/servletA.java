package com.atguigu;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/servletA")
public class servletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //向请求域存放数据
        req.setAttribute( "request","requestMessage");

        //向会话域存放数掘
        HttpSession session=req.getSession();
        session.setAttribute("session","sessionMessage");
        //向应用域存放数据
        ServletContext application=getServletContext();
        application.setAttribute("application","applicationMsessage");

        // 获取请求域数据
        String reqMessage=(String)req.getAttribute("request");
        System.out.println("请求域:"+reqMessage);

        //请求转发.如果不转发serveltB中的获取不到请求域中的数据
        req.getRequestDispatcher("servletB").forward(req,resp);
    }
}
