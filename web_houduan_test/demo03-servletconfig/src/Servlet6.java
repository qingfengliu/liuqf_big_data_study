import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class Servlet6 extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(2000);
        //设置相应头
        String info = "<h1>hello</h1>";
        resp.setHeader("aaa","valueg");
        resp.setHeader("Content-Type","text/html");
        resp.setContentLength(info.getBytes().length);

        //设置响应体内容API
        PrintWriter writer =resp.getWriter();
        writer.write(info);

        // 获得一个向响应体中输入二进制信息的字节输出流

       // ServletOutputStream outputStream=resp.getOutputStream();




    }
}
