import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/servletB")
public class ServletB extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("ServletB");
    }
}
