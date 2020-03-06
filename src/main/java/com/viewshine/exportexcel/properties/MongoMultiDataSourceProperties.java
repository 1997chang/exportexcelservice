package com.viewshine.exportexcel.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置Mongo数据库的，以及连接的线程池参数
 * @author changWei[changwei@viewshine.cn]
 */
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Profile(value = "mongosource")
@Data
public class MongoMultiDataSourceProperties {

    private Map<String, MongoProperties> multi = new LinkedHashMap<>();

    /**
     * 设置MongoDB的连接池信息
     */
    private MongoPoolProperties pool = new MongoPoolProperties();
}
