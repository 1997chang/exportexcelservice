package com.viewshine.exportexcel.properties;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DEFAULT_DATASOURCE_NAME;

/**
 * 这个表示数据库名称选择器，每个线程过这个进行选择具体的数据库
 * @author ChangWei[changwei@viewshine.cn]
 */
public class DataSourceNameHolder {

    private final static ThreadLocal<String> dataSourceName = ThreadLocal.withInitial(() -> DEFAULT_DATASOURCE_NAME);

    public static String getActiveDataSource() {
        return dataSourceName.get();
    }

    public static void setActiveDataSource(String activeDataSource) {
        dataSourceName.set(activeDataSource);
    }

    public static void clearDataSource() {
        dataSourceName.remove();
    }
}
