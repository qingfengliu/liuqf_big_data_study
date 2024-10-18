package org.liuqf.test_faker_data01;


import com.alibaba.fastjson2.JSONObject;

import java.sql.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import static net.datafaker.transformations.Field.field;

public class Test {
    public static void main(String[] args) throws SQLException {
        String Url = "jdbc:mysql://localhost/random_data";//参数参考MySql连接数据库常用参数及代码示例
        String name = "root";//数据库用户名
        String psd = "111111";//数据库密码
        String sql = "SELECT JSON_OBJECT('name',name,'address',address ,'restaurant',restaurant,'food',food," +
                "'count',count,'price',price,'gmv',gmv) from random_data.sale_data limit "+1+",10;";

        Connection conn = DriverManager.getConnection(Url, name, psd);
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();


        //将结果转换为json格式
        while (resultSet.next()) {
            //解析json
            String json_sale = resultSet.getString(1);
            JSONObject jsonObject_sd = JSONObject.parseObject(json_sale);
            //查勋msql表random_data.person_data限制条件为name
            String sql1 = "SELECT JSON_OBJECT('name',name,'address',address,'phone',phone,'email',email,'company',company,'job',job) from random_data.person_data where name = ?";
            PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
            preparedStatement1.setString(1, jsonObject_sd.get("name").toString());
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            while (resultSet1.next()) {
                String json_person = resultSet1.getString(1);
                System.out.println(json_person);
            }
            resultSet1.close();
            preparedStatement1.close();
        }
        resultSet.close();
        preparedStatement.close();
        conn.close();
    }

}

