package org.liuqf.make_random_data;
import org.liuqf.make_random_data.pojo.Address_Data;
import org.liuqf.make_random_data.pojo.Person_Data;
import org.liuqf.make_random_data.pojo.Restaurant_Data;
import org.liuqf.make_random_data.pojo.Sale_Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;

public class make_random_data {
//    static String Url = "jdbc:mysql://localhost/random_data";//参数参考MySql连接数据库常用参数及代码示例
    static String Url = "jdbc:mysql://192.168.212.133/random_data";
    static String name = "root";//数据库用户名
    static String psd = "111111";//数据库密码

    public static void make_sale_data() throws SQLException{

//        String jdbcName = "com.mysql.jdbc.Driver";//连接MySql数据库
        String sql = "insert into random_data.sale_data values(?,?,?,?,?,?,?,?)";//数据库操作语句（插入）

        //生成100000条数据
        Connection conn = DriverManager.getConnection(make_random_data.Url, make_random_data.name, make_random_data.psd);
        for (int i = 0; i < 100000; i++) {
            Sale_Data sale_data = new Sale_Data();
            System.out.println("-------------------第" + i + "条数据----------------------------------");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, sale_data.get_name());

            pst.setString(2, sale_data.get_address());
            pst.setString(3, sale_data.get_restaurant());
            pst.setString(4, sale_data.get_food());
            pst.setInt(5, sale_data.get_count());
            pst.setDouble(6, sale_data.get_price());
            pst.setDouble(7, sale_data.get_gmv());
            //6*60*24*i+随机数0-6*60*24-1 并转换为bigint
            BigInteger time = BigInteger.valueOf(6 * 60 * 24 * i + (int) (Math.random() * 10000));
//            System.out.println(time);
            pst.setBigDecimal(8, new BigDecimal(time));
//            System.out.println(pst.toString());
            pst.executeUpdate();
            pst.close();
        }
        conn.close();

    }

    public static void make_person_data() throws SQLException {
        //根据  random_data.sale_data中name列不同的值生产person数据

//        String jdbcName = "com.mysql.jdbc.Driver";//连接MySql数据库
        //查询sale_data表中name列不同的值
        String sql = "select distinct name from random_data.sale_data";
        //连接数据库
        Connection connection = DriverManager.getConnection(make_random_data.Url, make_random_data.name, make_random_data.psd);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();


        //生成person数据
        while (resultSet.next()) {
            Person_Data person_data = new Person_Data();
            System.out.println("-------------------" + resultSet.getString("name") + "----------------------------------");
            //mysql建表语句
            String sql1 = "insert into random_data.person_data values(?,?,?,?,?,?)";
            //插入数据
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.setString(1, resultSet.getString("name"));
            preparedStatement1.setString(2, person_data.get_address());
            preparedStatement1.setString(3, person_data.get_phone());
            preparedStatement1.setString(4, person_data.get_email());
            preparedStatement1.setString(5, person_data.get_company());
            preparedStatement1.setString(6, person_data.get_job());
            preparedStatement1.executeUpdate();
            preparedStatement1.close();

        }
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }

    public static void restaurant_data() throws SQLException {
//        String jdbcName = "com.mysql.jdbc.Driver";//连接MySql数据库
        String sql = "insert into random_data.restaurant_data values(?,?,?,?)";//数据库操作语句（插入）

        //查询sale_data表中restaurant列不同的值
        String sql1 = "select distinct restaurant from random_data.sale_data";
        //连接数据库
        Connection conn = DriverManager.getConnection(make_random_data.Url, make_random_data.name, make_random_data.psd);
        PreparedStatement preparedStatement = conn.prepareStatement(sql1);
        ResultSet resultSet = preparedStatement.executeQuery();
        //生成restaurant数据
        while (resultSet.next()) {
            Restaurant_Data restaurant_data = new Restaurant_Data();
            System.out.println("-------------------" + resultSet.getString("restaurant") + "----------------------------------");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, resultSet.getString("restaurant"));
            pst.setString(2, restaurant_data.get_company());
            pst.setString(3, restaurant_data.get_address());
            pst.setString(4, restaurant_data.get_color());
            pst.executeUpdate();
            pst.close();
        }
        resultSet.close();
        preparedStatement.close();
        conn.close();
    }

    public static void address_data() throws SQLException{
        String sql = "insert into random_data.address_data values(?,?,?,?,?,?)";//数据库操作语句（插入）

        //address_data建表语句

        String sql1 = "select distinct address from random_data.sale_data";
        Connection conn = DriverManager.getConnection(make_random_data.Url, make_random_data.name, make_random_data.psd);
        //查询sale_data表中address列不同的值
        PreparedStatement preparedStatement = conn.prepareStatement(sql1);
        ResultSet resultSet = preparedStatement.executeQuery();
        //生成address数据
        while (resultSet.next()) {
            Address_Data address_data = new Address_Data();
            System.out.println("-------------------" + resultSet.getString("address") + "----------------------------------");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, resultSet.getString("address"));
            pst.setString(2, address_data.get_city());
            pst.setString(3, address_data.get_country());
            pst.setString(4, address_data.get_state());
            pst.setString(5, address_data.get_zip());
            pst.setDouble(6, address_data.get_distance());
            pst.executeUpdate();
            pst.close();
        }
        resultSet.close();
        preparedStatement.close();
        conn.close();
    }

    public static void main(String[] args) {
        try {
            make_sale_data();
            make_person_data();
            restaurant_data();
            address_data();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
