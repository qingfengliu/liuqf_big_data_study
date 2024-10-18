package org.liuqf.make_random_data_product;

import com.alibaba.fastjson2.JSONObject;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class GetData {
    String Url = "jdbc:mysql://192.168.0.6/random_data";//参数参考MySql连接数据库常用参数及代码示例
    String name = "root";//数据库用户名
    String psd = "111111";//数据库密码


    Connection conn = null;


    public GetData() throws SQLException {
        this.conn = DriverManager.getConnection(Url, name, psd);
    }

    public List<List<String>> get_data(int cusor) throws Exception {
        //定义二维数组
        List<List<String>> data = new ArrayList<>();
        String sql_get_sales = "SELECT JSON_OBJECT('name',name,'address',address ,'restaurant',restaurant,'food',food," +
                "'count',count,'price',price,'gmv',gmv) from random_data.sale_data limit "+cusor+",10;";
        String sql_get_person = "SELECT JSON_OBJECT('name',name,'address',address,'phone',phone,'email',email,'company',company,'job',job) from random_data.person_data where name = ?";
        //获得销售数据
        PreparedStatement preparedStatement = conn.prepareStatement(sql_get_sales);
        ResultSet resultSet = preparedStatement.executeQuery();

        //将结果转换为json格式
        while (resultSet.next()) {
            //解析json
            List<String> tmp = new ArrayList<>();

            String sale_data = resultSet.getString(1);
            JSONObject jsonObject_sd = JSONObject.parseObject(sale_data);

            //通过销售数据的name字段查找人员数据
            PreparedStatement preparedStatement1 = conn.prepareStatement(sql_get_person);
            preparedStatement1.setString(1, jsonObject_sd.get("name").toString());
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            while (resultSet1.next()) {
                String json_person = resultSet1.getString(1);
                tmp.add(resultSet.getString(1));
                tmp.add(json_person);
                data.add(tmp);
            }
            resultSet1.close();
            preparedStatement1.close();
        }
        resultSet.close();
        preparedStatement.close();
        return data;
    }

    //析构函数
    public void close() throws SQLException {
        conn.close();
    }
}
