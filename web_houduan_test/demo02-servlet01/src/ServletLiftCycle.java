import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
* 1.实例化                 构造器 第一次请求
* 2.初始化                 init  构造完毕
  3.接收请求,处理请求 服务    service 每次请求
  4.销毁                   destory  关闭服务
    Servlet在:Tomcat中是单例的
* */
//@WebServlet("/test02")
public class ServletLiftCycle extends HttpServlet {
    public ServletLiftCycle() {
        System.out.println("构造器执行了");
    }

    @Override
    public void init() {
        System.out.println("初始化执行了");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("服务执行了");
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }

}
