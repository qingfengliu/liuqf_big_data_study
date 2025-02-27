package com.atguigu.config;


import com.alibaba.druid.support.logging.SLF4JImpl;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Properties;

/*
    配置mybatis

*/
@Configuration
@PropertySource("classpath:jdbc.properties")
public class MapperJavaConfigNew {


    // 配置SqlSessionFactoryBean
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource){
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        // 配置mybatis全局配置文件
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true); // 开启驼峰命名法
        configuration.setLogImpl(Slf4jImpl.class); // 设置日志输出
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL); // 开启自动映射
        sqlSessionFactoryBean.setConfiguration(configuration); // 设置全局配置文件
        sqlSessionFactoryBean.setTypeAliasesPackage("com.atguigu.pojo"); // 设置别名包

        PageInterceptor pageInterceptor = new PageInterceptor(); // 创建分页插件
        Properties properties = new Properties();
        properties.setProperty("helperDialect","mysql"); // 设置数据库类型
        pageInterceptor.setProperties(properties); // 设置分页插件属性
        sqlSessionFactoryBean.addPlugins(pageInterceptor); // 添加分页插件
        return sqlSessionFactoryBean;
    }

    // 配置MapperScannerConfigurer
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.atguigu.mapper");
        return mapperScannerConfigurer;
    }
}
