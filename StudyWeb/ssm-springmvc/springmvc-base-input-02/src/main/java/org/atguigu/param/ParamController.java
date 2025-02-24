package org.atguigu.param;

import org.atguigu.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("param")
public class ParamController {

    // 形参列表,填写对应名称的参数即可!请求参数名=形参参数名即可!
    @RequestMapping("data")
    @ResponseBody
    //没有自动绑定nane和age。需要手动绑定

    public String data(String name, int age) {
        if (name == null || age == 0) {
            return "name =" + name + ",age =" + age;
        }
        System.out.println("name=" + name + ",age =" + age);
        return "name =" + name + ",age =" + age;
    }

    //注解指定
    //指定任意的请求参数名要求必须传递要求不必须传递给与一个默认值
    // /param/data1?account=root&page=1
    //account必须传递page可以不必须传递，如果不传递默认值就是1
    @GetMapping("data1")
    @ResponseBody
    public String data1(@RequestParam(value = "account") String usermame
            , @RequestParam(required = false, defaultValue = "1") int page) {
        System.out.println("username=" + usermame + ",page =" + page);
        return "username =" + usermame + ",page =" + page;
    }

    //特殊值
    // 一名多值 key=1&key=2 直接使用集合接值即可
    //param/data2?hbs=吃&hbs=完&hbs=学习
    //集合必须加@RequestParam注解
    @GetMapping("data2")
    @ResponseBody
    public String data2(@RequestParam List<String> hbs) {
        System.out.println("hbs =" + hbs);
        return "ok";
    }

    @GetMapping("data3")
    @ResponseBody
    //使用实体对象接值
    public String data3(User user) {
        System.out.println("user =" + user);
        return user.toString();
    }
}
