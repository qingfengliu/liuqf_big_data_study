package com.atguigu.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet("/servlet1")
public class Servlet1 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获得session对象
        HttpSession session = req.getSession();

        System.out.println(session.getId());System.out.println(session.isNew());
        //将username存入
        session.setAttribute("username","what");
        // 客户端响应信息
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write("成功");


    }
}
