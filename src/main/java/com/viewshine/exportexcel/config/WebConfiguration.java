package com.viewshine.exportexcel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private DataSourceNameInterceptor dataSourceNameInterceptor;

/*    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ExcelColumnConverter());
    }*/

    /**
     * 用于设置数据库名称拦截器，作用就是选择数据源
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dataSourceNameInterceptor).addPathPatterns("/**");
    }
}
