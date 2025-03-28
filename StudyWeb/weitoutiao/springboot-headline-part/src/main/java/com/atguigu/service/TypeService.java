package com.atguigu.service;

import com.atguigu.pojo.Type;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 50477
* @description 针对表【news_type】的数据库操作Service
* @createDate 2025-03-05 20:11:55
*/
public interface TypeService extends IService<Type> {

    Result findAllTypes();
}
