package com.atguigu.schedule.service;

import com.atguigu.schedule.pojo.SysUser;

/*
* 该接口定义了以sys_user表格为核心的业务处理功能
* */
public interface SysUserService {

    /*
    * 注册用户的方法
    * @param sysUser 用于封装了用户注册信息
    * @return 返回注册成功1,注册失败0
    * */
    int regist(SysUser sysUser);

    /*
        * 根据用户名获取完整信息
     * @param username 用户名
     * @return SysUser 用户完整信息
     * */
    SysUser findByUserName(String username);
}
