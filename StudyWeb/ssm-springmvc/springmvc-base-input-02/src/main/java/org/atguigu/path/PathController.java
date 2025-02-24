package org.atguigu.path;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("path")
@ResponseBody
public class PathController {
    // path/账号/密码
    //动态路径设计fkey}=*{key}在形参列表获取传入的参数
    @RequestMapping("{account}/{password}")
    public String login(@PathVariable("account") String account, @PathVariable String password) {
        return "account=" + account + ",password=" + password;
    }


}
