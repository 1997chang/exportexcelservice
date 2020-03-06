package com.viewshine.exportexcel.properties;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置MySQL的的数据源信息
 * @author ChangWei[changwei@viewshine.cn]
 */
@ConfigurationProperties(prefix = "spring.datasource.excel")
@Data
@Profile(value = "multidatasource")
public class MysqlMultiDataSourceProperties {

    /**
     * 表示多个数据源的
     */
    private Map<String, DruidDataSource> multi = new LinkedHashMap<>();
}
