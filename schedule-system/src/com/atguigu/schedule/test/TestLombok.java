package com.atguigu.schedule.test;


import com.atguigu.schedule.pojo.SysUser;
import org.junit.Test;

public class TestLombok {

    @Test//没用这玩意
    public void test() {
        SysUser user=new SysUser(1, "", "");
        System.out.println(user);
    }
}
