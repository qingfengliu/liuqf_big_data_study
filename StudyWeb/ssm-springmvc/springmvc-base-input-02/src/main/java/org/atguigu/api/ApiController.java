package org.atguigu.api;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 如果想要获取请求或者响应对象,或者会话等,可以直接在形参列表传入,并且不分先后顺序!
 * 注意: 接收原生对象,并不影响参数接收!
 */
@Controller
public class ApiController{

    @Autowired
    public void data(HttpSession session, HttpServletRequest request,
                     HttpServletResponse response) {
        //可以使用原生的api
        ServletContext servletContext=request.getServletContext();
        ServletContext servletContext1=session.getServletContext();
    }
}
