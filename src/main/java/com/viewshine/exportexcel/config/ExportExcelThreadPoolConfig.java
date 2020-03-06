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
     * 当收到一个请求的时候，开启一个线程从数据源中获取数据，然后保存到本地磁盘上
     * @param exportExcelProperties 导出Excel属性
     * @return 线程池对象
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
        return threadPoolTaskExecutor;
    }

    /**
     * 使用线程池计划任务完成下载文件的删除任务。
     * @return 计划任务线程池
     */
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
        threadPoolTaskScheduler.setThreadNamePrefix("deleteExcel");
        return threadPoolTaskScheduler;
    }

}
