import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Enumeration;

//@Webservlet(
//        urlPatterns ="serlvet1",
//        initParams = {@WebInitParam(name="keya",value ="valuea" ),@WebInitParam(name="keyd",value ="valueb" )}
//)
public class Test02 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        ServletConfig servletConfig = super.getServletConfig();
        String keya = servletConfig.getInitParameter("keya");
        System.out.println("keya = " + keya);

        //获取所有的初始化参数
        Enumeration<String> initParameterNames = servletConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String name = initParameterNames.nextElement();
            String value = servletConfig.getInitParameter(name);
            System.out.println(name + " = " + value);
        }

        //获取ServletContext参数
        ServletContext servletContext1 = servletConfig.getServletContext();
        ServletContext servletContext2=req.getServletContext();
        ServletContext servletContext3=getServletContext();

        String encoding = servletContext1.getInitParameter( "encoding");
        System.out.println("encoding:"+encoding);
    }

}
