package com.atguigu.service.impl;

import com.atguigu.mapper.EmployeeMapper;
import com.atguigu.pojo.Employee;
import com.atguigu.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeserviceImpl implements EmployeeService {

    @Autowired
    //无法找到符号

    private EmployeeMapper employeeMapper;

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = employeeMapper.getAll();

        return employees;
    }

}
