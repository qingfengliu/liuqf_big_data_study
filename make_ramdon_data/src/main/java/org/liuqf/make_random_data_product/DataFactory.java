package org.liuqf.make_random_data_product;


import java.sql.SQLException;
import java.util.List;

public class DataFactory  {
    String Url = "jdbc:mysql://192.168.0.6/random_data";//参数参考MySql连接数据库常用参数及代码示例
    String name = "root";//数据库用户名
    String psd = "111111";//数据库密码
    String sql = "SELECT JSON_OBJECT('name',name,'address',address ,'restaurant',restaurant,'food',food," +
            "'count',count,'price',price,'gmv',gmv) from random_data.sale_data limit "+1+",10;";


    //每次运行间隔时间
    int sleep_time = 1000 * 60; // 1分钟

    GetData getData = null;
    public DataFactory() throws Exception {
        getData=new GetData();
    }

    public DataFactory(int sleep_time) throws Exception {
        this();
        this.sleep_time = sleep_time;
    }

    public List<List<String>> get_data(int cusor) throws Exception {
        //
        return getData.get_data(cusor);

    }

     public List<List<String>> run_data(int cusor) throws Exception {
        List<List<String>> data=this.get_data(cusor);
        Thread.sleep(this.sleep_time);
        return data;
    }

    public long getSleep_time() {
        return sleep_time;
    }

    public void close() {
        try {
            getData.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
