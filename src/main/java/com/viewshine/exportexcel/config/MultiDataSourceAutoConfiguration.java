package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.properties.MultiDataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建我们自己的数据源
 * @author ChangWei[changwei@viewshine.cn]
 */
public class MultiDataSourceAutoConfiguration {

    @Primary
    @Bean
    public MultiDataSourceRouting multiDataSource(MultiDataSourceProperties multiDataSourceProperties) {
        multiDataSourceProperties.getMulti().forEach((key, value) -> {
            try {
                value.addFilters("wall,stat");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Map<Object, Object> dataSourceMap = new HashMap<>();
        multiDataSourceProperties.getMulti().forEach(dataSourceMap::put);
        return new MultiDataSourceRouting(dataSourceMap);
    }

}
