package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.properties.ExportExcelProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Configuration
public class ExportExcelThreadPoolConfig {

    private final static Logger logger = LoggerFactory.getLogger(ExportExcelThreadPoolConfig.class);

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
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public TaskScheduler deleteExcelScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setErrorHandler(e -> {
            logger.error("执行计划任务失败。。。");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        });
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(300);
        threadPoolTaskScheduler.setThreadNamePrefix("deleteE");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

}
