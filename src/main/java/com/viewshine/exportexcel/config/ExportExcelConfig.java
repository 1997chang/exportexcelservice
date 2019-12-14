package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.properties.ExportExcelProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Configuration
public class ExportExcelConfig {

    /**
     * 使用多线程进行Excel的下载本地，或者直接通过浏览器下载
     * @param exportExcelProperties
     * @return
     */
    @Bean
    public TaskExecutor exportExcelTaskExecutor(ExportExcelProperties exportExcelProperties) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(exportExcelProperties.getCorePoolSize());
        threadPoolTaskExecutor.setQueueCapacity(exportExcelProperties.getQueueCapacity());
        threadPoolTaskExecutor.setKeepAliveSeconds(exportExcelProperties.getKeepAlive());
        threadPoolTaskExecutor.setMaxPoolSize(exportExcelProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setThreadNamePrefix(exportExcelProperties.getPrefix());
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
