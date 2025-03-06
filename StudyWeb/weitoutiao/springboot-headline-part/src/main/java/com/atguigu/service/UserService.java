package com.atguigu.service;

import com.atguigu.pojo.User;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 50477
* @description 针对表【news_user】的数据库操作Service
* @createDate 2025-03-05 20:11:55
*/
public interface UserService extends IService<User> {

    Result login(User user);

    Result getUserInfo(String token);

    Result checkUserName(String username);

    Result register(User user);
}
