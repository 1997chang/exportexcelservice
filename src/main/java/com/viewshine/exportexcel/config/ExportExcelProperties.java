package com.viewshine.exportexcel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Component
@ConfigurationProperties(prefix = "export.excel.service.threadpool")
@Data
public class ExportExcelProperties {

    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer queueCapacity;

    private Integer keepAlive;

    private String prefix;
}
