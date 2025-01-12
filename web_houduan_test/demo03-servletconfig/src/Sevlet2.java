import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;

@WebServlet(urlPatterns ="/sevlet2")
public class Sevlet2 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException {
        ServletContext servletContext=getServletContext();
        String path = servletContext.getRealPath("upload");

        String ka=(String)servletContext.getAttribute( "ka");
        System.out.println("ka = " + ka);
//        servletContext.setAttribute("ka","vaa");
//
//        String ka=(String)servletContext.getAttribute( "ka");


    }
}
