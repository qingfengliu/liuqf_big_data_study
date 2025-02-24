package org.atguigu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @RequestMapping("springmvc/hello")//对外访问的地址: 到handlerMapping注册的注解
    @ResponseBody   //返回的数据直接写给浏览器,不走视图解析器
    public String hello() {
        System.out.println("HelloController.hello");
        //返回给前端
        return "hello springmvc!!";
    }
}