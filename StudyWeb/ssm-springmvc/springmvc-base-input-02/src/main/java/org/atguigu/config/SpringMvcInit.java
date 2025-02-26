package org.atguigu.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import java.lang.String;

@Configuration
@ComponentScan("org.atguigu.param")
public class SpringMvcInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    //配置类
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{MvcConfig.class};
    }

    //设置DispatherServlet对应处理的地址
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}
