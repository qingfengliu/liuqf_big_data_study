package com.atguigu.mapper;
import com.atguigu.pojo.Employee;

import java.util.List;


public interface EmployeeMapper {
    // 查询所有员工
    List<Employee> getAll();

}
