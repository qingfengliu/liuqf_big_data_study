package org.liuqf.make_random_data_product;


import org.liuqf.make_random_data_product.pojo.PersonData;
import org.liuqf.make_random_data_product.pojo.SaleData;

import java.sql.SQLException;
import java.util.List;

public class DataFactory  {


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

    public List<SaleData> get_saledata(int cusor) throws Exception {
        //
        return getData.get_saledata(cusor);

    }

    public List<PersonData> get_persondata(List<SaleData> sales) throws Exception {
        return getData.get_persondata(sales);
    }

     public List<List> run_data(int cusor) throws Exception {
        List<SaleData> data_sale=this.get_saledata(cusor);
        List<PersonData> data_person=this.get_persondata(data_sale);
        //将data_person和data_sale打包成一个list
        List<List> data=new java.util.ArrayList<>();
        data.add(data_sale);
        data.add(data_person);
        Thread.sleep(this.sleep_time);
        return data;
    }

    public long getSleep_time() {
        return sleep_time;
    }

}
