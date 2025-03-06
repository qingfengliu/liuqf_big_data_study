package com.atguigu.service.impl;

import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Type;
import com.atguigu.service.TypeService;
import com.atguigu.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 50477
* @description 针对表【news_type】的数据库操作Service实现
* @createDate 2025-03-05 20:11:55
*/
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>
    implements TypeService{

    @Autowired
    private TypeMapper typeMapper ;
    @Override
    public Result findAllTypes(){

        List<Type> list = typeMapper.selectList(null);
        return Result.ok(list);
    }
}




