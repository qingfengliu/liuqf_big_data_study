package com.atguigu.controller;

import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import com.atguigu.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        //具体返回的数据由业务层做
        Result result=userService.login(user);
        return result;
    }


    @GetMapping("/getuserinfo")
    public Result getUserInfo(@RequestHeader String token){
        Result result=userService.getUserInfo(token);
        return result;
    }
}
