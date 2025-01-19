package filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingFilter implements Filter {
    /*
    * 过滤请求的和响应的方法
        1 请求到达目标资源之前，先经过该方法
        2 该方法有能力控制请求是否继续向后到达目标资源
    * */

    private SimpleDateFormat dateFonmat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        /*
        * 1 请求到达目标资源之前的功能代码
        *   判断是否登录
        *   校验权限是否满足
        *
        * 2 放行
        * 3 响应之前 HttpServletResponse 转换为响应报文之前的功能代码
        *
        */

        //参数父转子
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;
        // 请求到达目标资源之前 打印日志yyyy-MM-dd HH:mm:ss ***被访问
        String requestURI= request.getRequestURI();
        String dateTime =dateFonmat.format(new Date());
        String beforeLogging= requestURI+"在"+dateTime+"被访问了";
        System.out.println(beforeLogging);

        long t1=System.currentTimeMillis();

        // 放行
        chain.doFilter(request,response);

        long t2=System.currentTimeMillis();
        // 响应之前的功能代码 ***资源在HH:mm:ss的请求耗时毫秒
        String afterLogging=requestURI+"资源在"+dateTime+"的请求耗时:"+(t2-t1)+"毫秒";
        System.out.println(afterLogging);
    }

    @Override
    public void destroy() {
        System.out.println("LoggingFilter destroy invoked");
    }
}
