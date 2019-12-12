package com.viewshine.exportexcel.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Configuration
public class ExportExcelConfig {

    @Bean
    @ConditionalOnBean(value = ExportExcelProperties.class)
    public TaskExecutor taskExecutor(ExportExcelProperties exportExcelProperties) {
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
