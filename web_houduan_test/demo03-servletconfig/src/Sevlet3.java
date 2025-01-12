import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;

@WebServlet(urlPatterns ="/sevlet3")
public class Sevlet3 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException {
        ServletContext servletContext=getServletContext();
        String path = servletContext.getRealPath("upload");
        System.out.println("path = " + path);

        // 获取项目的上下文路径 项目的部署路径
        String contextPath=servletContext.getContextPath();
        System.out.println("contextPath = " + contextPath);

        servletContext.setAttribute("ka","va");
//        servletContext.setAttribute("ka","vaa");
//
//        String ka=(String)servletContext.getAttribute( "ka");


    }

}
