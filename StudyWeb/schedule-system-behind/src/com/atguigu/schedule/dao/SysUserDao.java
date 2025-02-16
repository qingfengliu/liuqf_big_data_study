package com.atguigu.schedule.dao;

import com.atguigu.schedule.pojo.SysUser;

public interface SysUserDao {

    /**
     * 添加用户
     * @param sysUser
     * @return
     */
    int addSysUser(SysUser sysUser);

    /**
     * 根据用户名查询用户
     * @param username
     * @return SysUser
     */

    SysUser findUserByUsername(String username);
}
