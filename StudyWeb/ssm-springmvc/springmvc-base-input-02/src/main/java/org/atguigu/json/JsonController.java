package org.atguigu.json;

import org.atguigu.pojo.Person;
import org.atguigu.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("json")
@ResponseBody
//@RestController=@Controller+@ResponseBody
public class JsonController {
    //前端-》json->415 不支持数据类型呢??
    /* 原因:
        Java原生的api,只支持路径参数和param参数 request.getParameter("key");param
                不支持json
        json是前端的格式
    */
    //解决:1、导入ison处理的依赖 2.handlerAdapter配置json转化器

    //TOD0: @ResponseBody
    //数据直接放入响应体返回!也不会在走视图解析器快速查找视图，转发和重定向都不生效了!
    @PostMapping("data")
    public String data(@RequestBody Person person){
        System.out.println("person="+ person);
        return person.toString();
    }

    @GetMapping("data2")
    @ResponseBody
    public User data(){
        User user = new User();
        user.setUsername("two dogs!");
        user.setAge(18);
        return user;
    }

    @GetMapping("data3")
    @ResponseBody
    public List<User> data1(){
        User user = new User();
        user.setUsername("two dogs!");
        user.setAge(3);

        List<User>users =new ArrayList<>();
        users.add(user);
        return users;
    }

}
