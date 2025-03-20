package org.liuqf.make_random_data_product;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.liuqf.make_random_data_product.Dao.SaleDataDao;
import org.liuqf.make_random_data_product.pojo.SaleData;

public class test {
    public static void main(String[] args) throws IOException {
        // 读取配置文件mybatis-config.xml
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
        // 根据配置文件构建SqlSessionFactory
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(config);
        // 通过SqlSessionFactory创建SqlSession
        SqlSession ss = ssf.openSession();
        // SqlSession执行文件中定义的SQL，并返回映射结果
        SaleDataDao userDao = ss.getMapper(SaleDataDao.class);

        // 查询所有网站
        List<SaleData> sale = userDao.getAll();


        // 关闭 SqlSession
        ss.close();
    }
}
