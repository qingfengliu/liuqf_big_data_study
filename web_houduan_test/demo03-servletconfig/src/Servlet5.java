import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Arrays;

@WebServlet(urlPatterns ="/servlet5")
public class Servlet5 extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException {
        String username = req.getParameter("username");
        System.out.println(username);

        String userPwd = req.getParameter("userPwd");
        System.out.println(userPwd);

        // 根据参数名获取多个参数值

        String[] hobbies = req.getParameterValues("hobby");
        System.out.println(Arrays.toString(hobbies));

        // 获取所有的参数名
        Enumeration<String> pnames = req.getParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = pnames.nextElement();
            String[] pvalue = req.getParameterValues(pname);
            if (pvalue.length > 1) {
                System.out.println(pname + "=" + Arrays.toString(pvalue));
            } else {
                System.out.println(pname + "_" + pvalue[0]);
            }
        }
    }
}
