package com.atguigu.config;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringloCInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    //rootioc容器的配置类指定
    @Override
    protected Class<?>[] getRootConfigClasses(){
        return new Class[]{DataSourceJavaConfig.class, MapperJavaConfigNew.class, ServiceJavaConfig.class};
    }

    //webioc容器的配置类指定
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebMvcJavaConfig.class};
    }

    //DispatcherServlet拦截请求的映射
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}