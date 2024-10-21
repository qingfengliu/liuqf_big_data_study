package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Class.forName;

//将维表数据写入到状态中，然后在每个task中进行join
public class StaticJoinDemo extends RichMapFunction<Tuple2<String, String>, String> {
    static String Url = "jdbc:mysql://192.168.0.6/random_data";
    static String name = "root";//数据库用户名
    static String psd = "111111";//数据库密码

    Map<String, Student> dim;

    @Override
    public String map(Tuple2<String, String> value) {
        //输入map的第一个值是个key,第二个值是个json字符串
        String key = value.f0;
        //通过key获取dim中的数据
        Student student = dim.get(key);
        //将student转换成json
        JSONObject jsonObject1 = new JSONObject(value.f1);
        JSONObject jsonObject2 = new JSONObject(student);
        Iterator<String> keys = jsonObject2.keys();
        while (keys.hasNext()) {
            String key_1 = keys.next();
            //字符串
            if (jsonObject1.has(key_1)&& !key_1.equals("name")) {
                jsonObject1.put(key_1 + "_A", jsonObject1.get(key_1));
                jsonObject1.put(key_1 + "_B", jsonObject2.get(key_1));
                //删除key
                jsonObject1.remove(key_1);
            } else {
                jsonObject1.put(key_1, jsonObject2.get(key_1));
            }
        }
        return jsonObject1.toString();
    }

    //open
    @Override
    public void open(Configuration parameters) throws Exception {
        dim=new HashMap<>();
        //查询sale_data表中restaurant列不同的值
        String sql1 = "SELECT name,address,phone,email,company,job from random_data.person_data";
        //连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(this.Url, this.name, this.psd);
        PreparedStatement preparedStatement = conn.prepareStatement(sql1);
        ResultSet resultSet = preparedStatement.executeQuery();
        //生成restaurant数据
        while (resultSet.next()) {
            dim.put(resultSet.getString("name"), new Student(resultSet.getString("name")
                    , resultSet.getString("address"), resultSet.getString("phone")
                    , resultSet.getString("email"), resultSet.getString("company")
                    , resultSet.getString("job"))
            );
        }
        resultSet.close();
        preparedStatement.close();
        conn.close();
    }



    @Data  // 注解在类上，为类提供读写属性，还提供equals()、hashCode()、toString()方法
    @AllArgsConstructor  // 注解在类上，为类提供全参构造函数，参数的顺序与属性定义的顺序一致
    @NoArgsConstructor  // 注解在类上，为类提供无参构造函数
    public static class Student {
        private String name;
        private String address;
        private String phone;
        private String email;
        private String company;
        private String job;
    }

}
