package com.atguigu.interceptors;

import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginProtectedinterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中获取token
        String token=request.getHeader("token");//检查是否有效
        boolean expiration=jwtHelper.isExpiration(token);
        //有效放行
        if(!expiration) {
            //旅行，没有过期
            return true;
        }
        //无效返回504的状态json
        Result result=Result.build(null, ResultCodeEnum.NOTLOGIN);
        ObjectMapper objectMapper =new ObjectMapper();
        String json=objectMapper.writeValueAsString(result);
        response.getWriter().print(json);
        return false;
    }

}
