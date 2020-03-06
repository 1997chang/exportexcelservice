package com.viewshine.exportexcel.datasource;

import com.viewshine.exportexcel.utils.DataSourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * MYSQL的数据源动态路由
 * @author ChangWei[changwei@viewshine.cn]
 */
public class MysqlDataSourceRouting extends AbstractRoutingDataSource {

    private final static Logger logger = LoggerFactory.getLogger(MysqlDataSourceRouting.class);

    private final MapDataSourceLookup dataSourceLookup = new MapDataSourceLookup();

    public MysqlDataSourceRouting(@NonNull Map<Object, Object> multiDataSource) {
        setTargetDataSources(multiDataSource);
        setDataSourceLookup(dataSourceLookup);
        setLenientFallback(false);
//        afterPropertiesSet();
    }

    /**
     * 最终确定选择的数据源的名称，也就是Map中的Key的值，从而获对应的Value值，也就是对应的DataSource
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String activeDataSource = DataSourceHolder.getActiveDataSourceName();
        logger.info("【{}】线程准备切换到【{}】数据源", Thread.currentThread().getName(), activeDataSource);
        DataSourceHolder.clearDataSourceName();
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
