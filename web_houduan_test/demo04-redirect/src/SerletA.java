import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servletA")
public class SerletA extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //请求转发给ServletB
        // 获得请求转发器
        RequestDispatcher reguestDispatcher = req.getRequestDispatcher("servletB");

        //让请求转发器做出转发动作

        reguestDispatcher.forward(req,resp);


    }
}
