package org.liuqf.make_random_data_product;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.liuqf.make_random_data_product.Dao.PersonDataDao;
import org.liuqf.make_random_data_product.Dao.SaleDataDao;
import org.liuqf.make_random_data_product.pojo.PersonData;
import org.liuqf.make_random_data_product.pojo.SaleData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GetData {

    public List<SaleData> get_saledata(int cusor) throws Exception {
        // 读取配置文件mybatis-config.xml
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
        // 根据配置文件构建SqlSessionFactory
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(config);
        // 通过SqlSessionFactory创建SqlSession
        SqlSession ss = ssf.openSession();
        // SqlSession执行文件中定义的SQL，并返回映射结果
        SaleDataDao userDao = ss.getMapper(SaleDataDao.class);
        // 查询所有网站
        List<SaleData> sale = userDao.getPage(cusor,10);
        // 关闭 SqlSession
        ss.close();
        return sale;
    }

    public List<PersonData> get_persondata(List<SaleData> sales) throws Exception {
        // 读取配置文件mybatis-config.xml
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
        // 根据配置文件构建SqlSessionFactory
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(config);
        // 通过SqlSessionFactory创建SqlSession
        SqlSession ss = ssf.openSession();
        // SqlSession执行文件中定义的SQL，并返回映射结果
        PersonDataDao userDao = ss.getMapper(PersonDataDao.class);
        // 查询所有网站
        List<PersonData> persons = new ArrayList<>();
        for (SaleData sale : sales) {
            PersonData person = userDao.getDataBySaleInfo(sale);
            //将person的tm加上正太分布的随机数，方差为1
            Random random = new Random();
            long num = (long)(Math.sqrt(1000) * random.nextGaussian() + 0);
            person.setTm((long) (person.getTm() + num));
            persons.add(person);
        }
        // 关闭 SqlSession
        ss.close();
        return persons;
    }


}
