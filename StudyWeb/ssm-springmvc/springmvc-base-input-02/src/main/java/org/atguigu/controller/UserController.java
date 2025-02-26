package org.atguigu.controller;

import org.atguigu.pojo.User2;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @PostMapping("register")
    //使用校验注解
    //捕捉错误绑定错误信息:
    //1.handler(校验对象,BindingResultresult)   要求:bindingResult必须紧挨着 校验对象
    public Object register(@Validated @RequestBody User2 user, BindingResult result){
        if(result.hasErrors()){
            //有绑定错误，就不直接返回了，由我们自己决定!
            Map data= new HashMap();
            data.put("code",400);
            data.put("msg","参数校验异常了!");
            return data;
        }
        System.out.println("user ="+ user);
        return user;
    }
}
