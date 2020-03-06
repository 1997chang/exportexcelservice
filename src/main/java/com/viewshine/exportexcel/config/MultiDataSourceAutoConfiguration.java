package com.viewshine.exportexcel.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.viewshine.exportexcel.datasource.MongoDataSourceRouting;
import com.viewshine.exportexcel.datasource.MysqlDataSourceRouting;
import com.viewshine.exportexcel.properties.MongoMultiDataSourceProperties;
import com.viewshine.exportexcel.properties.MongoPoolProperties;
import com.viewshine.exportexcel.properties.MysqlMultiDataSourceProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 创建我们自己的数据源
 * @author ChangWei[changwei@viewshine.cn]
 */
public class MultiDataSourceAutoConfiguration {

    /**
     * MySQL的动态路由对象，必须继承AbstractRoutingDataSource类
     * @param mysqlMultiDataSourceProperties MySQL的连接属性
     * @return
     */
    @Primary
    @Bean
    public MysqlDataSourceRouting multiDataSource(MysqlMultiDataSourceProperties mysqlMultiDataSourceProperties) {
        mysqlMultiDataSourceProperties.getMulti().forEach((key, value) -> {
            try {
                value.addFilters("wall,stat");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Map<Object, Object> dataSourceMap = new HashMap<>();
        mysqlMultiDataSourceProperties.getMulti().forEach(dataSourceMap::put);
        return new MysqlDataSourceRouting(dataSourceMap);
    }

    /**
     * 构建MongoDB的连接数据源
     * @param mongoMultiDataSourceProperties MongoDB数据库的连接属性
     * @return 返回MongoDB数据库的连接对象
     */
    @Bean
    public MongoDataSourceRouting mongoDataSource(MongoMultiDataSourceProperties mongoMultiDataSourceProperties) {
        MongoDataSourceRouting mongoDataSource = new MongoDataSourceRouting();
        MongoPoolProperties mongoPoolProperties = mongoMultiDataSourceProperties.getPool();
        Map<String, MongoOperations> multiMongoClients = mongoMultiDataSourceProperties.getMulti().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()) && StringUtils.isNotBlank(entry.getValue().getUri()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    MongoProperties mongoProperties = entry.getValue();
                    ConnectionString connectionString = new ConnectionString(mongoProperties.getUri());
                    MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().
                            applyConnectionString(connectionString).
                            applyToConnectionPoolSettings(pool ->
                                    pool.maxSize(mongoPoolProperties.getMaxSize())
                                            .minSize(mongoPoolProperties.getMinSize())
                                            .maintenanceFrequency(mongoPoolProperties.getMaintenanceFrequencyMS(), MILLISECONDS)
                                            .maxWaitTime(mongoPoolProperties.getMaxWaitTimeMS(), MILLISECONDS)
                                            .maintenanceInitialDelay(mongoPoolProperties.getMaintenanceInitialDelayMS(), MILLISECONDS)
                                            .maxConnectionIdleTime(mongoPoolProperties.getMaxConnectionIdleTimeMS(), MILLISECONDS)
                                            .maxWaitQueueSize(mongoPoolProperties.getMaxWaitQueueSize())
                                            .maxConnectionLifeTime(mongoPoolProperties.getMaxConnectionLifeTimeMS(), MILLISECONDS)
                            )
                            .retryReads(true)
                            .build(), null);

                    //首先获取dataBase属性值，如果为空的话获取URI中的数据库的名称，最后使用默认数据库名称
                    String databaseName = Optional.ofNullable(
                            Optional.ofNullable(mongoProperties.getDatabase()).orElse(connectionString.getDatabase()))
                            .orElse("test");
                    return new MongoTemplate(mongoClient, databaseName);
                }));

        mongoDataSource.setDatasources(multiMongoClients);
        return mongoDataSource;
    }

}
