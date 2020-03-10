package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.interceptors.DataSourceNameInterceptor;
import com.viewshine.exportexcel.interceptors.DownloadInterceptor;
import com.viewshine.exportexcel.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Objects;

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;

/**
 * 用于配置路由拦截器、设置虚拟目录。必须实现WebMvcConfigurer
 * @author ChangWei[changwei@viewshine.cn]
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private DataSourceNameInterceptor dataSourceNameInterceptor;

    @Autowired
    private DownloadInterceptor downloadInterceptor;

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
     * 设置路由拦截器
     *      1.设置数据库名称与数据库类型的拦截器
     *      2.设置下载文件拦截器，从而进入虚拟目录下载文件。（拦截的地址为：/downloadExcel/**）
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dataSourceNameInterceptor).addPathPatterns("");
        registry.addInterceptor(downloadInterceptor).addPathPatterns(DOWNLOAD_FILE_INTERCEPTOR + "/**");
    }

    /**
     * 用于设置虚拟目录，从而下载文件。
     *      addResourceLocations：必须与[file:]开头，例如file:/User/changxiao/，注意最后一定有一个【/或者\】
     * @param registry 资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        StringBuilder downloadFilePath = new StringBuilder("file:");
        downloadFilePath.append(CommonUtils.formatPathOnSystem(docFilePath));
        char separatorChar = File.separatorChar;
        if (! Objects.equals(separatorChar, downloadFilePath.charAt(downloadFilePath.length() - 1))) {
            downloadFilePath.append(separatorChar);
        }
        registry.addResourceHandler(DOWNLOAD_FILE_URL + "/**")
                .addResourceLocations(downloadFilePath.toString());
    }
}
