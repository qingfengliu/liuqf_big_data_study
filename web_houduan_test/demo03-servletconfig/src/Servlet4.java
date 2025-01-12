import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/servlet4")
public class Servlet4 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        //行相关
        System.out.println(req.getMethod()); //获取请求方式
        System.out.println(req.getScheme()); //获取协议
        System.out.println(req.getProtocol()); //获取协议版本
        System.out.println(req.getRequestURI());    //获取请求的资源路径
        System.out.println(req.getRequestURL());    //获取请求的统一资源定位符

        System.out.println(req.getLocalPort());     //本应用容器的端口
        System.out.println(req.getServerPort());    //客户端请求的端口
        System.out.println(req.getRemotePort());    //客户端软件端口


        //头相关 key:value key:value
        // 根据名字单独获取某个请求头
        String accept=req.getHeader("Accept");

        System.out.println("Accept:"+accept);




        //头相关
    }

}
