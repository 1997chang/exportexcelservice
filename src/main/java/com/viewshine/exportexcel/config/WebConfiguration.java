package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Objects;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DOWNLOAD_FILE_URL_HANDLE;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private DataSourceNameInterceptor dataSourceNameInterceptor;

    /**
     * 表示文件保存位置
     */
    @Value("${export.excel.docFilePath}")
    private String docFilePath;

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
        registry.addInterceptor(dataSourceNameInterceptor).addPathPatterns("/**")
                .excludePathPatterns(DOWNLOAD_FILE_URL_HANDLE);
    }

    /**
     * 用于设置虚拟目录，从而下载文件
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        StringBuilder downloadFilePath = new StringBuilder("file:");
        downloadFilePath.append(CommonUtils.formatFileOnSystem(docFilePath));
        char separatorChar = File.separatorChar;
        if (! Objects.equals(separatorChar, downloadFilePath.charAt(downloadFilePath.length() - 1))) {
            downloadFilePath.append(separatorChar);
        }
        registry.addResourceHandler(DOWNLOAD_FILE_URL_HANDLE)
                .addResourceLocations(downloadFilePath.toString());
    }
}
