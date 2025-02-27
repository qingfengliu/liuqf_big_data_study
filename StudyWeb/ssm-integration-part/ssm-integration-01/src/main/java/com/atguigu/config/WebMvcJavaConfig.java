package com.atguigu.config;

/*
* 控制层配置类
    1.controller
    2.全局异常处理器
    3. handlerMapping handlerAdapter
    4.静态资源处理
    5.jsp 视图解析器前后缀6.json转化器7.拦截器...
*
* */

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc   // 开启SpringMVC,配置handlerMapping和handlerAdapter
@Configuration
@ComponentScan("com.atguigu.controller")
public class WebMvcJavaConfig implements WebMvcConfigurer {

    // 配置静态资源处理
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
        configurer.enable();
    }

    // 配置视图解析器
    public void configureViewResolvers(ViewResolverRegistry registry){
        registry.jsp("/WEB-INF/views/",".jsp");
    }
    // 配置拦截器
    //@Override
    //public void addInterceptors(InterceptorRegistry registry)

//    // 配置json转化器
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
//        converters.add(new MappingJackson2HttpMessageConverter());
//    }

}
