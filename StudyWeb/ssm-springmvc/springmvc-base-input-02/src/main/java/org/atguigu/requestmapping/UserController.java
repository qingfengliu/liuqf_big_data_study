package org.atguigu.requestmapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    /*
    * 1.精准地址[一个|多个] /user/login {"地址1”,“地址2”}

    2.支持模糊 *|** 一层字符串任意层|任意字符串

    3.类上和方法上添加@RequestMapping的区别
        类上提取通用的访问地址
        方法上是具体的handler地址
        访问:类地址+方法地址即可

    4.请求方式指定
        客户端->http(get|post|put|delete)->ds ->handler
        默认情况: @RequestMapping("login") 主要地址正确,任何请求方式都可以访问
        指定请求方式:method={RequestMethod.GET,RequestMethod.POST}
        不符合请求方式:会出现405异常!
        *
    5.注解进阶
        get @GetMapping == @RequestMapping(xxx,method=GET);
        post @PostMapping == @RequestMapping(xxx,method=POST);
        put @PUTMapping == @RequestMapping(xxx,method=PUT);
        delete @DeleteMapping == @RequestMapping(xxx,method=DELETE);
    * */

    @RequestMapping
    @GetMapping
    public String index(){
        return null;
    }


    @RequestMapping(value = "/user/login",method = RequestMethod.POST)//作用注册地址: 将handler注册到handlerMapping
    public String login() {
        return null;
    }


}
