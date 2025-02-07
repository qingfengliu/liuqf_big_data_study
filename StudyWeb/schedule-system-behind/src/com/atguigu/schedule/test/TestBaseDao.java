package com.atguigu.schedule.test;

import com.atguigu.schedule.dao.BaseDao;
import com.atguigu.schedule.pojo.SysUser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class TestBaseDao {

    private static BaseDao baseDao;

    @BeforeClass
    public static void initBaseDao(){
        baseDao=new BaseDao();
    }

    @Test
    public void testBaseQueryobject(){
        String sql = "select count(1) from sys_user";
        Long count = baseDao.baseQueryObject(Long.class, sql);
        System.out.println(count);
    }

    @Test
    public void testBaseQuery(){
        String sql="select uid,username,user_pwd userPwd from sys_user"; //sql输出的字段名必须和实体类的属性名一致
        List<SysUser> sysUserList = baseDao.baseQuery(SysUser.class, sql);
        sysUserList.forEach(System.out::println);
    }
}
