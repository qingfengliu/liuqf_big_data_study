
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servlet1")
public class Servlet1 extends HttpServlet{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //响应重定向
        //1.设置响应状态码302
//        resp.setStatus(302);
        //2.设置响应头location
//        resp.setHeader("location","/demo04/servlet2");
        resp.sendRedirect("servlet2");


    }
}
