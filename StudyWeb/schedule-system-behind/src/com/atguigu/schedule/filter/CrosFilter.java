package com.atguigu.schedule.filter;

import com.atguigu.schedule.common.Result;
import com.atguigu.schedule.util.WebUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter("/*")
public class CrosFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIO0S,DELETE, HEAD");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, X-Requested-With");
        // 非预检请求,放行即可,预检请求,则到此结束,不需要放行
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
//视频代码和文档代码不一样放着备用
//        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
//            WebUtil.writeJson(response, Result.ok(null));
//        } else {
//            //非预检请求,放行即可
//            filterChain.doFilter(servletRequest,servletResponse);
//        }
    }
}