package com.viewshine.exportexcel.datasource;

import lombok.Data;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author changWei[changwei@viewshine.cn]
 */
@Data
public class MongoDataSourceRouting {

    /**
     * 表示MongoDB的数据库连接对象
     */
    Map<String, MongoOperations> datasources = new HashMap<>(8);

}
