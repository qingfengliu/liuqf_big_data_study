//import jakarta.servlet.Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
/*
servlet开发流程
    1. 创建iavaWEB项目，同时将tomcat添加为当前项目的依赖

    2. 重写service力法 service(HttpServletRequest reg, HttpServletResponse resp)

    3.在service方法中,定义业务处理代码

    4.在web.xml中,配置servlet 对应的请求映射路径
*/


//public class TestServlet01 implements Servlet {

@WebServlet("/test01")  //也可以通过注解的方式配置servlet的映射路径
public class TestServlet01 extends HttpServlet {
    public TestServlet01() {
        System.out.println("TestServlet01 constructor");
    }

    @Override
    protected void service (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1 从request 对象中获取请求中的任何信息(username参数)
        String username=request.getParameter("username");


        //2 处理业务的代码
        String info ="YES";

        if("atguigu".equals(username)) {
            info ="NO";
        }


        //3 将要响应的数据放入response
        PrintWriter writer =response.getWriter();
        writer.write(info);


    }


}
