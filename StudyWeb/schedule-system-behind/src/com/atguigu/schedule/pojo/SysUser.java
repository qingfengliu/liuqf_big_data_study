package com.atguigu.schedule.pojo;

/*
    1 实体类的类名和表格名称应该对应(对应不是一致)
    2 实体类的属性名和表格的列名应该对应
    3 每个属性都必须是私有的
    4 每个属性都应该具备 getter setter
    5 必须具备无参构造器
    6 应该实现序列化接口
    7 应该重新类的hashcode和equals方法
    8 toString是否重写都可以

引入lombok

    1 检查idea是否已经交装lombok插件
    2 检查是否勾选了enable annotation processer
    3 导入lombok的jar包

* */


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@AllArgsConstructor //全参构造
@NoArgsConstructor //无参构造
@Data //get set hashcode equals toString
public class SysUser implements Serializable {
    private Integer uid;
    private String username;
    private String userPwd;

    //添加getter setter方法
    //添加无参构造



    @Override
    public String toString() {
        return "SysUser{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", userPwd='" + userPwd + '\'' +
                '}';
    }

}
