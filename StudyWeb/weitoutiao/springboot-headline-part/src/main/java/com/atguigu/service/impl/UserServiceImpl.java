package com.atguigu.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.MD5Util;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import com.atguigu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author 50477
* @description 针对表【news_user】的数据库操作Service实现
* @createDate 2025-03-05 20:11:55
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtHelper jwtHelper;

    /*
    * @description 用户登录
    * @param user 用户信息
    * @return com.atguigu.utils.Result
    * TODO :
    *   1.根据账号，查询用户对象
    *   2.如果用户对象为null，查询失败，账号错误! 501
    *   3.对比，密码，失败返回503的错误
    *   4.根据用户id生成一个token，token->result 返回
    * */
    public Result login(User user) {
        LambdaQueryWrapper<User>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,user.getUsername());
        User loginUser =userMapper.selectOne(lambdaQueryWrapper);

        if(loginUser == null){
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }
        //对比密码
        if(!StringUtils.isEmpty(user.getUserPwd())
            && MD5Util.encrypt(user.getUserPwd()).equals(loginUser.getUserPwd())){
            //登录成功  ,根据用户id生成 token
            // 将token封装到result返回

            String token = jwtHelper.createToken(Long.valueOf(loginUser.getUid()));

            Map data= new HashMap();
            data.put("token",token);
            return Result.ok(data);
        }
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);

    }

    /*
    * 根据token获取用户数据
    * */
    public Result getUserInfo(String token) {
        //1.校验token是否有效
        //2,根据topen解析用户id
        //3.根据用户id查询用户数据
        //4.去掉密码，封装result结果返回即可
        //打印token
        boolean expiration=jwtHelper.isExpiration(token);
        if(expiration) {
            //失效，未登录看待
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }


        int userId = jwtHelper.getUserId(token).intValue();

        User user =userMapper.selectById(userId);
        user.setUserPwd("");
        Map data= new HashMap();
        data.put("loginUser",user);

        return Result.ok(data);
    }

    /**/
    public Result checkUserName(String username) {
        LambdaQueryWrapper<User>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,username);
        User user =userMapper.selectOne(lambdaQueryWrapper);
        if(user == null){
            //用户名不存在，可以使用
            return Result.ok(null);
        }

        return Result.build(null, ResultCodeEnum.USERNAME_USED);
    }

    /*
    * TODO :
    *   1.校验用户名是否存在
    *   2.对密码进行加密
    *   3.插入数据库
    *   4.返回成功
    * */
    public Result register(User user){
        LambdaQueryWrapper<User> querylrapper= new LambdaQueryWrapper<>();
        querylrapper.eq(User::getUsername,user.getUsername());
        Long count=userMapper.selectCount(querylrapper);
        if (count>0){
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        }
        user.setUserPwd(MD5Util.encrypt(user.getUserPwd()));
        userMapper.insert(user);
        return Result.ok(null);
    }
}




