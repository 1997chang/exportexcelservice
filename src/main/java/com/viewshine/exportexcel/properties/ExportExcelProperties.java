package com.viewshine.exportexcel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@ConfigurationProperties(prefix = "export.excel.service.threadpool")
@Data
public class ExportExcelProperties {

    /**
     * 导出Excel的线程池中核心线程的个数
     */
    private Integer corePoolSize;

    /**
     * 导出Excel的线程池中最大线程个数
     */
    private Integer maxPoolSize;

    /**
     * 每一个线程对应的队列大小
     */
    private Integer queueCapacity;

    /**
     * 每一个线程保留多少秒
     */
    private Integer keepAlive;

    /**
     * 这个导出Excel线程池的前缀，便于查看时那个线程池数据
     */
    private String prefix;
}
