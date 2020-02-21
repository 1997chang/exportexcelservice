package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
public class MultiDataSourceRouting extends AbstractRoutingDataSource {

    private final static Logger logger = LoggerFactory.getLogger(MultiDataSourceRouting.class);

    private final MapDataSourceLookup dataSourceLookup= new MapDataSourceLookup();

    public MultiDataSourceRouting(@NonNull Map<Object, Object> multiDataSource) {
        setTargetDataSources(multiDataSource);
        setDataSourceLookup(dataSourceLookup);
        setLenientFallback(false);
//        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String activeDataSource = DataSourceNameHolder.getActiveDataSource();
        logger.info("【{}】线程准备切换到【{}】数据源", Thread.currentThread().getName(), activeDataSource);
        DataSourceNameHolder.clearDataSource();
        return activeDataSource;
    }

    /**
     * TODO 表示添加一个新的数据库
     */
    public void addNewDataSOurce() {
        afterPropertiesSet();
    }

    /**
     * TODO 强制添加一个数据源
     */
    public void addForceDataSource() {
        afterPropertiesSet();
    }

    /**
     * TODO 表示清除一个数据源
     */
    public void cleanDataSource() {
        afterPropertiesSet();
    }
}
